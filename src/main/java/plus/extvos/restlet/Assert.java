package plus.extvos.restlet;

import plus.extvos.restlet.exception.RestletException;

import java.util.Collection;
import java.util.Map;

/**
 * Assert, a generic assertion toolkit.
 *
 * @author Mingcai SHEN
 */
public class Assert {


    public static void equals(Object o, Object v, RestletException... e) throws RestletException {
        if (!o.equals(v)) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("values not equals");
        }
    }

    public static void notEquals(Object o, Object v, RestletException... e) throws RestletException {
        if (o.equals(v)) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("values are equals");
        }
    }

    public static void notNull(Object o, RestletException... e) throws RestletException {
        if (null == o) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("null value is not acceptable");
        }
    }

    public static void isNull(Object o, RestletException... e) throws RestletException {
        if (null != o) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("not null value is not acceptable");
        }
    }

    public static void isTrue(boolean f, RestletException... e) throws RestletException {
        if (!f) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("false value is not acceptable");
        }
    }

    public static void notEmpty(String s, RestletException... e) throws RestletException {
        if (null == s || s.isEmpty()) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("empty is not acceptable");
        }
    }

    public static void notEmpty(Map<?, ?> s, RestletException... e) throws RestletException {
        if (null == s || s.isEmpty()) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("empty is not acceptable");
        }
    }

    public static void notEmpty(Collection<?> s, RestletException... e) throws RestletException {
        if (null == s || s.isEmpty()) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("empty is not acceptable");
        }
    }

    public static void isEmpty(String s, RestletException... e) throws RestletException {
        if (null != s && !s.isEmpty()) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("not empty is not acceptable");
        }
    }

    public static void isEmpty(Map<?, ?> s, RestletException... e) throws RestletException {
        if (null != s && !s.isEmpty()) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("not empty is not acceptable");
        }
    }

    public static void isEmpty(Collection<?> s, RestletException... e) throws RestletException {
        if (null != s && !s.isEmpty()) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("not empty is not acceptable");
        }
    }

    public static void lessThan(Integer s, Integer v, RestletException... e) throws RestletException {
        if (s >= v) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value can not be greater than " + v);
        }
    }

    public static void lessThan(Long s, Long v, RestletException... e) throws RestletException {
        if (s >= v) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value can not be greater than " + v);
        }
    }

    public static void greaterThan(Integer s, Integer v, RestletException... e) throws RestletException {
        if (s <= v) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value can not be less than " + v);
        }
    }

    public static void greaterThan(Long s, Long v, RestletException... e) throws RestletException {
        if (s <= v) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value can not be less than " + v);
        }
    }


    public static void between(Integer v, Integer begin, Integer end, RestletException... e) throws RestletException {
        if (v < begin || v > end) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value not between " + begin + "," + end);
        }
    }

    public static void between(Long v, Long begin, Long end, RestletException... e) throws RestletException {
        if (v < begin || v > end) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value not between " + begin + "," + end);
        }
    }

    public static void notBetween(Integer v, Integer begin, Integer end, RestletException... e) throws RestletException {
        if (v >= begin && v <= end) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value is between " + begin + "," + end);
        }
    }

    public static void notBetween(Long v, Long begin, Long end, RestletException... e) throws RestletException {
        if (v >= begin && v <= end) {
            throw e.length > 0 ? e[0] : RestletException.badRequest("value is between " + begin + "," + end);
        }
    }

}
