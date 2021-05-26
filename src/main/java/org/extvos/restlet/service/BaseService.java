package org.extvos.restlet.service;

import org.extvos.restlet.QuerySet;
import org.extvos.restlet.exception.RestletException;

import java.io.Serializable;
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
     * get count of by queries
     *
     * @param querySet of queries
     * @return num
     * @throws RestletException for failures
     */
    Long countByMap(QuerySet<T> querySet) throws RestletException;

    /**
     * select one entity by queries
     *
     * @param querySet of queries
     * @return an entity
     * @throws RestletException for failures
     */
    T selectOne(QuerySet<T> querySet) throws RestletException;

}
