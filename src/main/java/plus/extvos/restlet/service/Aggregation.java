package plus.extvos.restlet.service;

import java.util.Arrays;
import java.util.List;

public class Aggregation {
    private final static String COUNT = "COUNT";
    private final static String SUM = "SUM";
    private final static String AVG = "AVG";
    private final static String MAX = "MAX";
    private final static String MIN = "MIN";

    private final static List<String> FUNCS = Arrays.asList(COUNT, SUM, AVG, MAX, MIN);
    private String function;
    private String field;

    public Aggregation(String function, String field) {
        this.field = field;
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public String getField() {
        return field;
    }

    public String expression() {
        return function.toUpperCase() + "(" + field + ") AS " + function + "__" + field;
    }

    public static boolean validFunc(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        return FUNCS.contains(s.toUpperCase());
    }

}
