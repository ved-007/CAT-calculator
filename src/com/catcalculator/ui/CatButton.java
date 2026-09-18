package com.catcalculator.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Custom button component replicating the official CAT on-screen calculator button style:
 * beveled borders, 3D tactile press effect, custom colors, and smooth rendering.
 */
public class CatButton extends JButton {

    public enum ButtonType {
        NUMERIC,
        OPERATOR,
        COMMAND,    // C, Backspace, +/-, sqrt (Red)
        EQUALS,     // Enter = (Green)
        MEMORY      // MC, MR, MS, M+, M-
    }

    private final ButtonType type;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public CatButton(String text, ButtonType type) {
        super(text);
        this.type = type;

        setFocusable(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setMargin(new Insets(0, 0, 0, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Typography matching CAT calci
        switch (type) {
            case EQUALS:
                setFont(new Font("Arial", Font.BOLD, 19));
                break;
            case MEMORY:
                setFont(new Font("Arial", Font.BOLD, 10));
                break;
            case COMMAND:
                setFont(new Font("Arial", Font.BOLD, "←".equals(text) ? 17 : ("√".equals(text) ? 16 : 12)));
                break;
            case OPERATOR:
                setFont(new Font("Arial", Font.BOLD, "*".equals(text) ? 18 : 14));
                break;
            default:
                setFont(new Font("Arial", Font.BOLD, 13));
                break;
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public ButtonType getType() {
        return type;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Color palettes from CAT website CSS
        Color bgColor;
        Color borderColor;
        Color shadowBorderColor;
        Color textColor;

        switch (type) {
            case COMMAND: // Red (#e74c3c / #c0392b)
                bgColor = isPressed ? new Color(0xb83227) : (isHovered ? new Color(0xd63031) : new Color(0xe74c3c));
                borderColor = new Color(0xc0392b);
                shadowBorderColor = new Color(0x96281b);
                textColor = Color.WHITE;
                break;

            case EQUALS: // Green (#2ecc71 / #27ae60)
                bgColor = isPressed ? new Color(0x219653) : (isHovered ? new Color(0x27ae60) : new Color(0x2ecc71));
                borderColor = new Color(0x27ae60);
                shadowBorderColor = new Color(0x1e8449);
                textColor = Color.WHITE;
                break;

            case MEMORY: // Light grayish
                bgColor = isPressed ? new Color(0xdfdfdf) : (isHovered ? new Color(0xe8e8e8) : new Color(0xf4f4f4));
                borderColor = new Color(0xb5b5b5);
                shadowBorderColor = new Color(0x999999);
                textColor = new Color(0x333333);
                break;

            case OPERATOR:
            case NUMERIC:
            default: // #f1f1f1 with #aaa border
                bgColor = isPressed ? new Color(0xdcdcdc) : (isHovered ? new Color(0xeaeaea) : new Color(0xf7f7f7));
                borderColor = new Color(0xbababa);
                shadowBorderColor = new Color(0x9a9a9a);
                textColor = new Color(0x333333);
                break;
        }

        int arc = 4;
        int shadowHeight = isPressed ? 1 : 3;
        int yOffset = isPressed ? 2 : 0;

        // Draw button shadow / bottom bevel
        g2.setColor(shadowBorderColor);
        g2.fillRoundRect(0, yOffset, width, height - yOffset, arc, arc);

        // Draw main button face
        g2.setColor(bgColor);
        g2.fillRoundRect(0, yOffset, width, height - shadowHeight - yOffset, arc, arc);

        // Draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, yOffset, width - 1, height - shadowHeight - yOffset, arc, arc);

        // Draw label text
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics(getFont());
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = (width - textWidth) / 2;
        int textY = ((height - shadowHeight - yOffset + textHeight) / 2) + yOffset - 1;

        if ("*".equals(text)) {
            textY += 3; // Slight visual alignment for asterisk
        } else if ("←".equals(text)) {
            textY -= 1;
        }

        g2.drawString(text, textX, textY);
        g2.dispose();
    }
}
