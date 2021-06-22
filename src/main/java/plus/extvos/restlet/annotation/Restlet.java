package plus.extvos.restlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mingcai SHEN
 *
 * Annotates Entities with CRUD controle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Restlet {

    /**
     * If allowed to read record(s)
     */
    boolean readable() default true;

    /**
     * If allowed to create new record(s)
     */
    boolean creatable() default true;

    /**
     * If allowed to update record(s)
     */
    boolean updatable() default true;

    /**
     * If allowed to delete record(s)
     */
    boolean deletable() default true;
}
