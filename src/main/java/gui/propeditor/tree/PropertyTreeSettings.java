package gui.propeditor.tree;

import core.inspection.PropertyInfo;
import gui.common.SimpleEventSupport;

import java.util.*;

public class PropertyTreeSettings implements SimpleEventSupport {
    public static int SORT_BY_NAME = 0;
    public static int SORT_BY_KIND = 1;

    public static int GROUP_BY_NONE = 0;
    public static int GROUP_BY_CLASS = 1;

    public static int SHOW_NON_BOUND = 1 << 2;
    public static int SHOW_HIDDEN = 1 << 3;
    public static int SHOW_EXPERT = 1 << 4;
    public static int SHOW_READONLY = 1 << 5;

    public int sortMode = SORT_BY_NAME;
    public int groupMode = GROUP_BY_CLASS;
    public int filter = SHOW_READONLY | SHOW_NON_BOUND;

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

    public PropertyNode[] filterNodes(PropertyNode[] nodes) {
        List<PropertyNode> result = new ArrayList<>();
        for (PropertyNode node : nodes) {
            PropertyInfo prop = node.editor.prop;
            if (!prop.isSettable() && (this.filter & SHOW_READONLY) == 0) continue;
            if (!prop.isBound() && (this.filter & SHOW_NON_BOUND) == 0) continue;
            if (prop.isHidden() && (this.filter & SHOW_HIDDEN) == 0) continue;
            if (prop.isExpert() && (this.filter & SHOW_EXPERT) == 0) continue;
            result.add(node);
        }
        return result.toArray(PropertyNode[]::new);
    }

    public PropertyNode[] sortNodes(PropertyNode[] nodes) {
        List<PropertyNode> nodeList = Arrays.asList(nodes);
        if (this.sortMode == SORT_BY_NAME) {
            nodeList.sort(Comparator.comparing(node -> node.editor.prop.name));
        } else {
            nodeList.sort(Comparator.comparing((PropertyNode node) -> {
                PropertyInfo prop = node.editor.prop;
                int result = 0;
                if (prop.isSettable()) result += SHOW_READONLY;
                if (!prop.isBound()) result += SHOW_NON_BOUND;
                if (prop.isHidden()) result += SHOW_HIDDEN;
                if (prop.isExpert()) result += SHOW_EXPERT;
                return result;
            }).thenComparing(node -> node.editor.prop.name));
        }
        return nodeList.toArray(PropertyNode[]::new);
    }

    public HashMap<String, List<PropertyNode>> groupNodes(PropertyNode[] nodes) {
        if (this.groupMode == GROUP_BY_NONE) return null;
        TreeMap<Class<?>, List<PropertyNode>> groups = new TreeMap<>((a, b) -> {
            boolean x = a.isAssignableFrom(b);
            boolean y = b.isAssignableFrom(a);
            if (x && y) return 0;
            return x ? -1 : 1;
        });
        for (PropertyNode node : nodes) {
            PropertyInfo prop = node.editor.prop;
            List<PropertyNode> group = groups.computeIfAbsent(prop.definer, (__) -> new ArrayList<>());
            group.add(node);
        }
        HashMap<String, List<PropertyNode>> result = new LinkedHashMap<>();
        for (Map.Entry<Class<?>, List<PropertyNode>> group : groups.entrySet()) {
            PropertyInfo prop = group.getValue().get(0).editor.prop;
            String title = group.getKey().getSimpleName();
            if (group.getKey() != prop.target.getClass()) title += "!inherited";
            result.put(title, group.getValue());
        }
        return result;
    }
}
