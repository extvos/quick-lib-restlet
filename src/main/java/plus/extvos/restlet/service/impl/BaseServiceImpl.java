package plus.extvos.restlet.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.extvos.restlet.QuerySet;
import plus.extvos.common.exception.ResultException;
import plus.extvos.restlet.service.BaseService;
import plus.extvos.restlet.service.QueryBuilder;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * @return Class<T>
     */
    protected Class<T> getGenericType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
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
    public int insert(T entity) throws ResultException {
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
    public int insert(List<T> entities) throws ResultException {
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

    @Override
    public int deleteById(Serializable id) throws ResultException {
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
    public int deleteByMap(QuerySet<T> querySet) throws ResultException {
        int n = 0;
        try {
            n = getMapper().delete(querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    public int deleteByMap(Map<String, Object> columnMap) throws ResultException {
        return getMapper().deleteByMap(columnMap);
    }

    @Override
    public int deleteByWrapper(Wrapper<T> queryWrapper) throws ResultException {
        return getMapper().delete(queryWrapper);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> idList) throws ResultException {
        return getMapper().deleteBatchIds(idList);
    }

    @Override
    public int updateById(Serializable id, T entity) throws ResultException {
        int n = 0;
        try {
            QueryWrapper<T> qw = new QueryWrapper<T>();
//            qw = qw.eq("id", id);
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
    public int updateByMap(QuerySet<T> querySet, T entity) throws ResultException {
        int n = 0;
        try {
            n = getMapper().update(entity, querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    public int updateByMap(Map<String, Object> columnMap, T entity) throws ResultException {
        QueryWrapper<T> qw = new QueryWrapper<T>();
        if (columnMap != null) {
            columnMap.forEach(qw::eq);
        }
        return getMapper().update(entity, qw);
    }

    @Override
    public int updateByWrapper(T entity, Wrapper<T> updateWrapper) throws ResultException {
        return getMapper().update(entity, updateWrapper);
    }

    @Override
    public T selectById(Serializable id) throws ResultException {
        T obj;
        try {
            QueryWrapper<T> qw = new QueryWrapper<>();
//            qw = qw.eq("id", id);
            qw = qw.eq(getTableInfo().getKeyColumn(), id);
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (null == obj) {
            throw ResultException.notFound("record of id not found");
        }
        return obj;

    }

    @Override
    public T selectById(QuerySet<T> querySet, Serializable id) throws ResultException {
        T obj;
        try {
            QueryWrapper<T> qw = new QueryWrapper<>();
            if (querySet != null) {
                qw = qw.select(querySet.columns().toArray(new String[0]));
            }
//            qw = qw.eq("id", id);
            qw = qw.eq(getTableInfo().getKeyColumn(), id);
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (null == obj) {
            throw ResultException.notFound("record of id not found");
        }
        return obj;

    }

    @Override
    public List<T> selectByMap(QuerySet<T> querySet) throws ResultException {
        List<T> objs;
        try {
            QueryWrapper<T> qw = querySet.buildQueryWrapper(this).clone();
            qw = qw.select(querySet.columns().toArray(new String[0]));
            if (querySet.getOrderBy() != null && querySet.getOrderBy().size() > 0) {
                QueryWrapper<T> finalQw = qw;
                querySet.getOrderBy().forEach((String s) -> {
                    if (s.startsWith("-")) {
                        finalQw.orderByDesc(s.substring(1));
                    } else {
                        finalQw.orderByAsc(s);
                    }
                });
            }
            if (querySet.getPageSize() >= 0) {
                qw = qw.last("LIMIT " + querySet.getPageSize() + " OFFSET " + (querySet.getPage() * querySet.getPageSize()));
            }
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
    public List<T> selectByWrapper(Wrapper<T> queryWrapper) throws ResultException {
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public Long countByMap(QuerySet<T> querySet) throws ResultException {
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
    public Long countByWrapper(Wrapper<T> queryWrapper) throws ResultException {
        return (long) getMapper().selectCount(queryWrapper);
    }

    @Override
    public T selectOne(QuerySet<T> querySet) throws ResultException {
        T obj;
        try {
            QueryWrapper<T> qw = querySet.buildQueryWrapper(this).clone();
            qw = qw.select(querySet.columns().toArray(new String[0]));
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw ResultException.internalServerError(e.getMessage());
        }
        if (null == obj) {
            throw ResultException.notFound("record not found");
        }
        return obj;
    }

    @Override
    public T selectOne(Wrapper<T> queryWrapper) throws ResultException {
        return getMapper().selectOne(queryWrapper);
    }

    @Override
    public boolean parseQuery(String k, Object v, QueryWrapper<?> wrapper) {
        return false;
    }
}
