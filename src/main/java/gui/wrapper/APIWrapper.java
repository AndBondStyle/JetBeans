package gui.wrapper;

import com.intellij.icons.AllIcons;
import javax.swing.*;
import java.awt.*;

public class APIWrapper extends Wrapper {
    static int MARGIN = 10;

    public APIWrapper(Object target) {
        super(target);
    }

    @Override
    protected Component initView() {
        String name = this.target.getClass().getSimpleName();
        JLabel title = new JLabel(name, AllIcons.Nodes.Class, SwingConstants.CENTER);
        JPanel view = new JPanel(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        view.add(title, BorderLayout.CENTER);
        return view;
    }
}
