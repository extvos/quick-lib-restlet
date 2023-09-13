package plus.extvos.restlet.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plus.extvos.common.Result;
import plus.extvos.common.ResultCode;
import plus.extvos.common.Validator;
import plus.extvos.common.exception.ResultException;
import plus.extvos.common.utils.PrimitiveConvert;
import plus.extvos.common.utils.SpringContextHolder;
import plus.extvos.logging.annotation.Log;
import plus.extvos.logging.annotation.type.LogAction;
import plus.extvos.logging.annotation.type.LogLevel;
import plus.extvos.restlet.QuerySet;
import plus.extvos.restlet.annotation.ActionType;
import plus.extvos.restlet.annotation.Restlet;
import plus.extvos.restlet.config.RestletConfig;
import plus.extvos.restlet.intfs.OnCreate;
import plus.extvos.restlet.intfs.OnUpdate;
import plus.extvos.restlet.service.BaseService;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Mingcai SHEN
 * BaseController, a basic RESTful controller provice CURD operations
 */
public abstract class BaseController<T, S extends BaseService<T>> extends BaseROController<T, S> {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    private final boolean _creatable;
    private final boolean _updatable;
    private final boolean _deletable;

    public BaseController() {
        super();
        _creatable = true;
        _updatable = true;
        _deletable = true;
    }

    public boolean creatable() {
        if (getGenericType().isAnnotationPresent(Restlet.class)) {
            Restlet r = getGenericType().getAnnotation(Restlet.class);
            return r.creatable();
        } else if (this.getClass().isAnnotationPresent(Restlet.class)) {
            Restlet r = this.getClass().getAnnotation(Restlet.class);
            return r.creatable();
        }
        return _creatable;
    }

    public boolean updatable() {
        if (getGenericType().isAnnotationPresent(Restlet.class)) {
            Restlet r = getGenericType().getAnnotation(Restlet.class);
            return r.updatable();
        } else if (this.getClass().isAnnotationPresent(Restlet.class)) {
            Restlet r = this.getClass().getAnnotation(Restlet.class);
            return r.updatable();
        }
        return _updatable;
    }

    public boolean deletable() {
        if (getGenericType().isAnnotationPresent(Restlet.class)) {
            Restlet r = getGenericType().getAnnotation(Restlet.class);
            return r.deletable();
        } else if (this.getClass().isAnnotationPresent(Restlet.class)) {
            Restlet r = this.getClass().getAnnotation(Restlet.class);
            return r.deletable();
        }
        return _deletable;
    }

    protected void updateFieldValue(T entity, String k, Object v) {
        try {
            Field f = entity.getClass().getDeclaredField(k);
            if (null != f) {
                f.setAccessible(true);
                f.set(entity, PrimitiveConvert.from(v.toString()).to(f.getType()));
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error(">>", e);
        }
    }

    @ApiOperation(value = "插入一条新记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @PostMapping()
    @Log(action = LogAction.CREATE, level = LogLevel.IMPORTANT)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<T> insertNew(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @Validated(OnCreate.class) @RequestBody T record) throws ResultException {
        predicate(ActionType.CREATE, pathMap, null, null);
        log.debug("insertNew:> {}, {}", pathMap, record);
        if (Validator.notEmpty(pathMap)) {
            for (String k : pathMap.keySet()) {
                updateFieldValue(record, k, pathMap.get(k));
            }
        }
        record = preInsert(record);
        int n = getService().insert(record);
        postInsert(record);
        Result<T> ret = Result.data(record).success(ResultCode.CREATED);
        ret.setCount((long) n);
        return ret;
    }


    @ApiOperation(value = "按条件更新记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @PutMapping(value = {"", "/{id}"})
    @Log(action = LogAction.UPDATE, level = LogLevel.IMPORTANT)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<T> updateByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap,
            @Validated(OnUpdate.class) @RequestBody T record) throws ResultException {
        predicate(ActionType.UPDATE, pathMap, queryMap, null);
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);

        int updated = 0;
        if (pathMap != null && pathMap.containsKey("id")) {
            Serializable id = convertId(pathMap.get("id").toString());
            record = preUpdate(id, record);
            updated = getService().updateById(id, record);
            postUpdate(id, record);
        } else {
            record = preUpdate(qs, record);
            if (Validator.notEmpty(pathMap)) {
                for (String k : pathMap.keySet()) {
                    updateFieldValue(record, k, pathMap.get(k));
                }
            }
            updated = getService().updateByMap(qs, record);
            postUpdate(qs, record);
        }
        Result<T> ret = Result.data(record).success();
        ret.setCount((long) updated);
        return ret;
    }


    @ApiOperation(value = "按条件删除记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @DeleteMapping(value = {"", "/{id}"})
    @Log(action = LogAction.DELETE, level = LogLevel.IMPORTANT)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<Integer> deleteByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        predicate(ActionType.DELETE, pathMap, queryMap, null);
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        int deleted = 0;
        if (pathMap != null && pathMap.containsKey("id")) {
            Serializable id = convertId(pathMap.get("id").toString());
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
            return Result.data(deleted).success(ResultCode.OK);
        } else {
            return Result.data(deleted).success(ResultCode.NO_CONTENT);
        }
    }

    /* The following method can be overridden by extended classes */

    public T preInsert(T entity) throws ResultException {
        if (!creatable()) {
            throw ResultException.forbidden();
        }
        return entity;
    }

    public T preUpdate(Serializable id, T entity) throws ResultException {
        if (!updatable()) {
            throw ResultException.forbidden();
        }
        return entity;
    }

    public T preUpdate(QuerySet<T> qs, T entity) throws ResultException {
        if (!updatable()) {
            throw ResultException.forbidden();
        }
        return entity;
    }

    public void preDelete(Serializable id) throws ResultException {
        if (!deletable()) {
            throw ResultException.forbidden();
        }
    }

    public QuerySet<T> preDelete(QuerySet<T> qs) throws ResultException {
        if (!deletable()) {
            throw ResultException.forbidden();
        }
        return qs;
    }

    public void postInsert(T entity) throws ResultException {

    }

    public void postUpdate(Serializable id, T entity) throws ResultException {

    }

    public void postUpdate(QuerySet<T> qs, T entity) throws ResultException {

    }

    public void postDelete(Serializable id) throws ResultException {

    }

    public void postDelete(QuerySet<T> qs) throws ResultException {

    }

}
