package org.extvos.restlet.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonParseException;
import org.extvos.restlet.RestletCode;
import org.extvos.restlet.Result;
import org.extvos.restlet.config.RestletConfig;
import org.extvos.restlet.exception.RestletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * @author Mingcai SHEN
 */
@RestControllerAdvice
public class RestletResultAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private RestletConfig config;

    private static final Logger log = LoggerFactory.getLogger(RestletResultAdvice.class);

    /**
     * Generic Exception process
     */
    @ExceptionHandler(value = {NestedRuntimeException.class, RestletException.class})
    public Result<?> exception(HttpServletRequest request, Exception e, HandlerMethod handlerMethod) {
        log.warn("exception:> {} {} ({}) > {}",
            request.getMethod(), request.getRequestURI(), handlerMethod.getMethod().getName(), e.getMessage());
        if (config.isLogTrace()) {
            log.error("Trace:> ", e);
        }
        if (e instanceof RestletException) {
            return ((RestletException) e).asResult();
        }
        if (e instanceof HttpMessageNotReadableException) {
            return Result.message("Invalid request data format").failure(RestletCode.BAD_REQUEST);
        }
        if (e instanceof JsonParseException) {
            return Result.message("Invalid json data").failure(RestletCode.BAD_REQUEST);
        }
        if (e instanceof ConversionNotSupportedException) {
            return Result.message("Invalid request queries").failure(RestletCode.BAD_REQUEST);
        }
        Result<?> r = Result.message("Unknown internal server error").failure(RestletCode.INTERNAL_SERVER_ERROR);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        r.setError(writer.toString());
        return r;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();
        assert method != null;
        return method.getReturnType() == Result.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
//        log.debug("beforeBodyWrite:> {} {}: {} ({})",
//            request.getMethod(), request.getURI(), body != null ? body.getClass().getSimpleName() : "null",
//            selectedContentType.toString());
        // Set response status code according the result status code.
        if (body instanceof Result) {
            Result<?> rs = (Result<?>) body;
//            log.debug("beforeBodyWrite:> {},{}", (Result<?>) body, ((Result<?>) body).getCode());
            if (rs.getHeaders() != null) {
                rs.getHeaders().forEach((k, v) -> {
                    response.getHeaders().add(k, v);
                });
            }
            if (rs.getCookies() != null) {
                rs.getCookies().forEach((k, v) -> {
                    Cookie ck = new Cookie(k, v);
//                    resp.addCookie(ck);
                });
            }
            int code = ((Result<?>) body).getCode() / 100;
            response.setStatusCode(HttpStatus.valueOf(code));
        }
        return body;
    }

}
