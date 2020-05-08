package core.registry;

import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.Pair;
import gui.common.SimpleEventSupport;
import icons.JavaUltimateIcons;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.List;
import java.util.*;

public class MasterLoader extends ClassLoader implements SimpleEventSupport {
    public HashMap<PresentationData, List<Pair<String, PresentationData>>> groups = new LinkedHashMap<>();
    public List<ClassLoader> loaders = new ArrayList<>();
    public PresentationData unsorted;

    public MasterLoader() {
        super("MasterLoader", ClassLoader.getPlatformClassLoader());
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("jetbeans.jetbeans"));
        this.loaders.add(plugin.getPluginClassLoader());
        this.registerHardcodedClasses();
        this.unsorted = this.makeLoaderPresentation("Unsorted", null, "misc");
        this.groups.put(this.unsorted, new ArrayList<>());
    }

    public PresentationData makeLoaderPresentation(String name, String extra, String type) {
        PresentationData p = new PresentationData();
        p.setPresentableText(name);
        if (extra != null) p.setLocationString(extra);
        if (type.equals("jar")) p.setIcon(AllIcons.Nodes.PpJar);
        if (type.equals("lib")) p.setIcon(AllIcons.Nodes.PpLib);
        if (type.equals("misc")) p.setIcon(AllIcons.Actions.GroupBy);
        return p;
    }

    public Pair<String, PresentationData> makeClassPresentation(String name, String extra) {
        PresentationData p = new PresentationData();
        String[] tokens = name.split("\\.");
        p.setPresentableText(tokens[tokens.length - 1]);
        if (extra != null) p.setLocationString(extra);
        p.setIcon(AllIcons.Nodes.Class);
        return new Pair<>(name, p);
    }

    public void loadFile(String path) throws Exception {
        Path pathObject = Paths.get(path);
        String filename = pathObject.getFileName().toString();
        String[] tokens = filename.split("\\.");
        String ext = tokens[tokens.length - 1].toLowerCase();
        if (ext.equals("jar")) {
            JarFile jar = new JarFile(path);
            URL url = new URL("jar:file:" + path + "!/");
            ClassLoader loader = URLClassLoader.newInstance(new URL[] {url});
            PresentationData group = this.makeLoaderPresentation(filename, null, "jar");
            List<Pair<String, PresentationData>> items = this.groups.computeIfAbsent(group, (__) -> new ArrayList<>());
            for (JarEntry item : Collections.list(jar.entries())) {
                if (item.isDirectory() || !item.getName().endsWith(".class")) continue;
                String name = item.getName().substring(0, item.getName().length() - 6);
                items.add(this.makeClassPresentation(name.replace("/", "."), ""));
            }
            this.loaders.add(loader);
        } else if (ext.equals("class")) {
            URL url = pathObject.getParent().getParent().toUri().toURL();
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{url});
            String name = pathObject.getName(pathObject.getNameCount() - 2) + "." + filename.substring(0, filename.length() - 6);
            this.groups.get(this.unsorted).add(this.makeClassPresentation(name, path));
            this.loaders.add(loader);
        }
        this.fireEvent("updated");
    }

    public void registerHardcodedClasses() {
        PresentationData loader;
        List<Pair<String, PresentationData>> items;

        // Swing components
        loader = this.makeLoaderPresentation("Swing", "javax.swing", "lib");
        items = this.groups.computeIfAbsent(loader, (__) -> new ArrayList<>());
        items.add(this.makeClassPresentation("javax.swing.JLabel", null));
        items.add(this.makeClassPresentation("javax.swing.JComboBox", null));
        items.add(this.makeClassPresentation("javax.swing.JProgressBar", null));
        items.add(this.makeClassPresentation("javax.swing.JSlider", null));
        items.add(this.makeClassPresentation("javax.swing.JFileChooser", null));
        items.add(this.makeClassPresentation("javax.swing.JColorChooser", null));
        items.add(this.makeClassPresentation("javax.swing.JTextField", null));
        items.add(this.makeClassPresentation("javax.swing.JTextArea", null));
        items.add(this.makeClassPresentation("javax.swing.JTextPane", null));
        items.add(this.makeClassPresentation("javax.swing.JPasswordField", null));
        items.add(this.makeClassPresentation("javax.swing.JFormattedTextField", null));
        items.add(this.makeClassPresentation("javax.swing.JToggleButton", null));
        items.add(this.makeClassPresentation("javax.swing.JButton", null));
        items.add(this.makeClassPresentation("javax.swing.JCheckBox", null));
        items.add(this.makeClassPresentation("javax.swing.JRadioButton", null));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println("[TRACE] loadClass: name = " + name + " resolve = " + resolve);
        for (ClassLoader loader : this.loaders) {
            try { return loader.loadClass(name); }
            catch (Throwable ignored) {}
        }
        return super.loadClass(name, resolve);
    }

    @Nullable
    @Override
    public URL getResource(String name) {
        System.out.println("[TRACE] getResource: name = " + name);
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        System.out.println("[TRACE] getResources: name = " + name);
        return super.getResources(name);
    }

    @Nullable
    @Override
    public InputStream getResourceAsStream(String name) {
        System.out.println("[TRACE] getResourceAsStream: name = " + name);
        return super.getResourceAsStream(name);
    }
}
