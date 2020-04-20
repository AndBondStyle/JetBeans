package gui.canvas;

import com.intellij.ui.components.JBLayeredPane;

import java.awt.*;

// Main canvas widget, handling move/select/resize actions
public class Canvas extends JBLayeredPane {
    Content content = new Content();

    public Canvas() {
        this.setLayout(new BorderLayout());
        this.add(content, BorderLayout.CENTER);
    }
}
