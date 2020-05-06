package core.registry.loaders;

import com.intellij.icons.AllIcons;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URLClassLoader;
import java.net.URL;

public class JarLoader extends Loader {
    static {
        Loader.registerLoader("jar", JarLoader.class);
    }

    private URLClassLoader loader;

    public JarLoader(String data) {
        super(data);
        this.parseJar();
    }

    private void parseJar() {
        try {
            JarFile jar = new JarFile(this.data);
            URL url = new URL("jar:file:" + this.data + "!/");
            this.loader = URLClassLoader.newInstance(new URL[] {url});
            for (JarEntry item : Collections.list(jar.entries())) {
                if (item.isDirectory() || !item.getName().endsWith(".class")) continue;
                String name = item.getName().substring(0, item.getName().length() - 6);
                this.add(name.replace('/', '.'));
            }
            this.primaryText = Paths.get(this.data).getFileName().toString();
            this.icon = AllIcons.Nodes.PpJar;
        } catch (IOException e) {
            String message = "Can't read jar (" + this.data + ")";
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loader.loadClass(name);
    }
}
