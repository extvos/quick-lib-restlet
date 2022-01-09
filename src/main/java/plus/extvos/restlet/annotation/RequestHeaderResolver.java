package plus.extvos.restlet.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shenmc
 */
public class RequestHeaderResolver implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(RequestHeaderResolver.class);
    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA",
        "REMOTE_ADDR"
    };

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class)
            || parameter.hasParameterAnnotation(UserAgent.class)
            || parameter.hasParameterAnnotation(RemoteAddress.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.getParameterType().equals(String.class)) {
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

            if (parameter.hasParameterAnnotation(RequestHeader.class)) {
                RequestHeader rh = parameter.getParameterAnnotation(RequestHeader.class);
                if (null != rh) {
                    return request.getHeader(rh.value());
                }
            } else if (parameter.hasParameterAnnotation(UserAgent.class)) {
                return request.getHeader(HttpHeaders.USER_AGENT);
            } else if (parameter.hasParameterAnnotation(RemoteAddress.class)) {
                for (String header : IP_HEADER_CANDIDATES) {
                    String ip = request.getHeader(header);
                    if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                        return ip;
                    }
                }
                return request.getRemoteAddr();
            } else {
                log.warn(" ???? ");
            }
        }
        return null;
    }
}
