package plus.extvos.restlet.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import plus.extvos.restlet.controller.BaseController;

import java.lang.reflect.Method;

/**
 * @author shenmc
 */
public class RestletKeyGenerator implements KeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @Override
    public Object generate(Object target, Method method, Object... params) {
        log.info("RestletKeyGenerator::generate> target : {} ", target);
        log.info("RestletKeyGenerator::generate> method : {} ", method);
        log.info("RestletKeyGenerator::generate> params : {} ", params);
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        return new SimpleKey(params);
    }
}
