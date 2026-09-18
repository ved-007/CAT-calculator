package com.catcalculator.input;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;

/**
 * Enforces the strict CAT exam rule: "Only the mouse will work in it, no other input".
 * When active, all keyboard events directed to the calculator window are intercepted and discarded.
 */
public class MouseOnlyFilter implements KeyEventDispatcher {
    private final Window targetWindow;
    private boolean mouseOnly = true;

    public MouseOnlyFilter(Window targetWindow) {
        this.targetWindow = targetWindow;
    }

    public void install() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    public void uninstall() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    }

    public boolean isMouseOnly() {
        return mouseOnly;
    }

    public void setMouseOnly(boolean mouseOnly) {
        this.mouseOnly = mouseOnly;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (!mouseOnly) {
            return false;
        }

        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, targetWindow)) {
            // Strictly consume keyboard events for the calculator window
            e.consume();
            return true;
        }

        return false;
    }
}
