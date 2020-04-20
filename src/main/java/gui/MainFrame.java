package gui;

import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBPanel;
import gui.canvas.Canvas;

import javax.swing.*;
import java.awt.*;

// Main custom editor widget
public class MainFrame extends JBPanel<MainFrame> {
    public MainFrame() {
        JBSplitter mainSplitter = new JBSplitter(false, "JetBeans:mainSplitter", (float) 0.25);
        JBSplitter rightSplitter = new JBSplitter(true, "JetBeans:rightSplitter", (float) 0.5);

        JComponent mockCanvas = new Canvas();
        JComponent mockBeanLibrary = new JLabel("Bean Library");
        JComponent mockPropertyEditor = new JLabel("Property Editor");

        mockCanvas.setBorder(BorderFactory.createLineBorder(JBColor.ORANGE, 2));
        mockBeanLibrary.setBorder(BorderFactory.createLineBorder(JBColor.GREEN, 2));
        mockPropertyEditor.setBorder(BorderFactory.createLineBorder(JBColor.CYAN, 2));

        this.setLayout(new BorderLayout());
        this.add(mainSplitter, BorderLayout.CENTER);
        mainSplitter.setFirstComponent(mockCanvas);
        mainSplitter.setSecondComponent(rightSplitter);
        rightSplitter.setFirstComponent(mockBeanLibrary);
        rightSplitter.setSecondComponent(mockPropertyEditor);
    }
}
