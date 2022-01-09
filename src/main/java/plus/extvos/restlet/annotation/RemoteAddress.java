package plus.extvos.restlet.annotation;

import java.lang.annotation.*;

/**
 *
 * @author shenmc
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteAddress {
}
