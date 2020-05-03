package gui.propeditor.tree;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import java.awt.*;

public class PropertyTreeUI extends BasicTreeUI {
    static int RIGHT_OFFSET = 10;

    private Component wrapper;

    public PropertyTreeUI(Component wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {
            @Override
            public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
                Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                dimensions.width = wrapper.getWidth() - getRowX(row, depth) - RIGHT_OFFSET;
                return dimensions;
            }
        };
    }
}
