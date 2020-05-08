package core.serialization;

import java.awt.*;
import java.io.Serializable;

public class InstanceToken implements Serializable {
    public String id;
    public String loader;
    public String origin;
    public String type;
    public Component component;
    public Point location;
    public Dimension size;
}
