package plus.extvos.restlet;

/**
 * @author Mingcai SHEN
 */

public interface Code {

    /**
     * get code value
     *
     * @return integer
     */
    public int value();

    /**
     * get status value for HTTP
     *
     * @return integer
     */
    public int status();

    /**
     * get code description
     *
     * @return String
     */
    public String desc();
}
