package plus.extvos.restlet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result 通用RESTful返回数据
 *
 * @author Mingcai SHEN
 */
public class Result<T> implements Serializable {

    /**
     * Result code defines as plus.extvos.restlet.Code
     */
    private Integer code;

    /**
     * Message output for detail information
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;


    /**
     * Detail error traces when there is a failure.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    /**
     * Data object
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * Total num of records by query
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;

    /**
     * Current page num
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long page;

    /**
     * Current pageSize
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long pageSize;

    /**
     * Current record returned in data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long count;

    @JsonIgnore
    private Map<String, String> headers;

    @JsonIgnore
    private Map<String, String> cookies;


    public static <T> Result<T> data(T t) {
        Result<T> r = new Result<>();
        r.data = t;
        if (t instanceof List) {
            r.count = (long) ((List<?>) t).size();
        }
        return r;
    }

    public static <T> Result<T> message(String m) {
        Result<T> r = new Result<>();
        r.msg = m;
        return r;
    }

    public static <T> Result<T> code(Code code) {
        Result<T> r = new Result<>();
        r.code = code.value();
        return r;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
        code = RestletCode.OK.value();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Result<T> paged(long total, long page, long pageSize) {
        if (this.data instanceof Collection) {
            this.count = (long) ((Collection<?>) this.data).size();
            this.total = total;
            this.page = page;
            this.pageSize = pageSize;
        }
        return this;
    }

    public Result<T> success() {
        return success(RestletCode.OK);
    }

    public Result<T> header(String key, String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    public Result<T> cookie(String key, String value) {
        if (this.cookies == null) {
            this.cookies = new LinkedHashMap<>();
        }
        this.cookies.put(key, value);
        return this;
    }

    public Result<T> success(Code c) {
        code = c.value();
        return this;
    }

    public Result<T> failure() {
        return failure(RestletCode.BAD_REQUEST);
    }

    public Result<T> failure(Code c) {
        code = c.value();
        if (msg == null) {
            msg = c.desc();
        }
        return this;
    }

}

