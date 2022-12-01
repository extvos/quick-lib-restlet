package plus.extvos.restlet.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import plus.extvos.common.Assert;
import plus.extvos.common.exception.ResultException;
import plus.extvos.restlet.QuerySet;
import plus.extvos.restlet.service.Aggregation;
import plus.extvos.restlet.service.BaseService;
import plus.extvos.restlet.service.QueryBuilder;
import plus.extvos.restlet.utils.DateTrunc;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mingcai SHEN
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T>, QueryBuilder {

    private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);

    protected TableInfo tableInfo;

    public abstract M getMapper();

    /**
     * get the generic type which helps to get table info
     *
     * @return Class&lt;T&gt;
     */
    protected Class<?> getGenericType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[1];
    }

    /**
     * @return bundled table info
     */
    public TableInfo getTableInfo() {
        if (this.tableInfo == null) {
            this.tableInfo = TableInfoHelper.getTableInfo(getGenericType());
        }
        return this.tableInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(@NotNull T entity) throws ResultException {
        int n = 0;
        try {
            n = getMapper().insert(entity);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (n != 1) {
            throw ResultException.internalServerError("insert record failed?");
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(@NotNull List<T> entities) throws ResultException {
        int n;
        try {
            n = entities.stream().mapToInt(entity -> getMapper().insert(entity)).sum();
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (n < 1) {
            throw ResultException.internalServerError("insert record failed?");
        }
        return n;
    }

    /**
     * Replace entity: insert new on missing or update on exists
     *
     * @param entity of object
     * @return inserted of updated num
     * @throws ResultException for failure
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int replace(@NotNull T entity) throws ResultException {
        Class<?> c = entity.getClass();
        try {
            Field f = c.getDeclaredField(tableInfo.getKeyColumn());
            f.setAccessible(true);
            Serializable id = (Serializable) f.get(entity);
            if (null != id) {
                QueryWrapper<T> qw = new QueryWrapper<T>();
                qw = qw.eq(getTableInfo().getKeyColumn(), id);
                int n = getMapper().update(entity, qw);
                if (n > 0) {
                    return n;
                }
            }
            return getMapper().insert(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw ResultException.internalServerError(e.getMessage());
        }
    }

    /**
     * Replace entities: insert new on missing or update on exists
     *
     * @param entities of objects
     * @return inserted of updated num
     * @throws ResultException for failure
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int replace(@NotNull List<T> entities) throws ResultException {
        if (entities.size() < 1) {
            return 0;
        }
        int totalReplaced = 0;
        Class<?> c = entities.get(0).getClass();
        Field f = null;
        try {
            f = c.getDeclaredField(tableInfo.getKeyColumn());
        } catch (NoSuchFieldException e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        f.setAccessible(true);
        for (T entity : entities) {
            try {
                Serializable id = (Serializable) f.get(entity);
                if (null != id) {
                    QueryWrapper<T> qw = new QueryWrapper<T>();
                    qw = qw.eq(getTableInfo().getKeyColumn(), id);
                    int n = getMapper().update(entity, qw);
                    if (n > 0) {
                        totalReplaced += 1;
                        continue;
                    }
                }
                totalReplaced += getMapper().insert(entity);
            } catch (IllegalAccessException e) {
                throw ResultException.internalServerError(e.getMessage());
            }
        }
        return totalReplaced;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(@NotNull Serializable id) throws ResultException {
        int n = 0;
        try {
            n = getMapper().deleteById(id);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (n != 1) {
            throw ResultException.notFound("record of id not found");
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByMap(@NotNull QuerySet<T> querySet) throws ResultException {
        int n = 0;
        try {
            n = getMapper().delete(querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByMap(Map<String, Object> columnMap) throws ResultException {
        return getMapper().deleteByMap(columnMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByWrapper(Wrapper<T> queryWrapper) throws ResultException {
        return getMapper().delete(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@NotNull Collection<? extends Serializable> idList) throws ResultException {
        return getMapper().deleteBatchIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateById(@NotNull Serializable id, @NotNull T entity) throws ResultException {
        int n = 0;
        try {
            QueryWrapper<T> qw = new QueryWrapper<T>();
            qw = qw.eq(getTableInfo().getKeyColumn(), id);
            n = getMapper().update(entity, qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (n != 1) {
            throw ResultException.notFound("record of id not found");
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByMap(@NotNull QuerySet<T> querySet, @NotNull T entity) throws ResultException {
        int n = 0;
        try {
            n = getMapper().update(entity, querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByMap(Map<String, Object> columnMap, @NotNull T entity) throws ResultException {
        QueryWrapper<T> qw = new QueryWrapper<T>();
        if (columnMap != null) {
            columnMap.forEach(qw::eq);
        }
        return getMapper().update(entity, qw);
    }

    private QueryWrapper<T> querySetFinish(@NotNull QuerySet<T> querySet, @NotNull QueryWrapper<T> queryWrapper) {
        if (querySet.getOrderBy() != null && querySet.getOrderBy().size() > 0) {
            QueryWrapper<T> finalQw = queryWrapper;
            querySet.getOrderBy().forEach((String s) -> {
                if (s.startsWith("-")) {
                    finalQw.orderByDesc(s.substring(1));
                } else {
                    finalQw.orderByAsc(s);
                }
            });
        }
        if (querySet.getPageSize() >= 0) {
            queryWrapper = queryWrapper.last("LIMIT " + querySet.getPageSize() + " OFFSET " + (querySet.getPage() * querySet.getPageSize()));
        }
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByWrapper(@NotNull T entity, Wrapper<T> updateWrapper) throws ResultException {
        return getMapper().update(entity, updateWrapper);
    }

    @Override
    public T selectById(@NotNull Serializable id) throws ResultException {
        T obj;
        try {
            QueryWrapper<T> qw = new QueryWrapper<>();
            qw = qw.eq(getTableInfo().getKeyColumn(), id);
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
//        if (null == obj) {
//            throw ResultException.notFound("record of id not found");
//        }
        return obj;

    }

    @Override
    public T selectById(QuerySet<T> querySet, @NotNull Serializable id) throws ResultException {
        T obj;
        try {
            QueryWrapper<T> qw = new QueryWrapper<>();
            if (querySet != null) {
                qw = qw.select(querySet.columns().toArray(new String[0]));
            }
            qw = qw.eq(getTableInfo().getKeyColumn(), id);
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
//        if (null == obj) {
//            throw ResultException.notFound("record of id not found");
//        }
        return obj;

    }

    @Override
    public List<T> selectByMap(@NotNull QuerySet<T> querySet) throws ResultException {
        List<T> objs;
        try {
            QueryWrapper<T> qw = querySet.buildQueryWrapper(this).clone();
            qw = qw.select(querySet.columns().toArray(new String[0]));
            qw = querySetFinish(querySet, qw);
            objs = getMapper().selectList(qw);
        } catch (Exception e) {
            log.error(">>", e);
            throw ResultException.internalServerError(e.getMessage());
        }
        return objs;
    }

    @Override
    public List<T> selectByMap(Map<String, Object> columnMap) throws ResultException {
        return getMapper().selectByMap(columnMap);
    }

    @Override
    public List<T> selectByWrapper(@NotNull Wrapper<T> queryWrapper) throws ResultException {
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public Long countByMap(@NotNull QuerySet<T> querySet) throws ResultException {
        long n = 0L;
        try {
            n = (long) getMapper().selectCount(querySet.buildQueryWrapper(this));
        } catch (ResultException e) {
            throw e;
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    public Map<Object, Long> countByMap(String fieldName, QuerySet<T> querySet) throws ResultException {
        QueryWrapper<T> qw = querySet.buildQueryWrapper(this);
        qw.select(fieldName, "COUNT(1) AS count");
        qw.groupBy(fieldName);
        qw = querySetFinish(querySet, qw);
        List<Map<String, Object>> rows = getMapper().selectMaps(qw);
        Map<Object, Long> result = new HashMap<>(rows.size());
        rows.forEach(r -> {
                    result.put(r.get(fieldName) == null ? "" : r.get(fieldName), (Long) r.get("count"));
                }
        );
        return result;
    }

    @Override
    public Map<Object, Long> countByWrapper(String fieldName, Wrapper<T> queryWrapper) throws ResultException {
        QueryWrapper<T> qw = (QueryWrapper<T>) queryWrapper;
        qw.select(fieldName, "COUNT(1)");
        qw.groupBy(fieldName);
        List<Map<String, Object>> rows = getMapper().selectMaps(qw);
        Map<Object, Long> result = new HashMap<>(rows.size());
        rows.forEach(r -> {
                    result.put(r.get(fieldName) == null ? "" : r.get(fieldName), (Long) r.get("count"));
                }
        );
        return result;
    }

    @Override
    public Long countByWrapper(@NotNull Wrapper<T> queryWrapper) throws ResultException {
        return (long) getMapper().selectCount(queryWrapper);
    }


    @Override
    public List<Map<String, Object>> aggregateByMap(String fieldName, QuerySet<T> querySet, Aggregation... aggregations) throws ResultException {
        QueryWrapper<T> qw = querySet.buildQueryWrapper(this);
        String[] fields = fieldName.split(",");
        List<String> ls = new LinkedList<>(Arrays.asList(fields));
        if (aggregations.length < 1) {
            ls.add("COUNT(1) AS count");
        } else {
            ls.addAll(Arrays.stream(aggregations).map(Aggregation::expression).collect(Collectors.toList()));
        }
        qw.select(ls.toArray(new String[0]));
        qw.groupBy(fields);
        qw = querySetFinish(querySet, qw);
        return getMapper().selectMaps(qw);
    }

    // Only adapt to pg
    @Override
    public List<Map<String, Object>> trendByMap(String fieldName, String interval, String[] groupBy, QuerySet<T> querySet, Aggregation... aggregations) throws ResultException {
        QueryWrapper<T> qw = querySet.buildQueryWrapper(this);
        Assert.isTrue(DateTrunc.validate(interval), ResultException.badRequest());
        List<String> ls = new LinkedList<>();
        List<String> groups = new LinkedList<>();
        ls.add("date_trunc('" + interval + "', " + fieldName + ") as " + fieldName);
        groups.add("date_trunc('" + interval + "', " + fieldName + ")");
        if (null != groupBy && groupBy.length > 0) {
            ls.addAll(Arrays.asList(groupBy));
            groups.addAll(Arrays.asList(groupBy));
        }
        if (aggregations.length < 1) {
            ls.add("COUNT(1) AS count");
        } else {
            ls.addAll(Arrays.stream(aggregations).map(Aggregation::expression).collect(Collectors.toList()));
        }
        qw.select(ls.toArray(new String[0]));
        qw.groupBy(groups.toArray(new String[0]));
        return getMapper().selectMaps(qw);
    }

//    @Override
//    public List<Map<String, Object>> trendTimeline(String interval, Timestamp begin, Timestamp end) throws ResultException{
//        getMapper().
//    }

    @Override
    public T selectOne(@NotNull QuerySet<T> querySet) throws ResultException {
        T obj;
        try {
            QueryWrapper<T> qw = querySet.buildQueryWrapper(this).clone();
            qw = qw.select(querySet.columns().toArray(new String[0]));
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
//        if (null == obj) {
//            throw ResultException.notFound("record not found");
//        }
        return obj;
    }

    @Override
    public T selectOne(@NotNull Wrapper<T> queryWrapper) throws ResultException {
        return getMapper().selectOne(queryWrapper);
    }

    @Override
    public boolean parseQuery(String k, Object v, QueryWrapper<?> wrapper) {
        return false;
    }
}
