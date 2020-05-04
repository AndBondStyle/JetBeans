package gui.propeditor.tree;

import gui.common.SimpleEventSupport;

public class PropertyTreeSettings implements SimpleEventSupport {
    static int SORT_BY_NAME = 0;
    static int SORT_BY_KIND = 1;

    static int GROUP_BY_NONE = 0;
    static int GROUP_BY_CLASS = 1;

    static int SHOW_SETTABLE = 1;
    static int SHOW_GETTABLE = 1 << 1;
    static int SHOW_BOUND = 1 << 2;
    static int SHOW_HIDDEN = 1 << 3;
    static int SHOW_EXPERT = 1 << 4;

    private PropertyTree tree;
    public int sortMode = SORT_BY_NAME;
    public int groupMode = GROUP_BY_CLASS;
    public int filter = SHOW_SETTABLE | SHOW_GETTABLE | SHOW_BOUND;

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
        this.fireEvent("update");
    }

    public void setGroupMode(int groupMode) {
        this.groupMode = groupMode;
        this.fireEvent("update");
    }

    public void toggleFilter(int key, boolean apply) {
        if (apply) this.filter |= key;
        else this.filter &= ~key;
        this.fireEvent("update");
    }
}
