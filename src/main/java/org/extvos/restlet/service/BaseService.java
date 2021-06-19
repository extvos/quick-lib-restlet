package org.extvos.restlet.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.extvos.restlet.QuerySet;
import org.extvos.restlet.exception.RestletException;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Mingcai SHEN
 */
public interface BaseService<T> {
    /**
     * Insert a new entity
     *
     * @param entity object
     * @return inserted num
     * @throws RestletException for failures
     */
    int insert(T entity) throws RestletException;

    /**
     * Insert multiple entities
     *
     * @param entities list of objects
     * @return inserted num
     * @throws RestletException for failures
     */
    int insert(List<T> entities) throws RestletException;

    /**
     * Delete by id
     *
     * @param id as pk
     * @return deleted num
     * @throws RestletException for failures
     */
    int deleteById(Serializable id) throws RestletException;

    /**
     * Delete by query map
     *
     * @param querySet of queries
     * @return deleted num
     * @throws RestletException for failures
     */
    int deleteByMap(QuerySet<T> querySet) throws RestletException;

    /**
     * Delete by map
     *
     * @param columnMap of query
     * @return num of deleted
     * @throws RestletException if error
     */
    int deleteByMap(Map<String, Object> columnMap) throws RestletException;

    /**
     * Delete by wrapper
     *
     * @param queryWrapper of query
     * @return num of deleted
     * @throws RestletException if error
     */
    int deleteByWrapper(Wrapper<T> queryWrapper) throws RestletException;

    /**
     * Delete by ids,
     *
     * @param idList of id
     * @return num of deleted
     * @throws RestletException if error
     */
    int deleteByIds(Collection<? extends Serializable> idList) throws RestletException;

    /**
     * update by id
     *
     * @param id     as pk
     * @param entity of object
     * @return updated num
     * @throws RestletException for failures
     */
    int updateById(Serializable id, T entity) throws RestletException;

    /**
     * update by query map
     *
     * @param querySet as queries
     * @param entity   update fields
     * @return updated num
     * @throws RestletException for failures
     */
    int updateByMap(QuerySet<T> querySet, T entity) throws RestletException;


    /**
     * Update by map
     *
     * @param columnMap of query
     * @param entity    of object
     * @return num ofupdated
     * @throws RestletException when error
     */
    int updateByMap(Map<String, Object> columnMap, T entity) throws RestletException;


    /**
     * Update by wrapper
     *
     * @param entity        object
     * @param updateWrapper of query
     * @return updated records
     * @throws RestletException if error
     */
    int updateByWrapper(T entity, Wrapper<T> updateWrapper) throws RestletException;

    /**
     * select by id
     *
     * @param querySet as queries
     * @param id       as pk
     * @return entity
     * @throws RestletException for failures
     */
    T selectById(QuerySet<T> querySet, Serializable id) throws RestletException;

    /**
     * select by queries
     *
     * @param querySet of queries
     * @return list of entity
     * @throws RestletException for failures
     */
    List<T> selectByMap(QuerySet<T> querySet) throws RestletException;

    /**
     * Select by map
     *
     * @param columnMap of query
     * @return list
     * @throws RestletException for failures
     */
    List<T> selectByMap(Map<String, Object> columnMap) throws RestletException;


    /**
     * Select by wrapper
     *
     * @param queryWrapper pf query
     * @return list
     * @throws RestletException for failures
     */
    List<T> selectByWrapper(Wrapper<T> queryWrapper) throws RestletException;

    /**
     * get count of by queries
     *
     * @param querySet of queries
     * @return num
     * @throws RestletException for failures
     */
    Long countByMap(QuerySet<T> querySet) throws RestletException;

    /**
     * Count by wrapper
     *
     * @param queryWrapper of query
     * @return num
     * @throws RestletException for failures
     */
    Long countByWrapper(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) throws RestletException;

    /**
     * select one entity by queries
     *
     * @param querySet of queries
     * @return an entity
     * @throws RestletException for failures
     */
    T selectOne(QuerySet<T> querySet) throws RestletException;

    /**
     * Select one by wrapper
     *
     * @param queryWrapper of query
     * @return null of object
     * @throws RestletException for failures
     */
    T selectOne(Wrapper<T> queryWrapper) throws RestletException;

}
