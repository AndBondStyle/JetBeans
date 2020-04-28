package core.registry;

import core.registry.loaders.ClassLoaderBase;
import gui.common.SimpleEventSupport;

import java.util.LinkedHashMap;
import java.util.HashMap;

public class Registry implements SimpleEventSupport {
    private HashMap<String, ClassLoaderBase> loaders = new LinkedHashMap<>();

    public Registry() {
        Hardcoded.populate(this);
    }

    public boolean add(ClassLoaderBase loader) {
        String key = loader.getID();
        if (this.loaders.containsKey(key)) return false;
        this.loaders.put(key, loader);
        this.fireEvent("update");
        return true;
    }

    public Class<?> getClass(String id) {
        String[] tokens = id.split(":");
        ClassLoaderBase loader = this.loaders.get(tokens[0]  + ":" + tokens[1]);
        // TODO: Try to dynamically create loader on cache miss?
        if (loader == null) return null;
        return loader.safeLoadClass(tokens[2]);
    }

    public HashMap<String, ClassLoaderBase> getLoaders() {
        return this.loaders;
    }
}
