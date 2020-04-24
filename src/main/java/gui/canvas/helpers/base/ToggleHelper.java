package gui.canvas.helpers.base;

import gui.canvas.CanvasItem;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class ToggleHelper extends Helper {
    public boolean possible = false;  // Possible state flag
    public boolean active = false;    // Active state flag

    // Gets cursor for current state
    public abstract Cursor getCursor();

    // Checks if action can start
    public abstract boolean isPossible();

    // Checks if action actually started
    public abstract boolean isStarted();

    // Handles action progress
    public abstract void handleProgress();

    // Checks if action ended
    public abstract boolean isEnded();

    @Override
    public void process(MouseEvent e, CanvasItem item) {
        super.process(e, item);
        if (!this.active) {
            this.possible = this.isPossible();
            this.active = this.possible && this.isStarted();
        } else {
            this.handleProgress();
            if (this.isEnded()) this.active = false;
        }
        Cursor cursor = getCursor();
        if (cursor != null) this.parent.cursor = cursor;
    }
}
