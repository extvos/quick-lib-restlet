package plus.extvos.restlet.exception;

import plus.extvos.restlet.Code;
import plus.extvos.restlet.RestletCode;
import plus.extvos.restlet.Result;

/**
 * @author Mingcai SHEN
 */
public class RestletException extends RuntimeException {
    private final Code code;

    public RestletException(Code c, String m) {
        super(m);
        code = c;

    }

    /**
     * Build a Result<> object with exception
     *
     * @return Result<?>
     */
    public Result<?> asResult() {
        return Result.message(getMessage()).failure(code);
    }

    /**
     * get error code
     *
     * @return Code
     */
    public Code getCode() {
        return code;
    }

    /**
     * build a bad request exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException badRequest(String... message) {
        return new RestletException(RestletCode.BAD_REQUEST, message.length > 0 ? message[0] : RestletCode.BAD_REQUEST.desc());
    }

    /**
     * build a unauthorized exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException unauthorized(String... message) {
        return new RestletException(RestletCode.UNAUTHORIZED, message.length > 0 ? message[0] : RestletCode.UNAUTHORIZED.desc());
    }

    /**
     * build a forbidden exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException forbidden(String... message) {
        return new RestletException(RestletCode.FORBIDDEN, message.length > 0 ? message[0] : RestletCode.FORBIDDEN.desc());
    }

    /**
     * build a not found exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException notFound(String... message) {
        return new RestletException(RestletCode.NOT_FOUND, message.length > 0 ? message[0] : RestletCode.NOT_FOUND.desc());
    }

    /**
     * build a method not allowed exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException methodNotAllowed(String... message) {
        return new RestletException(RestletCode.METHOD_NOT_ALLOWED, message.length > 0 ? message[0] : RestletCode.METHOD_NOT_ALLOWED.desc());
    }

    /**
     * build a conflict exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException conflict(String... message) {
        return new RestletException(RestletCode.CONFLICT, message.length > 0 ? message[0] : RestletCode.CONFLICT.desc());
    }

    /**
     * build a internal server error exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException internalServerError(String... message) {
        return new RestletException(RestletCode.INTERNAL_SERVER_ERROR, message.length > 0 ? message[0] : RestletCode.INTERNAL_SERVER_ERROR.desc());
    }

    /**
     * build a not implemented exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException notImplemented(String... message) {
        return new RestletException(RestletCode.NOT_IMPLEMENTED, message.length > 0 ? message[0] : RestletCode.NOT_IMPLEMENTED.desc());
    }

    /**
     * build a bad gateway exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException badGateway(String... message) {
        return new RestletException(RestletCode.BAD_GATEWAY, message.length > 0 ? message[0] : RestletCode.BAD_GATEWAY.desc());
    }

    /**
     * build a service unavailable exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException serviceUnavailable(String... message) {
        return new RestletException(RestletCode.SERVICE_UNAVAILABLE, message.length > 0 ? message[0] : RestletCode.SERVICE_UNAVAILABLE.desc());
    }

    /**
     * build a gateway timeout exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException gatewayTimeout(String... message) {
        return new RestletException(RestletCode.GATEWAY_TIMEOUT, message.length > 0 ? message[0] : RestletCode.GATEWAY_TIMEOUT.desc());
    }
}
