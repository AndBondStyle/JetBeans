package core.registry.loaders;

import com.intellij.icons.AllIcons;

public class SimpleLoader extends ClassLoaderBase {
    static {
        ClassLoaderBase.registerLoader("BASE", SimpleLoader.class);
    }

    public SimpleLoader(String data) {
        super(data);
    }

    public SimpleLoader(String display, String pkg) {
        super(pkg);
        this.primaryText = display;
        this.secondaryText = pkg + ".*";
        this.icon = AllIcons.Nodes.PpLib;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name, true);
    }
}
