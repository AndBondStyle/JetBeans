package core.registry.loaders;

import java.util.LinkedHashMap;
import java.util.HashMap;
import javax.swing.*;

public abstract class ClassLoaderBase extends ClassLoader {
    private static HashMap<String, Class<?>> keyToClass = new HashMap<>();
    private static HashMap<Class<?>, String> classToKey = new HashMap<>();
    protected HashMap<String, String> classes = new LinkedHashMap<>();

    protected String key;
    protected String data;
    protected String primaryText;
    protected String secondaryText;
    protected Icon icon;

    public ClassLoaderBase(String data) {
        this.key = ClassLoaderBase.classToKey.get(this.getClass());
        this.data = data;
    }

    public static void registerLoader(String key, Class<? extends ClassLoaderBase> klass) {
        ClassLoaderBase.keyToClass.put(key, klass);
        ClassLoaderBase.classToKey.put(klass, key);
    }

    public static ClassLoaderBase create(String id) {
        // TODO: ???
        try {
            String[] tokens = id.split(":");
            Class<?> klass = ClassLoaderBase.keyToClass.get(tokens[0]);
            return (ClassLoaderBase) klass.getDeclaredConstructor().newInstance(tokens[1]);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("IOOB while creating loader. ID = " + id);
            e.printStackTrace();
        } catch (ReflectiveOperationException e) {
            System.err.println("Reflection error while creating loader. ID = " + id);
            e.printStackTrace();
        }
        return null;
    }

    public void add(String name) {
        String id = this.getID() + ":" + name;
        this.classes.put(name, id);
    }

    public HashMap<String, String> getClasses() {
        return this.classes;
    }

    public Class<?> safeLoadClass(String name) {
        try {
            return this.loadClass(this.data + "." + name, true);
        } catch (ClassNotFoundException e) {
            System.err.println("Class NAME = " + name + " not found in loader ID = " + this.getID());
            return null;
        }
    }

    public String getID() {
        return this.key + ":" + this.data;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public Icon getIcon() {
        return icon;
    }
}
