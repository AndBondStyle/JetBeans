package gui;

import gui.wrapper.Wrapper;
import gui.canvas.Canvas;
import gui.link.Link;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Main custom editor widget
public class MainFrame extends JBPanel<MainFrame> {
    public MainFrame() {
        JBSplitter mainSplitter = new JBSplitter(false, "JetBeans:mainSplitter", (float) 0.25);
        JBSplitter rightSplitter = new JBSplitter(true, "JetBeans:rightSplitter", (float) 0.5);

        Canvas canvas = new Canvas();
        JComponent mockBeanLibrary = new JLabel("Bean Library");
        JComponent mockPropertyEditor = new JLabel("Property Editor");

        canvas.setBorder(BorderFactory.createLineBorder(JBColor.ORANGE, 2));
        mockBeanLibrary.setBorder(BorderFactory.createLineBorder(JBColor.GREEN, 2));
        mockPropertyEditor.setBorder(BorderFactory.createLineBorder(JBColor.CYAN, 2));

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
        this.add(mainSplitter, BorderLayout.CENTER);
        mainSplitter.setFirstComponent(canvas);
        mainSplitter.setSecondComponent(rightSplitter);
        rightSplitter.setFirstComponent(mockBeanLibrary);
        rightSplitter.setSecondComponent(mockPropertyEditor);
    }
}
