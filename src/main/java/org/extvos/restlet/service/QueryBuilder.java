package org.extvos.restlet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * @author Mingcai SHEN
 */
public interface QueryBuilder {
    /**
     * Parse a query k:v pair and append conditions to wrapper.
     *
     * @param k       query key
     * @param v       query value
     * @param wrapper the wrapper which you may add new conditions
     * @return true if query is processed, otherwise false
     */
    boolean parseQuery(String k, Object v, QueryWrapper<?> wrapper);
}
