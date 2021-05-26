package org.extvos.restlet.controller;

import org.extvos.restlet.QuerySet;
import org.extvos.restlet.RestletCode;
import org.extvos.restlet.Result;
import org.extvos.restlet.exception.RestletException;
import org.extvos.restlet.service.BaseService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Mingcai SHEN
 * BaseController, a basic RESTful controller provice CURD operations
 */
public abstract class BaseController<T, S extends BaseService<T>> extends BaseROController<T, S> {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @ApiOperation(value = "插入一条新记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @PostMapping()
    @Transactional(rollbackFor = Exception.class)
    public final Result<?> insertNew(@RequestBody T record) throws RestletException {
        record = preInsert(record);
        int n = getService().insert(record);
        postInsert(record);
        return Result.data(n).success(RestletCode.CREATED);
    }

    @ApiOperation(value = "按查询条件更新记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @PutMapping()
    @Transactional(rollbackFor = Exception.class)
    public final Result<?> updateByMap(@ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> columnMap, @RequestBody T record) throws RestletException {
        QuerySet<T> qs = buildQuerySet(columnMap);
        record = preUpdate(qs, record);
        int n = getService().updateByMap(qs, record);
        postUpdate(qs, record);
        return Result.data(n).success();
    }

    @ApiOperation(value = "按查询条件删除记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @DeleteMapping()
    @Transactional(rollbackFor = Exception.class)
    public final Result<?> deleteByMap(@ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> columnMap) throws RestletException {
        QuerySet<T> qs = buildQuerySet(columnMap);
        qs = preDelete(qs);
        int n = getService().deleteByMap(qs);
        postDelete(qs);
        return Result.data(n).success();
    }

    @ApiOperation(value = "按{id}更新记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @PutMapping("/{id:[0-9]+}")
    @Transactional(rollbackFor = Exception.class)
    public final Result<?> updateById(@PathVariable Serializable id, @RequestBody T record) throws RestletException {
        record = preUpdate(id, record);
        int n = getService().updateById(id, record);
        postUpdate(id, record);
        return Result.data(n).success();
    }

    @ApiOperation(value = "按{id}删除记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @DeleteMapping("/{id:[0-9]+}")
    @Transactional(rollbackFor = Exception.class)
    public final Result<?> deleteById(@PathVariable Serializable id) throws RestletException {
        preDelete(id);
        int n = getService().deleteById(id);
        postDelete(id);
        return Result.data(n).success(RestletCode.NO_CONTENT);
    }

    /* The following method can be overridden by extended classes */

    public T preInsert(T entity) throws RestletException {
        return entity;
    }

    public List<T> preInsert(List<T> entities) throws RestletException {
        return entities;
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

    public void postInsert(List<T> entities) throws RestletException {

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
