package org.extvos.restlet.utils;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author Mingcai SHEN
 */
public class EntityWrapper {

    /**
     * Get table name as a class was annotated by TableName.
     *
     * @param cls the entity class
     * @return table name of null
     */

    public static String getTableName(Class<?> cls) {
        if (cls.isAnnotationPresent(TableName.class)) {
            return cls.getAnnotation(TableName.class).value();
        }
        return null;
    }
}
