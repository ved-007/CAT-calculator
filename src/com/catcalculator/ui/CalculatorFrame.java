package com.catcalculator.ui;

import com.catcalculator.input.MouseOnlyFilter;
import com.catcalculator.model.CalculatorEngine;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * Main floating window for the CAT On-Screen Calculator.
 * Features an undecorated draggable window, custom title bar, always-on-top pinning,
 * opacity controls, and strict mouse-only input enforcement.
 */
public class CalculatorFrame extends JFrame {

    private final CalculatorEngine engine;
    private final MouseOnlyFilter mouseOnlyFilter;
    private final TitleBar titleBar;
    private final CalculatorPanel calculatorPanel;

    public CalculatorFrame() {
        super("CAT On-Screen Calculator");

        this.engine = new CalculatorEngine();
        this.mouseOnlyFilter = new MouseOnlyFilter(this);
        this.mouseOnlyFilter.install();

        // Undecorated window for authentic CAT floating dialog appearance
        setUndecorated(true);
        setResizable(false);
        setAlwaysOnTop(true); // Default to floating above test portals

        // Generate and set app icon
        Image icon = createAppIcon();
        setIconImage(icon);

        // Frame content layout
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(0xdadada));
        // Beveled border around entire window
        rootPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x2f6ea5), 2),
                new LineBorder(new Color(0x999999), 1)
        ));

        titleBar = new TitleBar(this);
        calculatorPanel = new CalculatorPanel(engine);

        rootPanel.add(titleBar, BorderLayout.NORTH);
        rootPanel.add(calculatorPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
        pack();

        centerOnScreen();
    }

    public CalculatorEngine getEngine() {
        return engine;
    }

    public boolean isMouseOnly() {
        return mouseOnlyFilter.isMouseOnly();
    }

    public void setMouseOnly(boolean enabled) {
        mouseOnlyFilter.setMouseOnly(enabled);
    }

    public void centerOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = Math.max(0, (screen.width - getWidth()) / 2);
        int y = Math.max(0, (screen.height - getHeight()) / 2);
        setLocation(x, y);
    }

    private Image createAppIcon() {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rounded blue background
        g2.setColor(new Color(0x2f6ea5));
        g2.fillRoundRect(2, 2, size - 4, size - 4, 16, 16);

        // Screen area
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(10, 10, size - 20, 14, 4, 4);

        // Buttons grid
        g2.setColor(new Color(0xdddddd));
        int btnW = 8;
        int btnH = 6;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (row == 2 && col == 3) {
                    g2.setColor(new Color(0x2ecc71)); // Equals green
                } else if (row == 0 && col == 0) {
                    g2.setColor(new Color(0xe74c3c)); // Red C
                } else {
                    g2.setColor(new Color(0xeeeeee));
                }
                g2.fillRoundRect(10 + col * 11, 30 + row * 9, btnW, btnH, 2, 2);
            }
        }

        g2.dispose();
        return img;
    }
}
