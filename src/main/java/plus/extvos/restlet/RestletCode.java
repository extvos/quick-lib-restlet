package plus.extvos.restlet;

/**
 * default customized Code(s)
 *
 * @author Mingcai SHEN
 */

public enum RestletCode implements Code {
    /**
     * SUCCESS
     */
    OK(20000, "OK"),
    CREATED(20100, "Created"),
    NO_CONTENT(20400, "No Content"),
    /**
     * Moved
     */
    MOVED_PERMANENTLY(30100, "Moved Permanently"),
    MOVED_TEMPORARILY(30200, "Moved Temporarily"),
    /**
     * Bad Requests
     */
    BAD_REQUEST(40000, "Bad Request"),
    UNAUTHORIZED(40100, "Unauthorized"),
    FORBIDDEN(40300, "Forbidden"),
    NOT_FOUND(40400, "Not Found"),
    METHOD_NOT_ALLOWED(40500, "Method Not Allowed"),
    CONFLICT(40900, "Conflict"),

    /**
     * Server Error
     */
    INTERNAL_SERVER_ERROR(50000, "Internal Server Error"),
    NOT_IMPLEMENTED(50100, "Not Implemented"),
    BAD_GATEWAY(50200, "Bad Gateway"),
    SERVICE_UNAVAILABLE(50300, "Service Unavailable"),
    GATEWAY_TIMEOUT(50400, "Gateway Timeout");


    private final int value;
    private final String desc;

    RestletCode(int v, String d) {
        value = v;
        desc = d;
    }

    @Override
    public int value() {
        return this.value;
    }

    @Override
    public int status() {
        return this.value / 100;
    }

    @Override
    public String desc() {
        return this.desc;
    }
}
