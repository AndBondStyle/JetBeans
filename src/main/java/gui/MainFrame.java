package gui;

import gui.wrapper.Wrapper;
import gui.canvas.Canvas;
import gui.link.Link;

import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

// Main custom editor widget
public class MainFrame extends JPanel {
    public MainFrame() {
        Canvas canvas = new Canvas();

        Component temp = new JBIntSpinner(50, 0, 100, 1);
        Wrapper wrapper = new Wrapper(temp);
        canvas.addItem(wrapper);
        wrapper.setLocation(100, 100);

        for (int i = 0; i < 10; i++) {
            Component temp2 = new JBIntSpinner(50, 0, 100, 1);
            Wrapper wrapper2 = new Wrapper(temp2);
            canvas.addItem(wrapper2);
            wrapper2.setLocation(100, 200 + 100 * i);

            Link link = new Link(JBColor.MAGENTA);
            canvas.addItem(link);
            wrapper.attachLink(link, 0);
            wrapper2.attachLink(link, 1);
        }

        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
    }
}
