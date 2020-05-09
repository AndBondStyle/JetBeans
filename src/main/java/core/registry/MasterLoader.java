package core.registry;

import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.Pair;
import com.intellij.util.ui.tree.TreeUtil;
import core.JetBeans;
import gui.common.SimpleEventSupport;
import gui.common.tree.PatchedNode;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.List;
import java.util.*;

public class MasterLoader extends ClassLoader implements SimpleEventSupport {
    public List<PatchedNode> groups = new ArrayList<>();
    public List<ClassLoader> loaders = new LinkedList<>();
    public PatchedNode unsorted;
    public JetBeans core;

    public MasterLoader(JetBeans core) {
        super("MasterLoader", ClassLoader.getPlatformClassLoader());
        this.core = core;
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("jetbeans.jetbeans"));
        this.loaders.add(plugin.getPluginClassLoader());
        this.registerHardcodedClasses();
        this.unsorted = this.makeLoaderNode("!Unsorted", null, "Items imported from single .class files", "misc", null);
        this.groups.add(this.unsorted);
    }

    public PatchedNode makeLoaderNode(String path, String extra, String tooltip, String type, ClassLoader loader) {
        PatchedNode node = new PatchedNode(this.core.project, path, loader);
        if (path.startsWith("!")) {
            path = path.substring(1);
        } else {
            Path pathObject = Paths.get(path);
            path = pathObject.getName(pathObject.getNameCount() - 1).toString();
        }
        node.setPrimaryText(path);
        if (extra != null) node.setSecondaryText(extra);
        if (tooltip != null) node.setTooltip(tooltip);
        if (type.equals("jar")) node.setIcon(AllIcons.Nodes.PpJar);
        if (type.equals("lib")) node.setIcon(AllIcons.Nodes.PpLib);
        if (type.equals("misc")) node.setIcon(AllIcons.Actions.GroupBy);
        return node;
    }

    public PatchedNode makeClassNode(String name, String extra, String tooltip, ClassLoader loader) {
        PatchedNode node = new PatchedNode(this.core.project, name, loader);
        String[] tokens = name.split("\\.");
        node.setPrimaryText(tokens[tokens.length - 1]);
        if (extra == null && name.contains(".")) extra = name.substring(0, name.lastIndexOf("."));
        node.setSecondaryText(extra == null ? "" : extra);
        if (tooltip != null) node.setTooltip(tooltip);
        node.setIcon(AllIcons.Nodes.Class);
        return node;
    }

    public PatchedNode makePackageNode(String name) {
        PatchedNode node = new PatchedNode(this.core.project, "!" + name);
        node.setIcon(AllIcons.Nodes.Package);
        node.setPrimaryText(name);
        return node;
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
            PatchedNode group = this.makeLoaderNode(path, null, path, "jar", loader);
            List<PatchedNode> children = new ArrayList<>();
            for (JarEntry item : Collections.list(jar.entries())) {
                String name = item.getName();
                if (item.isDirectory() || !name.endsWith(".class") || name.contains("$")) continue;
                name = name.substring(0, name.length() - 6);
                children.add(this.makeClassNode(name.replace("/", "."), null, null, null));
            }
            this.groupByPackage(children, group);
            this.groups.add(group);
            this.loaders.add(0, loader);
        } else if (ext.equals("class")) {
            URL url = pathObject.getParent().getParent().toUri().toURL();
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{url});
            String name = pathObject.getName(pathObject.getNameCount() - 2) + "." + filename.substring(0, filename.length() - 6);
            this.unsorted.add(this.makeClassNode(name, "", path, null));
            this.loaders.add(0, loader);
        }
        this.fireEvent("updated");
    }

    public void groupByPackage(List<PatchedNode> nodes, PatchedNode parent) {
        TreeMap<String, PatchedNode> grouped = new TreeMap<>();
        for (PatchedNode node : nodes) {
            String pack = node.presentation.getLocationString();
            if (pack == null) pack = "";
            PatchedNode group = grouped.computeIfAbsent(pack, this::makePackageNode);
            node.setSecondaryText("");
            group.add(node);
        }
        for (Map.Entry<String, PatchedNode> item : grouped.entrySet()) {
            if (item.getKey().equals("")) continue;
            parent.add(item.getValue());
        }
        if (grouped.containsKey("")) {
            TreeUtil.listChildren(grouped.get("")).forEach(x -> parent.add((PatchedNode) x));
        }
    }

    public void delete(PatchedNode node) {
        if (node.getValue() == null) return;
        if (this.groups.contains(node)) this.groups.remove(node);
        if (TreeUtil.isAncestor(this.unsorted, node)) this.unsorted.remove(node);
        this.loaders.remove(node.getValue());
        this.fireEvent("updated");
    }

    public void reload(PatchedNode node) throws Exception {
        if (node.getValue() == null) return;
        String path = node.presentation.getTooltip();
        this.delete(node);
        this.loadFile(path);
    }

    public void registerHardcodedClasses() {
        PatchedNode group;

        // Swing components
        group = this.makeLoaderNode("!Swing", "javax.swing", "Standard Swing components", "lib", null);
        group.add(this.makeClassNode("javax.swing.JLabel", "", null, null));
        group.add(this.makeClassNode("javax.swing.JComboBox", "", null, null));
        group.add(this.makeClassNode("javax.swing.JProgressBar", "", null, null));
        group.add(this.makeClassNode("javax.swing.JSlider", "", null, null));
        group.add(this.makeClassNode("javax.swing.JFileChooser", "", null, null));
        group.add(this.makeClassNode("javax.swing.JColorChooser", "", null, null));
        group.add(this.makeClassNode("javax.swing.JTextField", "", null, null));
        group.add(this.makeClassNode("javax.swing.JTextArea", "", null, null));
        group.add(this.makeClassNode("javax.swing.JTextPane", "", null, null));
        group.add(this.makeClassNode("javax.swing.JPasswordField", "", null, null));
        group.add(this.makeClassNode("javax.swing.JFormattedTextField", "", null, null));
        group.add(this.makeClassNode("javax.swing.JToggleButton", "", null, null));
        group.add(this.makeClassNode("javax.swing.JButton", "", null, null));
        group.add(this.makeClassNode("javax.swing.JCheckBox", "", null, null));
        group.add(this.makeClassNode("javax.swing.JRadioButton", "", null, null));
        this.groups.add(group);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        System.out.println("[TRACE] loadClass: name = " + name + " resolve = " + resolve);
        for (ClassLoader loader : this.loaders) {
            try { return loader.loadClass(name); }
            catch (Throwable ignored) {}
        }
        return super.loadClass(name, resolve);
    }

    @Nullable
    @Override
    public URL getResource(String name) {
//        System.out.println("[TRACE] getResource: name = " + name);
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
//        System.out.println("[TRACE] getResources: name = " + name);
        return super.getResources(name);
    }

    @Nullable
    @Override
    public InputStream getResourceAsStream(String name) {
//        System.out.println("[TRACE] getResourceAsStream: name = " + name);
        return super.getResourceAsStream(name);
    }
}
