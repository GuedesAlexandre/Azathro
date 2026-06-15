package fr.uge.azathro.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

final class DrawUtils {
    private DrawUtils() {}

    static void drawFullBackground(Graphics2D graphics2D, int screenWidth, int screenHeight,
                                   Color fallbackColor) {
        var background = AssetManager.background();
        if (background != null) {
            graphics2D.drawImage(background, 0, 0, screenWidth, screenHeight, null);
        } else {
            graphics2D.setColor(fallbackColor);
            graphics2D.fill(new Rectangle2D.Float(0, 0, screenWidth, screenHeight));
        }
    }

    static void drawOverlay(Graphics2D graphics2D, int screenWidth, int screenHeight, Color color) {
        graphics2D.setColor(color);
        graphics2D.fillRect(0, 0, screenWidth, screenHeight);
    }

    static void drawPanel(Graphics2D graphics2D, int x, int y, int width, int height,
                          Color backgroundColor, Color borderColor) {
        graphics2D.setColor(new Color(0, 0, 0, 100));
        graphics2D.fillRoundRect(x + (int) (x * 0.01d), y + (int) (y * 0.01d), width, height, 25, 25);
        graphics2D.setColor(backgroundColor);
        graphics2D.fillRoundRect(x, y, width, height, 25, 25);
        graphics2D.setColor(borderColor);
        graphics2D.setStroke(new BasicStroke(4));
        graphics2D.drawRoundRect(x, y, width, height, 25, 25);
    }

    static void drawCenteredText(Graphics2D graphics2D, String text, int screenWidth,
                                 int screenHeight, Color color) {
        graphics2D.setColor(color);
        graphics2D.setFont(AssetManager.titleFont());
        var fontMetrics = graphics2D.getFontMetrics();
        var centeredTextX = (screenWidth - fontMetrics.stringWidth(text)) / 2f;
        var centeredTextY = screenHeight / 2f + fontMetrics.getAscent() / 2f - fontMetrics.getDescent() / 2f;
        graphics2D.drawString(text, centeredTextX, centeredTextY);
    }
}
