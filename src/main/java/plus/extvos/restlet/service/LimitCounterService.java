package plus.extvos.restlet.service;

import java.util.List;

/**
 * @author Mingcai SHEN
 */
public interface LimitCounterService {
    /**
     * Counting by keys.
     *
     * @param keys  of list
     * @param num   current num?
     * @param limit limit ?
     * @return count
     */
    int count(List<?> keys, int num, int limit);
}
