package plus.extvos.restlet.utils;

public class DateTrunc {
    private static final String[] intervals = {
            "microseconds",
            "milliseconds",
            "second",
            "minute",
            "hour",
            "day",
            "week",
            "month",
            "quarter",
            "year",
            "decade",
            "century",
            "millennium",
    };

    public static boolean validate(String interval) {
        for (String f : intervals) {
            if (interval.equalsIgnoreCase(f)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getIntervals() {
        return intervals;
    }
}
