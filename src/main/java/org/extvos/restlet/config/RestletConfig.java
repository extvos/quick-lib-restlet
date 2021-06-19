package org.extvos.restlet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mingcai SHEN
 */
@Configuration
public class RestletConfig {
    /**
     * the default page number, it can be always 0.
     */
    @Value("${quick.restlet.default-page:0}")
    private long defaultPage;

    /**
     * default page size, default value is 50.
     */
    @Value("${quick.restlet.default-page-size:50}")
    public long defaultPageSize;

    /**
     * the query key for page number
     */
    @Value("${quick.restlet.page-key:__page}")
    private String pageKey;

    /**
     * the query key for pageSize
     */
    @Value("${quick.restlet.page-size-key:__pageSize}")
    private String pageSizeKey;

    /**
     * the query key for includes columns
     */
    @Value("${quick.restlet.includes-key:__includes}")
    private String includesKey;

    /**
     * the query key for order by columns
     */
    @Value("${quick.restlet.order-by-key:__orderBy}")
    private String orderByKey;

    /**
     * the query key for excludes columns
     */
    @Value("${quick.restlet.excludes-key:__excludes}")
    private String excludesKey;

    /**
     * switch for logging trace when failure
     */
    @Value("${quick.restlet.log-trace: false}")
    private boolean logTrace;


    /**
     * switch for response a body result when delete success.
     */
    @Value("${quick.restlet.delete-response-body:false}")
    private boolean deleteResponseBody;


    public boolean isPrettyJson() {
        return prettyJson;
    }

    public void setPrettyJson(boolean prettyJson) {
        this.prettyJson = prettyJson;
    }

    @Value("${quick.restlet.pretty-json:false}")
    private boolean prettyJson;

    public long getDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(long defaultPage) {
        this.defaultPage = defaultPage;
    }

    public long getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(long defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public String getPageKey() {
        return pageKey;
    }

    public void setPageKey(String pageKey) {
        this.pageKey = pageKey;
    }

    public String getPageSizeKey() {
        return pageSizeKey;
    }

    public void setPageSizeKey(String pageSizeKey) {
        this.pageSizeKey = pageSizeKey;
    }

    public String getIncludesKey() {
        return includesKey;
    }

    public void setIncludesKey(String includesKey) {
        this.includesKey = includesKey;
    }

    public String getExcludesKey() {
        return excludesKey;
    }

    public void setExcludesKey(String excludesKey) {
        this.excludesKey = excludesKey;
    }

    public String getOrderByKey() {
        return orderByKey;
    }

    public void setOrderByKey(String orderByKey) {
        this.orderByKey = orderByKey;
    }

    public boolean isLogTrace() {
        return logTrace;
    }

    public void setLogTrace(boolean logTrace) {
        this.logTrace = logTrace;
    }

    public boolean isDeleteResponseBody() {
        return deleteResponseBody;
    }

    public void setDeleteResponseBody(boolean deleteResponseBody) {
        this.deleteResponseBody = deleteResponseBody;
    }
}
