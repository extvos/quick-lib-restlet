package plus.extvos.restlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mingcai SHEN
 * <p>
 * Annotates Entities with CRUD controle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Restlet {

    /**
     * If allowed to read record(s)
     * @return true if readable
     */
    boolean readable() default true;

    /**
     * If allowed to create new record(s)
     * @return true if creatable
     */
    boolean creatable() default true;

    /**
     * If allowed to update record(s)
     * @return true if updatable
     */
    boolean updatable() default true;

    /**
     * If allowed to delete record(s)
     * @return true if deletable
     */
    boolean deletable() default true;
}
