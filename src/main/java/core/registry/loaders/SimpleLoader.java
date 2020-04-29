package core.registry.loaders;

import com.intellij.icons.AllIcons;

public class SimpleLoader extends ClassLoaderBase {
    static {
        ClassLoaderBase.registerLoader("core", SimpleLoader.class);
    }

    public SimpleLoader(String data) {
        super(data);
    }

    public SimpleLoader(String display, String pkg) {
        super(pkg);
        this.primaryText = display;
        this.secondaryText = pkg;
        this.icon = AllIcons.Nodes.PpLib;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (!this.data.equals("")) name = this.data + "." + name;
        return super.loadClass(name, true);
    }
}
