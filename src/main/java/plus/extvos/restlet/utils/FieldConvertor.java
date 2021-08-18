package plus.extvos.restlet.utils;

import java.sql.Timestamp;

public class FieldConvertor {

    private final Class<?> generic;

    public FieldConvertor(Class<?> t) {
        generic = t;
    }

    public Object convert(Object o) {
        if (generic == Integer.class) {
            return Integer.parseInt(o.toString());
        } else if (generic == Long.class) {
            return Long.parseLong(o.toString());
        } else if (generic == Float.class) {
            return Float.parseFloat(o.toString());
        } else if (generic == Double.class) {
            return Double.parseDouble(o.toString());
        } else if (generic == Boolean.class) {
            return Boolean.parseBoolean(o.toString());
        } else if (generic == Timestamp.class) {
            return Timestamp.valueOf(o.toString());
        } else if (generic == String.class) {
            return o.toString();
        }
        return o;
    }
}
