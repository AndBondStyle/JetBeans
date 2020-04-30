package core.registry.loaders;

import java.util.LinkedHashMap;
import java.util.HashMap;
import javax.swing.*;

public abstract class Loader extends ClassLoader {
    public static String SEP = "?";
    private static HashMap<String, Class<?>> keyToClass = new HashMap<>();
    private static HashMap<Class<?>, String> classToKey = new HashMap<>();
    protected HashMap<String, String> classes = new LinkedHashMap<>();

    protected String key;
    protected String data;
    protected String primaryText;
    protected String secondaryText;
    protected Icon icon;

    public Loader(String data) {
        this.key = Loader.classToKey.get(this.getClass());
        this.data = data;
    }

    public static void registerLoader(String key, Class<? extends Loader> klass) {
        Loader.keyToClass.put(key, klass);
        Loader.classToKey.put(klass, key);
    }

    public static Loader create(String id) {
        // TODO: ???
        try {
            String[] tokens = id.split(SEP);
            Class<?> klass = Loader.keyToClass.get(tokens[0]);
            return (Loader) klass.getDeclaredConstructor().newInstance(tokens[1]);
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
        String id = this.getID() + SEP + name;
        this.classes.put(name, id);
    }

    public HashMap<String, String> getClasses() {
        return this.classes;
    }

    public Class<?> load(String name) {
        try {
            return this.loadClass(name);
        } catch (ClassNotFoundException e) {
            String message = "Class \"" + name + "\" not found in loader \"" + this.getID() + "\"";
            throw new RuntimeException(message, e);
        }
    }

    public String getID() {
        return this.key + SEP + this.data;
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
