package plus.extvos.restlet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.extvos.common.exception.ResultException;
import plus.extvos.restlet.service.QueryBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * @author Mingcai SHEN
 */
public class QuerySet<T> implements Serializable {

    private static final String OP_NOT = "not";
    private static final String OP_NOTNULL = "notnull";
    private static final String OP_ISNULL = "isnull";
    private static final String OP_CONTAINS = "contains";
    private static final String OP_START_WITH = "startWith";
    private static final String OP_END_WITH = "endWith";
    private static final String OP_GT = "gt";
    private static final String OP_GTE = "gte";
    private static final String OP_LT = "lt";
    private static final String OP_LTE = "lte";
    private static final String OP_RANGE = "range";
    private static final String OP_BETWEEN = "between";
    private static final String OP_IN = "in";

    private static final String OP_OR = "or";
    private static final String OP_AND = "and";


    private static final Logger log = LoggerFactory.getLogger(QuerySet.class);

    private long page;
    private long pageSize;
    private Map<String, Object> queries;
    private Set<String> orderBy;

    private Set<String> includeCols;

    public Set<String> getIncludeCols() {
        return includeCols;
    }

    public void setIncludeCols(Set<String> includeCols) {
        this.includeCols = includeCols;
    }

    public void updateIncludeCols(Set<String> incs) {
        if (null == incs || incs.isEmpty()) {
            return;
        }
        if (null != includeCols) {
            includeCols.addAll(incs);
        } else {
            includeCols = incs;
        }
    }


    public Set<String> getExcludeCols() {
        return excludeCols;
    }

    public void setExcludeCols(Set<String> excludeCols) {
        this.excludeCols = excludeCols;
    }

    public void updateExcludeCols(Set<String> excs) {
        if (null == excs || excs.isEmpty()) {
            return;
        }
        if (excludeCols != null) {
            excludeCols.addAll(excs);
        } else {
            excludeCols = excs;
        }
    }

    private Set<String> excludeCols;

    private TableInfo tableInfo;
    private Map<String, String> columnMap;

    public Set<String> columns() {
        if (includeCols != null && includeCols.size() > 0) {
            includeCols.forEach((String k) -> {
                if (!columnMap.containsKey(k)) {
                    includeCols.remove(k);
                }
            });
            if (includeCols.size() > 0) {
                return includeCols;
            }
        }
        if (excludeCols != null && excludeCols.size() > 0) {
            Set<String> ss = new HashSet<>();
            ss.add(tableInfo.getKeyColumn());
            tableInfo.getFieldList().forEach((TableFieldInfo f) -> {
                if (!(excludeCols.contains(f.getProperty()) || excludeCols.contains(f.getColumn()))) {
                    ss.add(f.getColumn());
                }
            });
            return ss;
        }

        Set<String> ss = new HashSet<>();
        ss.add(tableInfo.getKeyColumn());
        tableInfo.getFieldList().forEach((TableFieldInfo f) -> {
            ss.add(f.getColumn());
        });
        return ss;
    }


    public Set<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Set<String> orderBy) {
        this.orderBy = orderBy;
    }

    public QuerySet(TableInfo tableInfo) {
        setTableInfo(tableInfo);
    }

    public QuerySet(TableInfo tableInfo, long page, long pageSize) {
        setTableInfo(tableInfo);
        this.page = page;
        this.pageSize = pageSize;
    }

    public QuerySet(TableInfo tableInfo, Map<String, Object> map) {
        setTableInfo(tableInfo);
        this.putAll(map);
    }

    public QuerySet(TableInfo tableInfo, long page, long pageSize, Map<String, Object> map) {
        log.debug("QuerySet({},{},{})", page, pageSize, map);
        setTableInfo(tableInfo);
        this.page = page;
        this.pageSize = pageSize;
        this.putAll(map);
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        if (this.tableInfo != null) {
            columnMap = new LinkedHashMap<>();
            columnMap.put(tableInfo.getKeyColumn(), tableInfo.getKeyColumn());
            columnMap.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
            tableInfo.getFieldList().forEach((TableFieldInfo f) -> {
                columnMap.put(f.getProperty(), f.getColumn());
                columnMap.put(f.getColumn(), f.getColumn());
            });
        }
    }

    public long getPage() {
        log.debug("getOffset {}", page);
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        log.debug("getLimit {}", pageSize);
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, Object> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, Object> queries) {
        this.queries = queries;
    }

    public boolean hasKey(String k) {
        return this.queries != null && this.queries.containsKey(k);
    }

    public int size() {
        if (this.queries == null) {
            return 0;
        } else {
            return this.queries.size();
        }
    }

    public boolean isEmpty() {
        return this.queries == null || this.queries.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.queries != null && this.queries.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.queries != null && this.queries.containsValue(value);
    }

    public Object get(Object key) {
        if (this.queries == null) {
            return null;
        }
        return this.queries.get(key);
    }

    public Object put(String key, Object value) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        return this.queries.put(key, value);
    }

    public Object remove(Object key) {
        if (this.queries == null) {
            return null;
        } else {
            return this.queries.remove(key);
        }


    }

    public void putAll(Map<? extends String, ?> m) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        this.queries.putAll(m);
    }

    public void clear() {
        if (this.queries != null) {
            this.queries.clear();
        }
    }

    public Set<String> keys() {
        if (this.queries == null) {
            return new HashSet<>();
        } else {
            return this.queries.keySet();
        }
    }

    public Collection<Object> values() {
        if (this.queries == null) {
            return new HashSet<>();
        } else {
            return this.queries.values();
        }
    }

    protected void parseQuery(String k, Object v, QueryWrapper<?> wrapper) throws ResultException {
        String[] ks = k.split("__");
        log.debug("parseQuery > {}", k);
        if (!columnMap.containsKey(ks[0])) {
            throw ResultException.badRequest("unknown column: " + ks[0]);
        }
        String field = columnMap.get(ks[0]);
        if (null == v || v.toString().isEmpty()) {
            return;
        }
        boolean fieldAccepted = false;
        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            fieldAccepted = fieldInfo.getColumn().equals(field) || fieldInfo.getProperty().equals(field);
            if (fieldAccepted) {
                break;
            }
        }
        if (!fieldAccepted) {
            throw ResultException.badRequest("unknown column '" + field + "'");
        }
        String operator;
        boolean condition = true;
        if (ks.length >= 3) {
            if (ks[1].equals(OP_OR)) {
                wrapper.or();
            }
            operator = ks[2];
        } else if (ks.length == 2) {
            if (ks[1].equals(OP_OR)) {
                wrapper.or().eq(field, v);
                return;
            } else {
                operator = ks[1];
            }
        } else {
            wrapper.eq(field, v);
            return;
        }
        switch (operator) {
            case OP_CONTAINS:
                wrapper.like(field, v);
                break;
            case OP_START_WITH:
                wrapper.likeRight(field, v);
                break;
            case OP_END_WITH:
                wrapper.likeLeft(field, v);
                break;
            case OP_NOT:
                wrapper.ne(field, v);
                break;
            case OP_GT:
                wrapper.gt(ks[0], v);
                break;
            case OP_GTE:
                wrapper.ge(field, v);
                break;
            case OP_LT:
                wrapper.lt(field, v);
                break;
            case OP_LTE:
                wrapper.le(field, v);
                break;
            case OP_RANGE:
            case OP_BETWEEN:
                String[] vs1 = v.toString().split(",", 2);
                if (vs1.length > 1) {
                    wrapper.between(field, vs1[0], vs1[1]);
                } else {
                    // TODO: failure ?
                    throw ResultException.badRequest("range values need to concat with ','");
                }
                break;
            case OP_IN:
                String[] vs2 = v.toString().split(",");
                wrapper.in(field, Arrays.asList(vs2));
                break;
            case OP_ISNULL:
                wrapper.isNull(field);
                break;
            case OP_NOTNULL:
                wrapper.isNotNull(field);
                break;
            default:
                // TODO: error ?
                throw ResultException.badRequest("unsupported operator: " + ks[1]);

        }
    }

    public QueryWrapper<T> buildQueryWrapper(QueryBuilder... qbs) throws ResultException {
        QueryWrapper<T> qw = new QueryWrapper<>();
        if (queries == null) {
            return qw;
        }
        for (Map.Entry<String, Object> entry : queries.entrySet()) {
            boolean done = false;
            for (QueryBuilder qb : qbs) {
                if (qb.parseQuery(entry.getKey(), entry.getValue(), qw)) {
                    done = true;
                    break;
                }
            }
            if (!done) {
                parseQuery(entry.getKey(), entry.getValue(), qw);
            }
        }
        return qw;
    }

}
