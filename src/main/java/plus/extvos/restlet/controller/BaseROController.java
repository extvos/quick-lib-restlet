package plus.extvos.restlet.controller;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import plus.extvos.common.Assert;
import plus.extvos.common.Result;
import plus.extvos.common.Validator;
import plus.extvos.common.exception.ResultException;
import plus.extvos.common.utils.SpringContextHolder;
import plus.extvos.logging.annotation.Log;
import plus.extvos.logging.annotation.type.LogAction;
import plus.extvos.logging.annotation.type.LogLevel;
import plus.extvos.restlet.QuerySet;
import plus.extvos.restlet.annotation.Restlet;
import plus.extvos.restlet.config.RestletConfig;
import plus.extvos.restlet.service.BaseService;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Mingcai SHEN
 */
public abstract class BaseROController<T, S extends BaseService<T>> {

    private boolean readable;

    protected TableInfo tableInfo;

    private static final Logger log = LoggerFactory.getLogger(BaseROController.class);

    public BaseROController() {
        log.debug("BaseROController:> Initializing ... {} {}", getGenericType().getName(), getGenericType().isAnnotationPresent(Restlet.class));
        readable = true;
        if (getGenericType().isAnnotationPresent(Restlet.class)) {
            Restlet r = getGenericType().getAnnotation(Restlet.class);
            readable = r.readable();
        } else if (this.getClass().isAnnotationPresent(Restlet.class)) {
            Restlet r = this.getClass().getAnnotation(Restlet.class);
            readable = r.readable();
        }
    }

    /**
     * get the generic type which helps to get table info
     *
     * @return Class&lt;?&gt;
     */
    protected Class<?> getGenericType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    /**
     * get the bundled base service
     *
     * @return bundled base service.
     */
    public abstract S getService();

    /**
     * @return bundled table info
     */
    public TableInfo getTableInfo() {
        if (this.tableInfo == null) {
            this.tableInfo = TableInfoHelper.getTableInfo(getGenericType());
        }
        return this.tableInfo;
    }

    /**
     * Building a QuerySet from a map from all queries.
     *
     * @param columnMaps as queries
     * @return a new QuerySet
     */
    @SafeVarargs
    protected final QuerySet<T> buildQuerySet(Map<String, Object>... columnMaps) {
        RestletConfig config = SpringContextHolder.getBean(RestletConfig.class);
        log.debug("buildQuerySet :> config: {}", config);
        long offset = config.getDefaultPage(), limit = config.getDefaultPageSize();
        QuerySet<T> qs = new QuerySet<T>(getTableInfo());
        Map<String, Object> allQueryMap = new LinkedHashMap<>();
        for (Map<String, Object> m : columnMaps) {
            if (Validator.notEmpty(m)) {
                allQueryMap.putAll(m);
            }
        }
        log.debug("buildQuerySet:> params: {}", allQueryMap);
        if (allQueryMap.containsKey(config.getPageKey())) {
            log.debug("selectByMap: get offset:> {} {}", config.getPageKey(), allQueryMap.get(config.getPageKey()));
            offset = Long.parseLong(allQueryMap.get(config.getPageKey()).toString());
            allQueryMap.remove(config.getPageKey());
            if (offset < 0) {
                offset = config.getDefaultPage();
            }
        }
        if (allQueryMap.containsKey(config.getPageSizeKey())) {
            log.debug("selectByMap: get offset:> {} {}", config.getPageSizeKey(), allQueryMap.get(config.getPageSizeKey()));
            limit = Long.parseLong(allQueryMap.get(config.getPageSizeKey()).toString());
            allQueryMap.remove(config.getPageSizeKey());
//            if (limit < 0) {
//                limit = config.getDefaultPageSize();
//            }
        }

        if (allQueryMap.containsKey(config.getExcludesKey())) {
            log.debug("selectByMap: get excludes:> {} {}", config.getExcludesKey(), allQueryMap.get(config.getExcludesKey()));
            qs.setExcludeCols(new HashSet<>(Arrays.asList(allQueryMap.get(config.getExcludesKey()).toString().split(","))));
            allQueryMap.remove(config.getExcludesKey());
        }

        if (allQueryMap.containsKey(config.getIncludesKey())) {
            log.debug("selectByMap: get includes:> {} {}", config.getIncludesKey(), allQueryMap.get(config.getIncludesKey()));
            qs.setIncludeCols(new HashSet<>(Arrays.asList(allQueryMap.get(config.getIncludesKey()).toString().split(","))));
            allQueryMap.remove(config.getIncludesKey());
        }

        if (allQueryMap.containsKey(config.getOrderByKey())) {
            log.debug("selectByMap: get orderBy:> {} {}", config.getOrderByKey(), allQueryMap.get(config.getOrderByKey()));
            qs.setOrderBy(new HashSet<>(Arrays.asList(allQueryMap.get(config.getOrderByKey()).toString().split(","))));
            allQueryMap.remove(config.getOrderByKey());
        }

//        QuerySet qs = new QuerySet(offset, limit, columnMap);
        if (defaultIncludes() != null) {
            qs.updateIncludeCols(new HashSet<>(Arrays.asList(defaultIncludes())));
        }

        if (defaultExcludes() != null) {
            qs.updateExcludeCols(new HashSet<>(Arrays.asList(defaultExcludes())));
        }

        qs.setPage(offset);
        qs.setPageSize(limit);
        qs.setQueries(allQueryMap);

        return qs;
    }


    @ApiOperation(value = "???????????????????????????", notes = "????????????????????????????????? https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "__page", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__pageSize", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__orderBy", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__includes", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__excludes", required = false, defaultValue = "")
    })
    @GetMapping()
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Generic SELECT multiple rows")
    public final Result<List<T>> selectByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        log.debug("BaseROController<{}>::selectByMap:1 parameters: {} {}", getService().getClass().getName(), queryMap, pathMap);
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        qs = preSelect(qs);
        log.debug("BaseROController<{}>::selectByMap:2 {}", getService().getClass().getName(), qs);
        long total = getService().countByMap(qs);
        log.debug("BaseROController<{}>::selectByMap:3 total = {}", getService().getClass().getName(), total);
        List<T> objs = qs.getPageSize() > 0 ? getService().selectByMap(qs) : new ArrayList<>();
        log.debug("BaseROController<{}>::selectByMap:4 count = {}", getService().getClass().getName(), objs.size());
        objs = postSelect(objs);
        return Result.data(objs).paged(total, qs.getPage(), qs.getPageSize()).success();
    }

    @ApiOperation(value = "???????????????????????????", notes = "????????????????????????????????? https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @GetMapping("/_count")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Generic COUNT multiple rows")
    public final Result<Long> countByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        log.debug("BaseROController<{}>::countByMap:1 parameters: {} {}", getService().getClass().getName(), queryMap, pathMap);
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        qs = preSelect(qs);
        log.debug("BaseROController<{}>::countByMap:2 {}", getService().getClass().getName(), qs);
        long total = getService().countByMap(qs);
        return Result.data(total).success();
    }


    @ApiOperation(value = "{id}??????????????????", notes = "????????????????????????????????? https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "__includes", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__excludes", required = false, defaultValue = "")
    })
    @GetMapping("/{id}")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Generic Select by Id")
    public final Result<T> selectById(
            @PathVariable Serializable id,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> columnMap) throws ResultException {
        log.debug("BaseROController:>{} selectById({}) with {}", getService().getClass().getName(), id, columnMap);
        id = convertId(id);
        QuerySet<T> qs = buildQuerySet(columnMap);
        preSelect(id);
        T entity = getService().selectById(qs, id);
        entity = postSelect(entity);
        log.debug("BaseROController:>{} selectById({})", getService().getClass().getName(), entity);
        if (entity == null) {
            throw ResultException.notFound("not found id of object");
        }
        return Result.data(entity).success();
    }

    protected Serializable convertId(Serializable id) throws ResultException {
        Assert.notEmpty(id);
        try {
            if (Integer.class == getTableInfo().getKeyType()) {
                return Integer.parseInt(id.toString());
            } else if (Long.class == getTableInfo().getKeyType()) {
                return Long.parseLong(id.toString());
            } else {
                return id;
            }
        } catch (Exception e) {
            throw ResultException.badRequest(e.getMessage());
        }

//        try {
//            Object nid = getTableInfo().getKeyType().newInstance();
//        } catch (InstantiationException | IllegalAccessException e) {
//            return id;
//        }
    }

    /* The following method can be overridden by extended classes */
    public String[] defaultIncludes() {
        return null;
    }

    public String[] defaultExcludes() {
        return null;
    }

    public void preSelect(Serializable id) throws ResultException {
        if (!readable) {
            throw ResultException.forbidden();
        }
    }

    public T postSelect(T entity) throws ResultException {
        return entity;
    }

    public List<T> postSelect(List<T> entities) throws ResultException {
        return entities;
    }


    public QuerySet<T> preSelect(QuerySet<T> qs) throws ResultException {
        if (!readable) {
            throw ResultException.forbidden();
        }
        return qs;
    }
}
