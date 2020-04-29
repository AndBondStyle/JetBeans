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

    public Object instantiate(String id) {
        String[] tokens = id.split(":");
        String loaderId = tokens[0]  + ":" + tokens[1];
        ClassLoaderBase loader = this.loaders.get(loaderId);
        // TODO: Try to dynamically create loader on cache miss?
        if (loader == null) throw new RuntimeException("Loader \"" + loaderId + "\" not found");
        Class<?> klass = loader.load(tokens[2]);
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            String message = "Failed to construct class \"" + id + "\"";
            throw new RuntimeException(message, e);
        }
    }

    public HashMap<String, ClassLoaderBase> getLoaders() {
        return this.loaders;
    }
}
