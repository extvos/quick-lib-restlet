package plus.extvos.restlet.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import plus.extvos.restlet.QuerySet;
import plus.extvos.common.exception.ResultException;

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
     * @throws ResultException for failures
     */
    int insert(T entity) throws ResultException;

    /**
     * Insert multiple entities
     *
     * @param entities list of objects
     * @return inserted num
     * @throws ResultException for failures
     */
    int insert(List<T> entities) throws ResultException;

    /**
     * Replace entity: insert new on missing or update on exists
     * @param entity of object
     * @return inserted of updated num
     * @throws ResultException for failure
     */
    int replace(T entity) throws ResultException;


    /**
     * Replace entities: insert new on missing or update on exists
     * @param entities of objects
     * @return inserted of updated num
     * @throws ResultException for failure
     */
    int replace(List<T> entities) throws ResultException;

    /**
     * Delete by id
     *
     * @param id as pk
     * @return deleted num
     * @throws ResultException for failures
     */
    int deleteById(Serializable id) throws ResultException;

    /**
     * Delete by query map
     *
     * @param querySet of queries
     * @return deleted num
     * @throws ResultException for failures
     */
    int deleteByMap(QuerySet<T> querySet) throws ResultException;

    /**
     * Delete by map
     *
     * @param columnMap of query
     * @return num of deleted
     * @throws ResultException if error
     */
    int deleteByMap(Map<String, Object> columnMap) throws ResultException;

    /**
     * Delete by wrapper
     *
     * @param queryWrapper of query
     * @return num of deleted
     * @throws ResultException if error
     */
    int deleteByWrapper(Wrapper<T> queryWrapper) throws ResultException;

    /**
     * Delete by ids,
     *
     * @param idList of id
     * @return num of deleted
     * @throws ResultException if error
     */
    int deleteByIds(Collection<? extends Serializable> idList) throws ResultException;

    /**
     * update by id
     *
     * @param id     as pk
     * @param entity of object
     * @return updated num
     * @throws ResultException for failures
     */
    int updateById(Serializable id, T entity) throws ResultException;

    /**
     * update by query map
     *
     * @param querySet as queries
     * @param entity   update fields
     * @return updated num
     * @throws ResultException for failures
     */
    int updateByMap(QuerySet<T> querySet, T entity) throws ResultException;


    /**
     * Update by map
     *
     * @param columnMap of query
     * @param entity    of object
     * @return num ofupdated
     * @throws ResultException when error
     */
    int updateByMap(Map<String, Object> columnMap, T entity) throws ResultException;


    /**
     * Update by wrapper
     *
     * @param entity        object
     * @param updateWrapper of query
     * @return updated records
     * @throws ResultException if error
     */
    int updateByWrapper(T entity, Wrapper<T> updateWrapper) throws ResultException;

    /**
     * select by id
     *
     * @param querySet as queries
     * @param id       as pk
     * @return entity
     * @throws ResultException for failures
     */
    T selectById(QuerySet<T> querySet, Serializable id) throws ResultException;


    /**
     * select by id
     *
     * @param id       as pk
     * @return entity
     * @throws ResultException for failures
     */
    T selectById(Serializable id) throws ResultException;

    /**
     * select by queries
     *
     * @param querySet of queries
     * @return list of entity
     * @throws ResultException for failures
     */
    List<T> selectByMap(QuerySet<T> querySet) throws ResultException;

    /**
     * Select by map
     *
     * @param columnMap of query
     * @return list
     * @throws ResultException for failures
     */
    List<T> selectByMap(Map<String, Object> columnMap) throws ResultException;


    /**
     * Select by wrapper
     *
     * @param queryWrapper pf query
     * @return list
     * @throws ResultException for failures
     */
    List<T> selectByWrapper(Wrapper<T> queryWrapper) throws ResultException;

    /**
     * get count of by queries
     *
     * @param querySet of queries
     * @return num
     * @throws ResultException for failures
     */
    Long countByMap(QuerySet<T> querySet) throws ResultException;

    /**
     * Count by wrapper
     *
     * @param queryWrapper of query
     * @return num
     * @throws ResultException for failures
     */
    Long countByWrapper(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) throws ResultException;

    /**
     * select one entity by queries
     *
     * @param querySet of queries
     * @return an entity
     * @throws ResultException for failures
     */
    T selectOne(QuerySet<T> querySet) throws ResultException;

    /**
     * Select one by wrapper
     *
     * @param queryWrapper of query
     * @return null of object
     * @throws ResultException for failures
     */
    T selectOne(Wrapper<T> queryWrapper) throws ResultException;

}
