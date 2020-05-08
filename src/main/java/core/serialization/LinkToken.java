package core.serialization;

public class LinkToken {
    public static int PROP_PROP    = 1;
    public static int PROP_METHOD  = 2;
    public static int EVENT_PROP   = 3;
    public static int EVENT_METHOD = 4;

    public String srcInstance;
    public String srcFeature;
    public String dstInstance;
    public String dstFeature;
    public int type;
    public String script;
}
