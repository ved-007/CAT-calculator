package com.catcalculator.ui;

import com.catcalculator.model.CalculatorEngine;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Main calculator panel containing the dual displays and the exact 5x6 button grid.
 */
public class CalculatorPanel extends JPanel {

    private final CalculatorEngine engine;
    private final JLabel historyDisplay;
    private final JLabel mainDisplay;
    private final JLabel memoryBadge;

    public CalculatorPanel(CalculatorEngine engine) {
        this.engine = engine;
        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(0xdadada)); // CAT light grey container
        setBorder(new EmptyBorder(8, 8, 10, 8));
        setPreferredSize(new Dimension(245, 270));

        // 1. Dual Display Area using GridLayout for identical widths
        JPanel displayContainer = new JPanel(new GridLayout(2, 1, 0, 4));
        displayContainer.setOpaque(false);

        // Upper History Display
        historyDisplay = new JLabel(" ");
        historyDisplay.setFont(new Font("Arial", Font.PLAIN, 12));
        historyDisplay.setForeground(new Color(0x555555));
        historyDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        historyDisplay.setOpaque(true);
        historyDisplay.setBackground(Color.WHITE);
        historyDisplay.setPreferredSize(new Dimension(225, 24));
        historyDisplay.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xaaaaaa), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        // Lower Main Display
        JPanel mainDisplayPanel = new JPanel(new BorderLayout());
        mainDisplayPanel.setBackground(Color.WHITE);
        mainDisplayPanel.setPreferredSize(new Dimension(225, 30));
        mainDisplayPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xaaaaaa), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));

        memoryBadge = new JLabel("M ");
        memoryBadge.setFont(new Font("Arial", Font.BOLD, 12));
        memoryBadge.setForeground(new Color(0x2f6ea5)); // Blue M indicator
        memoryBadge.setVisible(false);

        mainDisplay = new JLabel("0");
        mainDisplay.setFont(new Font("Arial", Font.BOLD, 19));
        mainDisplay.setForeground(new Color(0x111111));
        mainDisplay.setHorizontalAlignment(SwingConstants.RIGHT);

        mainDisplayPanel.add(memoryBadge, BorderLayout.WEST);
        mainDisplayPanel.add(mainDisplay, BorderLayout.CENTER);

        displayContainer.add(historyDisplay);
        displayContainer.add(mainDisplayPanel);

        add(displayContainer, BorderLayout.NORTH);

        // 2. Button Grid Area using GridBagLayout
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Row 0: MC, MR, MS, M+, M-
        String[] memLabels = {"MC", "MR", "MS", "M+", "M-"};
        for (int c = 0; c < 5; c++) {
            String label = memLabels[c];
            CatButton btn = new CatButton(label, CatButton.ButtonType.MEMORY);
            btn.setPreferredSize(new Dimension(42, 24));
            btn.addActionListener(e -> {
                engine.inputMemory(label);
                updateDisplay();
            });
            gbc.gridx = c;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gridPanel.add(btn, gbc);
        }

        // Row 1: Backspace (← span 2 cols), C, +/-, √
        CatButton backBtn = new CatButton("←", CatButton.ButtonType.COMMAND);
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(86, 26));
        backBtn.addActionListener(e -> {
            engine.backspace();
            updateDisplay();
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gridPanel.add(backBtn, gbc);

        CatButton clrBtn = new CatButton("C", CatButton.ButtonType.COMMAND);
        clrBtn.setPreferredSize(new Dimension(42, 26));
        clrBtn.addActionListener(e -> {
            engine.allClear();
            updateDisplay();
        });
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gridPanel.add(clrBtn, gbc);

        CatButton signBtn = new CatButton("+/-", CatButton.ButtonType.COMMAND);
        signBtn.setPreferredSize(new Dimension(42, 26));
        signBtn.addActionListener(e -> {
            engine.inputUnaryOp("+/-");
            updateDisplay();
        });
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gridPanel.add(signBtn, gbc);

        CatButton sqrtBtn = new CatButton("√", CatButton.ButtonType.COMMAND);
        sqrtBtn.setFont(new Font("Arial", Font.BOLD, 15));
        sqrtBtn.setPreferredSize(new Dimension(42, 26));
        sqrtBtn.addActionListener(e -> {
            engine.inputUnaryOp("sqrt");
            updateDisplay();
        });
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gridPanel.add(sqrtBtn, gbc);

        // Row 2: 7, 8, 9, /, %
        String[] r2 = {"7", "8", "9", "/", "%"};
        for (int c = 0; c < 5; c++) {
            String text = r2[c];
            boolean isDigit = Character.isDigit(text.charAt(0));
            CatButton btn = new CatButton(text, isDigit ? CatButton.ButtonType.NUMERIC : CatButton.ButtonType.OPERATOR);
            btn.setPreferredSize(new Dimension(42, 26));
            btn.addActionListener(e -> {
                if (isDigit) {
                    engine.inputDigit(text);
                } else {
                    engine.inputBinaryOp(text);
                }
                updateDisplay();
            });
            gbc.gridx = c;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gridPanel.add(btn, gbc);
        }

        // Row 3: 4, 5, 6, *, 1/x
        String[] r3 = {"4", "5", "6", "*", "1/x"};
        for (int c = 0; c < 5; c++) {
            String text = r3[c];
            boolean isDigit = Character.isDigit(text.charAt(0));
            CatButton btn = new CatButton(text, isDigit ? CatButton.ButtonType.NUMERIC : CatButton.ButtonType.OPERATOR);
            btn.setPreferredSize(new Dimension(42, 26));
            btn.addActionListener(e -> {
                if (isDigit) {
                    engine.inputDigit(text);
                } else if ("1/x".equals(text)) {
                    engine.inputUnaryOp("1/x");
                } else {
                    engine.inputBinaryOp(text);
                }
                updateDisplay();
            });
            gbc.gridx = c;
            gbc.gridy = 3;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gridPanel.add(btn, gbc);
        }

        // Row 4: 1, 2, 3, -  and Enter (= spanning row 4 & 5)
        String[] r4 = {"1", "2", "3", "-"};
        for (int c = 0; c < 4; c++) {
            String text = r4[c];
            boolean isDigit = Character.isDigit(text.charAt(0));
            CatButton btn = new CatButton(text, isDigit ? CatButton.ButtonType.NUMERIC : CatButton.ButtonType.OPERATOR);
            btn.setPreferredSize(new Dimension(42, 26));
            btn.addActionListener(e -> {
                if (isDigit) {
                    engine.inputDigit(text);
                } else {
                    engine.inputBinaryOp(text);
                }
                updateDisplay();
            });
            gbc.gridx = c;
            gbc.gridy = 4;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gridPanel.add(btn, gbc);
        }

        // Equals (=) button spanning row 4 and row 5 on column 4
        CatButton equalsBtn = new CatButton("=", CatButton.ButtonType.EQUALS);
        equalsBtn.setPreferredSize(new Dimension(42, 54));
        equalsBtn.addActionListener(e -> {
            engine.calculateEquals();
            updateDisplay();
        });
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 2; // Span 2 rows
        gridPanel.add(equalsBtn, gbc);

        // Row 5: 0 (span 2 cols), ., +
        CatButton zeroBtn = new CatButton("0", CatButton.ButtonType.NUMERIC);
        zeroBtn.setPreferredSize(new Dimension(86, 26));
        zeroBtn.addActionListener(e -> {
            engine.inputDigit("0");
            updateDisplay();
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.gridheight = 1;
        gridPanel.add(zeroBtn, gbc);

        CatButton dotBtn = new CatButton(".", CatButton.ButtonType.NUMERIC);
        dotBtn.setFont(new Font("Arial", Font.BOLD, 16));
        dotBtn.setPreferredSize(new Dimension(42, 26));
        dotBtn.addActionListener(e -> {
            engine.inputDigit(".");
            updateDisplay();
        });
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gridPanel.add(dotBtn, gbc);

        CatButton plusBtn = new CatButton("+", CatButton.ButtonType.OPERATOR);
        plusBtn.setPreferredSize(new Dimension(42, 26));
        plusBtn.addActionListener(e -> {
            engine.inputBinaryOp("+");
            updateDisplay();
        });
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gridPanel.add(plusBtn, gbc);

        add(gridPanel, BorderLayout.CENTER);

        updateDisplay();
    }

    public void updateDisplay() {
        String expr = engine.getExpressionString();
        historyDisplay.setText(expr.isEmpty() ? " " : expr);

        mainDisplay.setText(engine.getCurrentInput());
        memoryBadge.setVisible(engine.hasMemory());
    }
}
