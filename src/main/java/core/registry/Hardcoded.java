package core.registry;

import core.registry.loaders.ClassLoaderBase;
import core.registry.loaders.SimpleLoader;

public final class Hardcoded {
    public static void populate(Registry registry) {
        ClassLoaderBase swingLoader = new SimpleLoader("Swing", "javax.swing");
        swingLoader.add("JButton");
        swingLoader.add("JLabel");
        registry.add(swingLoader);
    }
}
