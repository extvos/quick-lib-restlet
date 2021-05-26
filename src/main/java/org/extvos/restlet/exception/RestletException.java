package org.extvos.restlet.exception;

import org.extvos.restlet.Code;
import org.extvos.restlet.RestletCode;
import org.extvos.restlet.Result;

/**
 * @author Mingcai SHEN
 */
public class RestletException extends Exception {
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
    public static RestletException badRequest(String message) {
        return new RestletException(RestletCode.BAD_REQUEST, message);
    }

    /**
     * build a unauthorized exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException unauthorized(String message) {
        return new RestletException(RestletCode.UNAUTHORIZED, message);
    }

    /**
     * build a forbidden exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException forbidden(String message) {
        return new RestletException(RestletCode.FORBIDDEN, message);
    }

    /**
     * build a not found exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException notFound(String message) {
        return new RestletException(RestletCode.NOT_FOUND, message);
    }

    /**
     * build a method not allowed exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException methodNotAllowed(String message) {
        return new RestletException(RestletCode.METHOD_NOT_ALLOWED, message);
    }

    /**
     * build a conflict exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException conflict(String message) {
        return new RestletException(RestletCode.CONFLICT, message);
    }

    /**
     * build a internal server error exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException internalServerError(String message) {
        return new RestletException(RestletCode.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * build a not implemented exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException notImplemented(String message) {
        return new RestletException(RestletCode.NOT_IMPLEMENTED, message);
    }

    /**
     * build a bad gateway exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException badGateway(String message) {
        return new RestletException(RestletCode.BAD_GATEWAY, message);
    }

    /**
     * build a service unavailable exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException serviceUnavailable(String message) {
        return new RestletException(RestletCode.SERVICE_UNAVAILABLE, message);
    }

    /**
     * build a gateway timeout exception with given message.
     *
     * @param message for hint
     * @return a RestletException
     */
    public static RestletException gatewayTimeout(String message) {
        return new RestletException(RestletCode.GATEWAY_TIMEOUT, message);
    }
}
