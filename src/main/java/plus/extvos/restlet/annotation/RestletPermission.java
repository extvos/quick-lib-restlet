package plus.extvos.restlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mingcai SHEN
 * <p>
 * Annotates permission requirements with CRUD controle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestletPermission {
    /**
     * If allowed to read record(s)
     *
     * @return permissions required to read
     */
    String[] read() default {};

    /**
     * If allowed to create new record(s)
     *
     * @return permissions required to create
     */
    String[] create() default {};

    /**
     * If allowed to update record(s)
     *
     * @return permissions required to update
     */
    String[] update() default {};

    /**
     * If allowed to delete record(s)
     *
     * @return permissions required to delete
     */
    String[] delete() default {};
}
