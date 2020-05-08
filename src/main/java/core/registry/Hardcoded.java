package core.registry;

import core.TestBean;
import core.registry.loaders.CoreLoader;
import core.registry.loaders.Loader;

public final class Hardcoded {
    public static void populate(Registry registry) {
        Loader swingLoader = new CoreLoader("Swing", "javax.swing");
        swingLoader.add("JButton");
        swingLoader.add("JLabel");
        registry.add(swingLoader);

        Loader testLoader = new CoreLoader("Test", "");
        testLoader.add("java.lang.String");
        testLoader.add("NonExistentClass");
        testLoader.add(TestBean.class.getCanonicalName());
        registry.add(testLoader);
    }
}
