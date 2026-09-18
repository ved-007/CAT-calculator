package com.catcalculator.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Custom draggable header replicating the CAT on-screen calculator titlebar.
 * Vector-drawn control icons ensure pixel-perfect clarity on all DPI scalings.
 */
public class TitleBar extends JPanel {

    private final JFrame parentFrame;
    private final CalculatorFrame calculatorFrame;
    private Point initialClick;
    private HeaderButton pinBtn;

    public TitleBar(CalculatorFrame calculatorFrame) {
        this.calculatorFrame = calculatorFrame;
        this.parentFrame = calculatorFrame;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(240, 30));
        setBackground(new Color(0x2f6ea5)); // Official CAT blue

        // Left: Title
        JLabel titleLabel = new JLabel("  Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.WEST);

        // Right: Window controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 3));
        controlsPanel.setOpaque(false);

        // Pin button (Always on Top)
        pinBtn = new HeaderButton(HeaderIconType.PIN, "Always on Top (Toggle)");
        pinBtn.addActionListener(e -> {
            boolean current = calculatorFrame.isAlwaysOnTop();
            calculatorFrame.setAlwaysOnTop(!current);
            updatePinState(!current);
        });

        // Options button
        HeaderButton optionsBtn = new HeaderButton(HeaderIconType.OPTIONS, "Options Menu");
        optionsBtn.addActionListener(e -> showOptionsMenu(optionsBtn));

        // Minimize button
        HeaderButton minBtn = new HeaderButton(HeaderIconType.MINIMIZE, "Minimize");
        minBtn.addActionListener(e -> parentFrame.setState(Frame.ICONIFIED));

        // Close button
        HeaderButton closeBtn = new HeaderButton(HeaderIconType.CLOSE, "Close");
        closeBtn.addActionListener(e -> System.exit(0));

        controlsPanel.add(pinBtn);
        controlsPanel.add(optionsBtn);
        controlsPanel.add(minBtn);
        controlsPanel.add(closeBtn);
        add(controlsPanel, BorderLayout.EAST);

        // Dragging listeners
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    int thisX = parentFrame.getLocation().x;
                    int thisY = parentFrame.getLocation().y;
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;
                    parentFrame.setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        };

        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
        titleLabel.addMouseListener(dragListener);
        titleLabel.addMouseMotionListener(dragListener);

        updatePinState(calculatorFrame.isAlwaysOnTop());
    }

    public void updatePinState(boolean pinned) {
        pinBtn.setPinned(pinned);
        pinBtn.setToolTipText(pinned ? "Always on Top: ON (Click to unpin)" : "Always on Top: OFF (Click to pin)");
        pinBtn.repaint();
    }

    private enum HeaderIconType {
        PIN,
        OPTIONS,
        MINIMIZE,
        CLOSE
    }

    private static class HeaderButton extends JButton {
        private final HeaderIconType iconType;
        private boolean isPinned = false;

        public HeaderButton(HeaderIconType iconType, String tooltip) {
            this.iconType = iconType;
            setPreferredSize(new Dimension(24, 22));
            setFocusPainted(false);
            setFocusable(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);
        }

        public void setPinned(boolean pinned) {
            this.isPinned = pinned;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (getModel().isPressed()) {
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(1, 1, w - 2, h - 2, 4, 4);
            } else if (getModel().isRollover()) {
                g2.setColor(iconType == HeaderIconType.CLOSE ? new Color(220, 53, 69, 200) : new Color(255, 255, 255, 60));
                g2.fillRoundRect(1, 1, w - 2, h - 2, 4, 4);
            }

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            int cy = h / 2;

            switch (iconType) {
                case CLOSE:
                    // Diagonal 'X'
                    g2.drawLine(cx - 4, cy - 4, cx + 4, cy + 4);
                    g2.drawLine(cx + 4, cy - 4, cx - 4, cy + 4);
                    break;

                case MINIMIZE:
                    // Flat horizontal bar
                    g2.drawLine(cx - 5, cy + 4, cx + 5, cy + 4);
                    break;

                case PIN:
                    if (isPinned) {
                        g2.setColor(new Color(0xffd700)); // Gold when pinned
                    }
                    // Pin head & needle
                    g2.fillOval(cx - 3, cy - 6, 6, 6);
                    g2.fillRect(cx - 4, cy - 1, 8, 3);
                    g2.drawLine(cx, cy + 2, cx, cy + 6);
                    break;

                case OPTIONS:
                    // 3 horizontal dots or bars
                    g2.fillOval(cx - 5, cy - 1, 2, 2);
                    g2.fillOval(cx, cy - 1, 2, 2);
                    g2.fillOval(cx + 5, cy - 1, 2, 2);
                    break;
            }

            g2.dispose();
        }
    }

    private void showOptionsMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        // Always on Top
        JCheckBoxMenuItem pinItem = new JCheckBoxMenuItem("Always on Top", calculatorFrame.isAlwaysOnTop());
        pinItem.addActionListener(e -> {
            boolean newState = pinItem.isSelected();
            calculatorFrame.setAlwaysOnTop(newState);
            updatePinState(newState);
        });
        menu.add(pinItem);

        // Strict Mouse-Only Mode
        JCheckBoxMenuItem mouseOnlyItem = new JCheckBoxMenuItem("Strict Mouse-Only Mode (CAT Rule)", calculatorFrame.isMouseOnly());
        mouseOnlyItem.addActionListener(e -> calculatorFrame.setMouseOnly(mouseOnlyItem.isSelected()));
        menu.add(mouseOnlyItem);

        menu.addSeparator();

        // Opacity Submenu
        JMenu opacityMenu = new JMenu("Window Opacity");
        float[] opacities = {1.0f, 0.90f, 0.80f, 0.70f};
        String[] opacityLabels = {"100% (Solid)", "90%", "80%", "70% (Translucent)"};
        ButtonGroup opacityGroup = new ButtonGroup();
        for (int i = 0; i < opacities.length; i++) {
            float val = opacities[i];
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(opacityLabels[i], Math.abs(calculatorFrame.getOpacity() - val) < 0.05);
            item.addActionListener(e -> calculatorFrame.setOpacity(val));
            opacityGroup.add(item);
            opacityMenu.add(item);
        }
        menu.add(opacityMenu);

        menu.addSeparator();

        // Center on screen
        JMenuItem centerItem = new JMenuItem("Center Window");
        centerItem.addActionListener(e -> calculatorFrame.centerOnScreen());
        menu.add(centerItem);

        // Reset
        JMenuItem resetItem = new JMenuItem("Reset Calculator State");
        resetItem.addActionListener(e -> {
            calculatorFrame.getEngine().allClear();
            calculatorFrame.repaint();
        });
        menu.add(resetItem);

        menu.addSeparator();

        JMenuItem aboutItem = new JMenuItem("About CAT Virtual Calculator");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                calculatorFrame,
                "CAT On-Screen Calculator\n" +
                "Authentic simulation of the official CAT exam virtual calculator.\n\n" +
                "Features:\n" +
                "• Strict mouse-only mode (keyboard typing blocked)\n" +
                "• Memory registers (MC, MR, MS, M+, M-)\n" +
                "• Unary & Binary operators with CAT precedence\n" +
                "• Always on Top toggle for overlaying on mock test portals",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        ));
        menu.add(aboutItem);

        menu.show(invoker, 0, invoker.getHeight());
    }
}
