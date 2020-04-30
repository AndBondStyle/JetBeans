package core.registry;

import core.registry.loaders.CoreLoader;
import core.registry.loaders.Loader;

public final class Hardcoded {
    public static void populate(Registry registry) {
        Loader swingLoader = new CoreLoader("Swing", "javax.swing");
        swingLoader.add("JButton");
        swingLoader.add("JLabel");
        registry.add(swingLoader);

        Loader testLoader = new CoreLoader("Test", "java.lang");
        testLoader.add("String");
        testLoader.add("NonExistentClass");
        registry.add(testLoader);
    }
}
