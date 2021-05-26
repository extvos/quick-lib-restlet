package org.extvos.restlet.config;

import org.extvos.restlet.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Mingcai SHEN
 */
@Configuration
public class RestletWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    RestletConfig restletConfig;

//    @Autowired
//    ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(RestletWebMvcConfig.class);


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.debug("configureMessageConverters :> {}", converters.size());
        converters.forEach((HttpMessageConverter<?> cvt) -> {
//            log.debug("configureMessageConverters :> {}", cvt.getClass().getName());
            if (cvt instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) cvt).setDefaultCharset(StandardCharsets.UTF_8);
                ((MappingJackson2HttpMessageConverter) cvt).setPrettyPrint(restletConfig.isPrettyJson());
            }
        });
    }

//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.debug("extendMessageConverters :> {}", converters.size());
//        converters.forEach((HttpMessageConverter<?> cvt) -> {
//            log.debug("configureMessageConverters :> {}", cvt.getClass().getName());
//        });
//    }

//    //    @Bean
//    public HttpMessageConverters configureMessageConverters() {
//        // objectMapper.setDateFormat(StdDateFormat.getDateTimeInstance());
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
//        converter.setDefaultCharset(StandardCharsets.UTF_8);
//        converter.setPrettyPrint(restletConfig.isPrettyJson());
//        return new HttpMessageConverters(converter);
//    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
