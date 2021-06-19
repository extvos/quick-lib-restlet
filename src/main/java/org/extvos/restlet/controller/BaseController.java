package org.extvos.restlet.controller;

import org.extvos.common.Validator;
import org.extvos.common.utils.PrimitiveConvert;
import org.extvos.restlet.QuerySet;
import org.extvos.restlet.RestletCode;
import org.extvos.restlet.Result;
import org.extvos.restlet.config.RestletConfig;
import org.extvos.restlet.exception.RestletException;
import org.extvos.restlet.service.BaseService;
import org.extvos.restlet.utils.SpringContextHolder;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Mingcai SHEN
 * BaseController, a basic RESTful controller provice CURD operations
 */
public abstract class BaseController<T, S extends BaseService<T>> extends BaseROController<T, S> {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @ApiOperation(value = "插入一条新记录", notes = "查询条件组织，请参考： https://gitlab.inodes.cn/quickstart/java-scaffolds/quick-lib-restlet/blob/develop/README.md")
    @PostMapping()
    @Transactional(rollbackFor = Exception.class)
    public final Result<T> insertNew(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @RequestBody T record) throws RestletException {
        log.debug("insertNew:> {}, {}", pathMap, record);
        record = preInsert(record);
        if (updatedCols(record) <= 0) {
            throw RestletException.badRequest("empty values for all fields is not allowed");
        }
        if (Validator.notEmpty(pathMap)) {
            for (String k : pathMap.keySet()) {
                try {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(record.getClass(), k);
                    if (null == pd) {
                        continue;
                    }
                    pd.getWriteMethod().invoke(record, PrimitiveConvert.from(pathMap.get(k).toString()).to(pd.getPropertyType()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        int n = getService().insert(record);
        postInsert(record);
        Result<T> ret = Result.data(record).success(RestletCode.CREATED);
        ret.setCount((long) n);
        return ret;
    }

    private int updatedCols(T record) {
        int updatedCols = 0;
        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(record.getClass())) {
            if ("class".equals(pd.getName())) {
                continue;
            }
            if (pd.getPropertyType().isPrimitive()) {
//                updatedCols += 1;
                throw RestletException.notImplemented("primitive property '" + pd.getName() + "' for entity is not allowed");
//                continue;
            }
            try {
                Object o = pd.getReadMethod().invoke(record);
                if (null != o) {
                    updatedCols += 1;
                }
            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }
        }
        return updatedCols;
    }

    @ApiOperation(value = "按条件更新记录", notes = "查询条件组织，请参考： https://gitlab.inodes.cn/quickstart/java-scaffolds/quick-lib-restlet/blob/develop/README.md")
    @PutMapping(value = {"","/{id:[0-9]+}"})
    @Transactional(rollbackFor = Exception.class)
    public final Result<T> updateByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap,
            @RequestBody T record) throws RestletException {
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        if (updatedCols(record) <= 0) {
            throw RestletException.badRequest("no field to update");
        }
        int updated = 0;
        if (pathMap != null && pathMap.containsKey("id")) {
            Serializable id = pathMap.get("id").toString();
            record = preUpdate(id, record);
            updated = getService().updateById(id, record);
            postUpdate(id, record);
        } else {
            record = preUpdate(qs, record);
            if (Validator.notEmpty(pathMap)) {
                for (String k : pathMap.keySet()) {
                    try {
                        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(record.getClass(), k);
                        if (null == pd) {
                            continue;
                        }
                        if (pd.getPropertyType().isPrimitive()) {
                            // TODO: we are not able to determine primitive types by null, so ...
                            continue;
                        }
                        if (null != pd.getReadMethod().invoke(record)) {
                            throw RestletException.forbidden("not allowed to update '" + k + "'");
                        }
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }
            }
            updated = getService().updateByMap(qs, record);
            postUpdate(qs, record);
        }
        Result<T> ret = Result.data(record).success();
        ret.setCount((long) updated);
        return ret;
    }

//    @ApiOperation(value = "按{id}更新记录", notes = "查询条件组织，请参考： https://gitlab.inodes.cn/quickstart/java-scaffolds/quick-lib-restlet/blob/develop/README.md")
//    @PutMapping("/{id:[0-9]+}")
//    @Transactional(rollbackFor = Exception.class)
//    public final Result<T> updateById(
//            @PathVariable Serializable id,
//            @RequestBody T record) throws RestletException {
//        if (updatedCols(record) <= 0) {
//            throw RestletException.badRequest("no field to update");
//        }
//        record = preUpdate(id, record);
//        int n = getService().updateById(id, record);
//        postUpdate(id, record);
//        Result<T> ret = Result.data(record).success();
//        ret.setCount((long) n);
//        return ret;
//    }

    @ApiOperation(value = "按条件删除记录", notes = "查询条件组织，请参考： https://gitlab.inodes.cn/quickstart/java-scaffolds/quick-lib-restlet/blob/develop/README.md")
    @DeleteMapping(value = {"", "/{id:[0-9]+}"})
    @Transactional(rollbackFor = Exception.class)
    public final Result<Integer> deleteByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws RestletException {
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        int deleted = 0;
        if (pathMap != null && pathMap.containsKey("id")) {
            Serializable id = pathMap.get("id").toString();
            preDelete(id);
            deleted = getService().deleteById(id);
            postDelete(id);
        } else {
            qs = preDelete(qs);
            deleted = getService().deleteByMap(qs);
            postDelete(qs);
        }

        RestletConfig config = SpringContextHolder.getBean(RestletConfig.class);
        if (config.isDeleteResponseBody()) {
            return Result.data(deleted).success(RestletCode.OK);
        } else {
            return Result.data(deleted).success(RestletCode.NO_CONTENT);
        }
    }

//    @ApiOperation(value = "按{id}删除记录", notes = "查询条件组织，请参考： https://gitlab.inodes.cn/quickstart/java-scaffolds/quick-lib-restlet/blob/develop/README.md")
//    @DeleteMapping("/{id:[0-9]+}")
//    @Transactional(rollbackFor = Exception.class)
//    public final Result<Integer> deleteById(
//            @PathVariable Serializable id) throws RestletException {
//        preDelete(id);
//        int n = getService().deleteById(id);
//        postDelete(id);
//        RestletConfig config = SpringContextHolder.getBean(RestletConfig.class);
//        if (config.isDeleteResponseBody()) {
//            return Result.data(n).success(RestletCode.OK);
//        } else {
//            return Result.data(n).success(RestletCode.NO_CONTENT);
//        }
//    }

    /* The following method can be overridden by extended classes */

    public T preInsert(T entity) throws RestletException {
        return entity;
    }

    public T preUpdate(Serializable id, T entity) throws RestletException {
        return entity;
    }

    public T preUpdate(QuerySet<T> qs, T entity) throws RestletException {
        return entity;
    }

    public void preDelete(Serializable id) throws RestletException {

    }

    public QuerySet<T> preDelete(QuerySet<T> qs) throws RestletException {
        return qs;
    }

    public void postInsert(T entity) throws RestletException {

    }

    public void postUpdate(Serializable id, T entity) throws RestletException {

    }

    public void postUpdate(QuerySet<T> qs, T entity) throws RestletException {

    }

    public void postDelete(Serializable id) throws RestletException {

    }

    public void postDelete(QuerySet<T> qs) throws RestletException {

    }

}
