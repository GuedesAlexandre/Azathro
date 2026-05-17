package fr.uge.azathro.view;

import com.github.forax.zen.ApplicationContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sound.sampled.*;

public record GraphicView(int cardWidth, int cardHeight, int gap, int bottomMargin) implements View {
    private static final BufferedImage BACKGROUND = loadBackground();

    private static BufferedImage loadBackground() {
        try {
            return ImageIO.read(new File("src/fr/uge/azathro/view/assets/background.png"));
        } catch (IOException e) {
            return null;
        }
    }

    public void draw(ApplicationContext context, GraphicViewState state) {
        var screen = context.getScreenInfo();
        var width = screen.width();
        var height = screen.height();
        context.renderFrame(graphic2DObject -> render(graphic2DObject, state, width, height));
    }

    private void render(Graphics2D graphics2D, GraphicViewState state, int screenWidth, int screenHeight) {
        if (BACKGROUND != null) {
            graphics2D.drawImage(BACKGROUND, 0, 0, screenWidth, screenHeight, null);
        } else {
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.fill(new Rectangle2D.Float(0, 0, screenWidth, screenHeight));
        }
        graphics2D.setColor(Color.WHITE);
        var headerFont = new Font("SansSerif", Font.BOLD, 36);
        graphics2D.setFont(headerFont);
        graphics2D.drawString("Blind: " + state.currentBlind().name() + " | Objectif: " + state.currentBlind().score(), 30, 50);
        graphics2D.drawString("Score: " + state.playerState().totalScore(), 30, 85);
        graphics2D.drawString("Deck: " + state.deckSize() + " | Defausse: " + state.discardSize(), 30, 120);

        var planetsText = state.playerState().planetDrawed().entrySet().stream()
                .map(e -> e.getKey() + "( " + e.getKey().combination() + " )" + " x" + e.getValue())
                .collect(Collectors.joining(" | "));

        graphics2D.drawString("Planètes : " + planetsText, 30, 155);
        if (state.lastCombination() != null) {
            var comboFont = headerFont.deriveFont(28f);
            graphics2D.setFont(comboFont);
            var comboText = "Vous venez de jouer cette combinaison: " + state.lastCombination();
            graphics2D.drawString(comboText, 30, screenHeight - bottomMargin - cardHeight - 40);
            graphics2D.setFont(headerFont);
        }

        var cardFont = headerFont.deriveFont(20f);
        graphics2D.setFont(cardFont);

        drawHand(graphics2D, state, screenWidth, screenHeight);
    }

    private void drawHand(Graphics2D graphics2D, GraphicViewState state, int screenWidth, int screenHeight) {
        var handSize = state.currentHand().size();
        var xOrigin = xOriginFor(screenWidth, handSize);
        var y = yOriginFor(screenHeight);
        var raise = 30;
        IntStream.range(0, handSize).forEach(index -> {
            var x = xOrigin + index * (cardWidth + gap);
            var selected = state.selectedIndexes().contains(index);
           var yCard = selected ? y - raise : y;
            graphics2D.setColor(Color.WHITE);
            graphics2D.fill(new Rectangle2D.Float(x, yCard, cardWidth, cardHeight));
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawRect(x, yCard, cardWidth, cardHeight);
            graphics2D.drawString(state.currentHand().get(index).toString(), x + 12, yCard + 28);
        });
    }

    public int cardIndexFromX(float x, int screenWidth, int handSize) {
        if (handSize <= 0) return -1;
        var xOrigin = xOriginFor(screenWidth, handSize);
        var totalWidth = totalWidthFor(handSize);
        // Vérifie si le clic est en dehors de la zone globale de la main
        if (x < xOrigin || x > xOrigin + totalWidth) {
            return -1;
        }
        // Calcul de l'index basé sur la position relative
        var cardIndex = (int)((x - xOrigin) / (cardWidth + gap));
        // Sécurise l'index entre 0 et handSize - 1
        return Math.clamp(cardIndex, 0, handSize - 1);
    }

    public boolean isInsideCardArea(float x, float y, int screenWidth, int screenHeight, int handSize) {
        if (handSize <= 0) return false;
        var totalWidth = totalWidthFor(handSize);
        var xOrigin = xOriginFor(screenWidth, handSize);
        var yOrigin = yOriginFor(screenHeight);
        return x >= xOrigin && x <= xOrigin + totalWidth
                && y >= yOrigin && y <= yOrigin + cardHeight;
    }

    private int totalWidthFor(int handSize) {
        return handSize * cardWidth + Math.max(0, handSize - 1) * gap;
    }

    private int xOriginFor(int screenWidth, int handSize) {
        var totalWidth = totalWidthFor(handSize);
        return (screenWidth - totalWidth) / 2;
    }

    private int yOriginFor(int screenHeight) {
        return screenHeight - bottomMargin - cardHeight;
    }



}
