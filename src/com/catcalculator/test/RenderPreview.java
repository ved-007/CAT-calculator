package com.catcalculator.test;

import com.catcalculator.ui.CalculatorFrame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class RenderPreview {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                CalculatorFrame frame = new CalculatorFrame();
                frame.setVisible(true);
                int w = frame.getWidth();
                int h = frame.getHeight();

                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = img.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                frame.printAll(g2);
                g2.dispose();

                File out = new File("cat_calculator_render.png");
                ImageIO.write(img, "PNG", out);
                System.out.println("Preview successfully rendered to: " + out.getAbsolutePath() + " (" + w + "x" + h + ")");
                frame.dispose();
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        });
    }
}
