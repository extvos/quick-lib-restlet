package plus.extvos.restlet.annotation;

public enum ActionType {
    READ("READ"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String desc;

    ActionType(String s) {
        desc = s;
    }

    public String getDesc() {
        return desc;
    }
}
