package plus.extvos.restlet.controller;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import plus.extvos.common.Assert;
import plus.extvos.common.Result;
import plus.extvos.common.exception.ResultException;
import plus.extvos.common.utils.SpringContextHolder;
import plus.extvos.logging.annotation.Log;
import plus.extvos.logging.annotation.type.LogAction;
import plus.extvos.logging.annotation.type.LogLevel;
import plus.extvos.restlet.QuerySet;
import plus.extvos.restlet.annotation.ActionType;
import plus.extvos.restlet.annotation.Restlet;
import plus.extvos.restlet.config.RestletConfig;
import plus.extvos.restlet.service.Aggregation;
import plus.extvos.restlet.service.BaseService;
import plus.extvos.restlet.utils.DateTrunc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Mingcai SHEN
 */
public abstract class BaseROController<T, S extends BaseService<T>> {

    private boolean _readable;

    private static final Logger log = LoggerFactory.getLogger(BaseROController.class);

    public BaseROController() {
        _readable = true;
    }

    public boolean readable() {
        if (getGenericType().isAnnotationPresent(Restlet.class)) {
            Restlet r = getGenericType().getAnnotation(Restlet.class);
            return r.readable();
        } else if (this.getClass().isAnnotationPresent(Restlet.class)) {
            Restlet r = this.getClass().getAnnotation(Restlet.class);
            return r.readable();
        }
        return _readable;
    }

    /**
     * get the generic type which helps to get table info
     *
     * @return Class&lt;?&gt;
     */
    protected Class<?> getGenericType() {
        return getService().getGenericType();
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
        return getService().getTableInfo();
    }

    protected void checkRole(String s) {
        SecurityUtils.getSubject().checkRole(s);
    }

    protected void checkPermission(String s) {
        SecurityUtils.getSubject().checkPermission(s);
    }

    protected boolean hasRole(String s) {
        return SecurityUtils.getSubject().hasRole(s);
    }

    protected boolean isPermitted(String s) {
        return SecurityUtils.getSubject().isPermitted(s);
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
        return getService().buildQuerySet(config, defaultIncludes(), defaultExcludes(), columnMaps);
    }


    @ApiOperation(value = "按查询条件查询列表", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "__page", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__pageSize", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__orderBy", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__includes", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__excludes", required = false, defaultValue = "")
    })
    @GetMapping()
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Generic SELECT multiple rows")
    public Result<List<T>> selectByMap(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        predicate(ActionType.READ, pathMap, queryMap, null);
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

    @ApiOperation(value = "按查询条件统计数量", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @GetMapping("/_count")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Generic COUNT multiple rows")
    public Result<Long> countByQuery(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        predicate(ActionType.READ, pathMap, queryMap, null);
        log.debug("BaseROController<{}>::countByQuery:1 parameters: {} {}", getService().getClass().getName(), queryMap, pathMap);
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        qs = preSelect(qs);
        log.debug("BaseROController<{}>::countByQuery:2 {}", getService().getClass().getName(), qs);
        long total = getService().countByMap(qs);
        return Result.data(total).success();
    }

    @ApiOperation(value = "按查询条件根据字段分组统计数量", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @GetMapping("/_count/{fieldName}")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "COUNT by field grouping")
    public Result<Map<Object, Long>> countByField(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        predicate(ActionType.READ, pathMap, queryMap, null);
        log.debug("BaseROController<{}>::countByField:1 parameters: {} {}", getService().getClass().getName(), queryMap, pathMap);
        Assert.notNull(pathMap, ResultException.badRequest());
        Assert.isTrue(pathMap.containsKey("fieldName"), ResultException.badRequest("fieldName required"));
        String fieldName = pathMap.get("fieldName").toString();
        pathMap.remove("fieldName");
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        qs = preSelect(qs);
        log.debug("BaseROController<{}>::countByField:2 {}", getService().getClass().getName(), qs);
        Map<Object, Long> ret = getService().countByMap(fieldName, qs);
        return Result.data(ret).success();
    }

    @ApiOperation(value = "按查询条件根据字段分组聚合统计", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @GetMapping("/_aggregate/{fieldName}")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Aggregate by field grouping")
    public Result<List<Map<String, Object>>> aggregateByField(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        predicate(ActionType.READ, pathMap, queryMap, null);
        log.debug("BaseROController<{}>::aggregateByField:1 parameters: {} {}", getService().getClass().getName(), queryMap, pathMap);
        Assert.notNull(pathMap, ResultException.badRequest());
        Assert.isTrue(pathMap.containsKey("fieldName"), ResultException.badRequest("fieldName required"));
        String fieldName = pathMap.get("fieldName").toString();
        pathMap.remove("fieldName");
        List<Aggregation> aggregations = new LinkedList<>();
        if (null != queryMap) {
            queryMap.entrySet().removeIf(entry -> {
                String k = entry.getKey();
                if (k.startsWith("__") && Aggregation.validFunc(k.substring(2))) {
                    String[] fields = entry.getValue().toString().split(",");
                    for (String f : fields) {
                        aggregations.add(new Aggregation(k.substring(2), f));
                    }
                    return true;
                }
                return false;
            });
        }
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        qs = preSelect(qs);
        log.debug("BaseROController<{}>::aggregateByField:2 {}", getService().getClass().getName(), qs);
        List<Map<String, Object>> ret = getService().aggregateByMap(fieldName, qs, aggregations.toArray(new Aggregation[0]));
        return Result.data(ret).success();
    }

    @ApiOperation(value = "按查询条件根据时间字段字段分组聚合统计", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @GetMapping("/_trend/{fieldName}/{interval}")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Aggregate by timestamp and field grouping")
    public Result<List<Map<String, Object>>> trendByField(
            @ApiParam(hidden = true) @PathVariable(required = false) Map<String, Object> pathMap,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> queryMap) throws ResultException {
        predicate(ActionType.READ, pathMap, queryMap, null);
        log.debug("BaseROController<{}>::trendByField:1 parameters: {} {}", getService().getClass().getName(), queryMap, pathMap);
        Assert.notNull(pathMap, ResultException.badRequest());
        AtomicReference<String> groupBy = new AtomicReference<>();
        Assert.isTrue(pathMap.containsKey("fieldName"), ResultException.badRequest("fieldName required"));
        Assert.isTrue(pathMap.containsKey("interval"), ResultException.badRequest("interval required"));
        String fieldName = pathMap.get("fieldName").toString();
        pathMap.remove("fieldName");
        String interval = pathMap.get("interval").toString();
        pathMap.remove("interval");
        Assert.isTrue(DateTrunc.validate(interval), ResultException.badRequest());
        List<Aggregation> aggregations = new LinkedList<>();
        if (null != queryMap) {
            queryMap.entrySet().removeIf(entry -> {
                String k = entry.getKey();
                if (k.startsWith("__") && Aggregation.validFunc(k.substring(2))) {
                    String[] fields = entry.getValue().toString().split(",");
                    for (String f : fields) {
                        aggregations.add(new Aggregation(k.substring(2), f));
                    }
                    return true;
                } else if (k.equalsIgnoreCase("__groupBy") || k.equalsIgnoreCase("__group_by")) {
                    groupBy.set(entry.getValue().toString());
                    return true;
                }
                return false;
            });
        }
        QuerySet<T> qs = buildQuerySet(pathMap, queryMap);
        qs = preSelect(qs);
        log.debug("BaseROController<{}>::trendByField:2 {}", getService().getClass().getName(), qs);
        List<Map<String, Object>> ret = getService().trendByMap(
                fieldName, interval, groupBy.get() != null ? groupBy.get().split(",") : null
                , qs, aggregations.toArray(new Aggregation[0]));
        return Result.data(ret).success();
    }


    @ApiOperation(value = "{id}查询单个记录", notes = "查询条件组织，请参考： https://github.com/extvos/quick-lib-restlet/blob/develop/README.md")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "__includes", required = false, defaultValue = ""),
            @ApiImplicitParam(name = "__excludes", required = false, defaultValue = "")
    })
    @GetMapping("/{id}")
    @Log(action = LogAction.SELECT, level = LogLevel.NORMAL, comment = "Generic Select by Id")
    public Result<T> selectById(
            @PathVariable Serializable id,
            @ApiParam(hidden = true) @RequestParam(required = false) Map<String, Object> columnMap) throws ResultException {
        predicate(ActionType.READ, null, columnMap, id);
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

    }

    /* The following method can be overridden by extended classes */
    public String[] defaultIncludes() {
        return null;
    }

    public String[] defaultExcludes() {
        return null;
    }

    public void predicate(ActionType action, Map<String, Object> pathVariables, Map<String, Object> queries, Serializable id) throws ResultException {
    }

    public void preSelect(Serializable id) throws ResultException {
        if (!readable()) {
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
        if (!readable()) {
            throw ResultException.forbidden();
        }
        return qs;
    }
}
