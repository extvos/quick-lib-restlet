package org.extvos.restlet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.extvos.restlet.QuerySet;
import org.extvos.restlet.exception.RestletException;
import org.extvos.restlet.service.BaseService;
import org.extvos.restlet.service.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mingcai SHEN
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T>, QueryBuilder {

    private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);


    public abstract M getMapper();

    @Override
    public int insert(T entity) throws RestletException {
        int n = 0;
        try {
            n = getMapper().insert(entity);
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        if (n != 1) {
            throw RestletException.internalServerError("insert record failed?");
        }
        return n;
    }

    @Override
    public int insert(List<T> entities) throws RestletException {
        int n;
        try {
            n = entities.stream().mapToInt(entity -> getMapper().insert(entity)).sum();

        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        if (n < 1) {
            throw RestletException.internalServerError("insert record failed?");
        }
        return n;
    }

    @Override
    public int deleteById(Serializable id) throws RestletException {
        int n = 0;
        try {
            n = getMapper().deleteById(id);
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        if (n != 1) {
            throw RestletException.notFound("record of id not found");
        }
        return n;
    }

    @Override
    public int deleteByMap(QuerySet<T> querySet) throws RestletException {
        int n = 0;
        try {
            n = getMapper().delete(querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    public int updateById(Serializable id, T entity) throws RestletException {
        int n = 0;
        try {
            QueryWrapper<T> qw = new QueryWrapper<T>();
            qw = qw.eq("id", id);
            n = getMapper().update(entity, qw);
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        if (n != 1) {
            throw RestletException.notFound("record of id not found");
        }
        return n;
    }

    @Override
    public int updateByMap(QuerySet<T> querySet, T entity) throws RestletException {
        int n = 0;
        try {
            n = getMapper().update(entity, querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    public T selectById(QuerySet<T> querySet, Serializable id) throws RestletException {
        T obj;
        try {
            QueryWrapper<T> qw = new QueryWrapper<>();
            if (querySet != null) {
                qw = qw.select(querySet.columns().toArray(new String[0]));
            }
            qw = qw.eq("id", id);
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        if (null == obj) {
            throw RestletException.notFound("record of id not found");
        }
        return obj;

    }

    @Override
    public List<T> selectByMap(QuerySet<T> querySet) throws RestletException {
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
            qw = qw.last("LIMIT " + querySet.getPageSize() + " OFFSET " + (querySet.getPage() * querySet.getPageSize()));
            objs = getMapper().selectList(qw);
        } catch (Exception e) {
            log.error(">>", e);
            throw RestletException.internalServerError(e.getMessage());
        }
        return objs;
    }

    @Override
    public Long countByMap(QuerySet<T> querySet) throws RestletException {
        long n = 0L;
        try {
            n = (long) getMapper().selectCount(querySet.buildQueryWrapper(this));
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        return n;
    }

    @Override
    public T selectOne(QuerySet<T> querySet) throws RestletException {
        T obj;
        try {
            QueryWrapper<T> qw = querySet.buildQueryWrapper(this).clone();
            qw = qw.select(querySet.columns().toArray(new String[0]));
            obj = getMapper().selectOne(qw);
        } catch (Exception e) {
            throw RestletException.internalServerError(e.getMessage());
        }
        if (null == obj) {
            throw RestletException.notFound("record not found");
        }
        return obj;
    }

    @Override
    public boolean parseQuery(String k, Object v, QueryWrapper<?> wrapper) {
        return false;
    }
}
