package gui.canvas;

public interface CanvasItem {
    boolean isResizable();

    boolean isMovable();

    boolean isSelectable();

    void setSelected(boolean selected);

    boolean isDeletable();
}
