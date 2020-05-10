package core.main;

import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.ui.tree.TreeUtil;
import gui.common.SimpleEventSupport;
import gui.common.tree.PatchedNode;

import javax.swing.tree.TreeNode;
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
import java.util.stream.Collectors;

public class MasterLoader extends ClassLoader implements SimpleEventSupport {
    public List<PatchedNode> groups = new ArrayList<>();
    public List<ClassLoader> loaders = new LinkedList<>();
    public HashMap<Integer, String> origins = new HashMap<>();
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
        if (this.origins.containsValue(path)) this.delete(path);
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
            this.origins.put(loader.hashCode(), path);
        } else if (ext.equals("class")) {
            URL url = pathObject.getParent().getParent().toUri().toURL();
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{url});
            String name = pathObject.getName(pathObject.getNameCount() - 2) + "." + filename.substring(0, filename.length() - 6);
            this.unsorted.add(this.makeClassNode(name, "", path, null));
            this.loaders.add(0, loader);
            this.origins.put(loader.hashCode(), path);
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

    public void delete(String path) {
        List<PatchedNode> roots = new ArrayList<>(this.groups);
        roots.add(this.unsorted);
        for (PatchedNode group : roots) {
            List<TreeNode> nodes = TreeUtil.listChildren(group);
            nodes.add(group);
            nodes = nodes.stream().filter(x -> {
                PatchedNode node = (PatchedNode) x;
                String value = node.presentation.getTooltip();
                return value != null && value.equals(path);
            }).collect(Collectors.toList());
            if (nodes.size() != 0) {
                this.delete((PatchedNode) nodes.get(0));
                return;
            }
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
        System.out.println("[TRACE] loadClass: name = " + name + " resolve = " + resolve);
        for (ClassLoader loader : this.loaders) {
            try {
                Class<?> l = loader.loadClass(name);
                System.out.println("LOADED BY " + l);
                return l;
            }
            catch (Throwable ignored) {}
        }
        return super.loadClass(name, resolve);
    }
}
