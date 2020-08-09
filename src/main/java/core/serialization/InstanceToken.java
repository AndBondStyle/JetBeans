package core.serialization;

import java.awt.*;
import java.io.Serializable;

public class InstanceToken implements Serializable {
    public String id;
    public String type;
    public String origin;
    public Serializable ser;
    public Rectangle bounds;
}
