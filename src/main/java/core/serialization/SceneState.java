package core.serialization;

import core.inspection.*;
import core.linking.EventLink;
import core.linking.LinkBase;
import core.linking.Linker;
import core.linking.PropertyLink;
import core.main.JetBeans;
import gui.canvas.Canvas;
import gui.canvas.CanvasItem;
import gui.link.Link;
import gui.wrapper.Wrapper;
import org.apache.commons.io.input.ClassLoaderObjectInputStream;

import java.io.*;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class SceneState implements Serializable {
    public InstanceToken[] instances;
    public LinkToken[] links;

    public byte[] dump(Canvas canvas, JetBeans core) {
        List<InstanceToken> instances = new ArrayList<>();
        List<LinkToken> links = new ArrayList<>();
        for (CanvasItem item : canvas.items) {
            if (item instanceof Wrapper)
                instances.add(this.dumpInstance((Wrapper) item, core));
            if (item instanceof Link && item.isSelectable())
                links.add(this.dumpLink((Link) item));
        }
        this.instances = instances.toArray(InstanceToken[]::new);
        this.links = links.toArray(LinkToken[]::new);
        List<String> origins = instances.stream().map(x -> x.origin)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            this.writeOrigins(origins, stream);
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(this);
            out.close();
            stream.close();
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to dump scene state", e);
        }
    }

    public void writeOrigins(List<String> origins, ByteArrayOutputStream stream) throws IOException {
        stream.write(ByteBuffer.allocate(4).putInt(origins.size()).array());
        for (String origin : origins) {
            stream.write(ByteBuffer.allocate(4).putInt(origin.length()).array());
            stream.write(origin.getBytes());
        }
    }

    public List<String> readOrigins(ByteArrayInputStream stream) throws IOException {
        DataInputStream data = new DataInputStream(stream);
        List<String> items = new ArrayList<>();
        int size = data.readInt();
        for (int i = 0; i < size; i++) {
            int length = data.readInt();
            items.add(new String(data.readNBytes(length)));
        }
        return items;
    }

    public InstanceToken dumpInstance(Wrapper wrapper, JetBeans core) {
        Object bean = wrapper.getTarget();
        InstanceToken token = new InstanceToken();
        token.id = "" + bean.hashCode();
        token.type = bean.getClass().getName();
        ClassLoader loader = bean.getClass().getClassLoader();
        if (loader != null) {
            int hash = loader.hashCode();
            token.origin = core.loader.origins.getOrDefault(hash, null);
        }
        if (bean instanceof Serializable) token.ser = (Serializable) bean;
        token.bounds = wrapper.getBounds();
        return token;
    }

    public LinkToken dumpLink(Link link) {
        LinkBase lb = link.descriptor;
        LinkToken token = new LinkToken();
        boolean a = lb instanceof PropertyLink;
        boolean b = lb.dst instanceof PropertyInfo;
        token.type = a ? (b ? LinkToken.PROP_PROP : LinkToken.PROP_METHOD)
                       : (b ? LinkToken.EVENT_PROP : LinkToken.EVENT_METHOD);
        if (a) {
            PropertyLink pl = (PropertyLink) lb;
            token.srcInstance = "" + pl.src.target.hashCode();
            token.srcFeature = pl.src.name;
            token.dstInstance = "" + pl.destinationObject.hashCode();
            token.dstFeature = this.getFeatureKey(pl.dst);
        } else {
            EventLink el = (EventLink) lb;
            token.srcInstance = "" + el.src.target.hashCode();
            token.srcFeature = el.src.name;
            token.dstInstance = "" + el.destinationObject.hashCode();
            token.dstFeature = this.getFeatureKey(el.dst);
        }
        token.script = lb.script;
        return token;
    }

    public String getFeatureKey(Object feature) {
        if (feature instanceof PropertyInfo) return ((PropertyInfo) feature).name;
        if (feature instanceof EventInfo) {
            EventInfo info = (EventInfo) feature;
            return info.group.descriptor.getName() + info.name;
        }
        MethodInfo info = (MethodInfo) feature;
        return info.method + info.getSignature();
    }

    public void load(byte[] data, Canvas canvas, JetBeans core) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            this.loadOrigins(this.readOrigins(stream), core);
            ObjectInputStream in = new ClassLoaderObjectInputStream(core.loader, stream);
            SceneState state = (SceneState) in.readObject();
            this.instances = state.instances;
            this.links = state.links;
            in.close();
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException("Critical error during import", e);
        }
        HashMap<String, Object> beans = new HashMap<>();
        for (InstanceToken token : this.instances) {
            Object bean = token.ser;
            if (bean == null) bean = this.loadInstance(token, core);
            if (bean == null) continue;
            Wrapper wrapper = Wrapper.autowrap(bean);
            canvas.addItem(wrapper);
            wrapper.setBounds(token.bounds);
            beans.put(token.id, bean);
        }
        for (LinkToken token : this.links) {
            boolean a = token.type == LinkToken.PROP_PROP || token.type == LinkToken.PROP_METHOD;
            boolean b = token.type == LinkToken.PROP_PROP || token.type == LinkToken.EVENT_PROP;
            Object source = beans.get(token.srcInstance);
            Object destination = beans.get(token.dstInstance);
            if (source == null || destination == null) continue;
            InstanceInfo srcInfo = InstanceInfo.fetch(source);
            InstanceInfo dstInfo = InstanceInfo.fetch(destination);
            LinkBase link;
            Optional<?> src;
            Optional<?> dst;
            if (a) {
                src = this.getFeature(srcInfo, "prop", token.srcFeature);
                if (src.isEmpty()) continue;
                dst = this.getFeature(dstInfo, b ? "prop" : "method", token.dstFeature);
                if (dst.isEmpty()) continue;
                link = new PropertyLink(core.project, (PropertyInfo) src.get(), dst.get());
            } else {
                src = this.getFeature(srcInfo, "event", token.srcFeature);
                if (src.isEmpty()) continue;
                dst = this.getFeature(dstInfo, b ? "prop" : "method", token.dstFeature);
                if (dst.isEmpty()) continue;
                link = new EventLink(core.project, (EventInfo) src.get(), dst.get());
            }
            Linker.autoLink(canvas, src.get(), dst.get(), link, token.script);
        }
        canvas.setSelection(null);
    }

    public Optional<?> getFeature(InstanceInfo info, String type, String key) {
        if (type.equals("prop")) return info.props.stream().filter(x -> this.getFeatureKey(x).equals(key)).findAny();
        if (type.equals("method")) return info.methods.stream().filter(x -> this.getFeatureKey(x).equals(key)).findAny();
        if (type.equals("event")) {
            List<EventInfo> allEvents = new ArrayList<>();
            for (EventSetInfo group : info.events) allEvents.addAll(group.events);
            return allEvents.stream().filter(x -> this.getFeatureKey(x).equals(key)).findAny();
        }
        return Optional.empty();
    }

    public void loadOrigins(List<String> origins, JetBeans core) {
        for (String origin : origins) {
            if (!new File(origin).exists()) origin = this.findMissing(origin, core);
            if (origin == null) continue;
            try {
                core.loader.loadFile(origin);
            } catch (Exception e) {
                e = new RuntimeException("Failed to load " + origin, e);
                core.logException(e, "Import error");
            }
        }
    }

    public String findMissing(String path, JetBeans core) {
        System.err.println("MISSING IMPORT: " + path);
        return null;
    }

    public Object loadInstance(InstanceToken token, JetBeans core) {
        try {
            Class<?> klass = core.loader.loadClass(token.type);
            return klass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            RuntimeException err = new RuntimeException("Failed to load class " + token.type, e);
            core.logException(err, "Class load error");
        } catch (ReflectiveOperationException e) {
            RuntimeException err = new RuntimeException("Failed to instantiate class " + token.type, e);
            core.logException(err, "Class instantiation error");
        }
        return null;
    }
}
