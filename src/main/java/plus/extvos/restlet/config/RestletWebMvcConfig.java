package plus.extvos.restlet.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import plus.extvos.common.utils.SpringContextHolder;
import plus.extvos.restlet.annotation.RequestHeaderResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Mingcai SHEN
 */
@Configuration
public class RestletWebMvcConfig implements WebMvcConfigurer, ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    RestletConfig restletConfig;

    private static final Logger log = LoggerFactory.getLogger(RestletWebMvcConfig.class);


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach((HttpMessageConverter<?> cvt) -> {
            if (cvt instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) cvt).setDefaultCharset(StandardCharsets.UTF_8);
                ((MappingJackson2HttpMessageConverter) cvt).setPrettyPrint(restletConfig.isPrettyJson());
            }
        });
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RequestHeaderResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        WebMvcConfigurer.super.addReturnValueHandlers(handlers);
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        event.getApplicationContext().getBeansWithAnnotation(RestController.class).forEach((k, b) -> {
//            log.debug("Loaded bean: {} {}", k, b.getClass().getName());
//            if (b instanceof BaseController) {
//                log.debug("Subclass of BaseController :> {} {}", k, b.getClass().getName());
//                Field[] fields = ((BaseController<?, ?>) b).getTableInfo().getEntityType().getDeclaredFields();
//                for (Field f : fields) {
//                    if (f.getClass().isPrimitive()) {
//                        log.error("{} is primitive", f.getName());
//                        throw new RuntimeException("primitive property of Restlet entity is not allowed");
//                    }
//                }
//            }
//        });
    }
}
