package fr.uge.azathro.view;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.types.planet.Planet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GraphicView implements View {
    private final int gap;
    private final int bottomMargin;
    private ApplicationContext context;
    private static final BufferedImage BACKGROUND = loadBackground();
    private static final BufferedImage LOGO = loadLogo();

    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 36);
    private static final Font COMBO_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font CARD_FONT = new Font("SansSerif", Font.PLAIN, 18);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 30);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 72);

    public GraphicView(int gap, int bottomMargin) {
        this.gap = gap;
        this.bottomMargin = bottomMargin;
    }

    private static BufferedImage loadBackground() {
        try {
            return ImageIO.read(new File("src/fr/uge/azathro/view/assets/background.png"));
        } catch (IOException e) {
            return null;
        }
    }

    private static BufferedImage loadLogo() {
        try {
            return ImageIO.read(new File("assets/logo.png"));
        } catch (IOException e) {
            return null;
        }
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void showIntro() {
        if (context == null) return;
        renderIntro();
        waitForIntroAction();
    }

    private void renderIntro() {
        var screen = context.getScreenInfo();
        context.renderFrame(g -> {
            drawFullBackground(g, screen.width(), screen.height(), Color.BLACK);
            drawLogo(g, screen.width(), screen.height());
            drawIntroText(g, screen.width(), screen.height());
        });
    }

    private void drawFullBackground(Graphics2D graphics2D, int width, int height, Color fallbackColor) {
        if (BACKGROUND != null) {
            graphics2D.drawImage(BACKGROUND, 0, 0, width, height, null);
        } else {
            graphics2D.setColor(fallbackColor);
            graphics2D.fill(new Rectangle2D.Float(0, 0, width, height));
        }
    }

    private void drawLogo(Graphics2D graphics2D, int width, int height) {
        if (LOGO == null) return;
        int targetWidth = (width * 60) / 100;
        double scale = Math.min(1.0, (double) targetWidth / LOGO.getWidth());
        int sw = (int) (LOGO.getWidth() * scale);
        int sh = (int) (LOGO.getHeight() * scale);
        graphics2D.drawImage(LOGO, (width - sw) / 2, (height - sh) / 2 - 50, sw, sh, null);
    }

    private void drawIntroText(Graphics2D graphics2D, int width, int height) {
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(BUTTON_FONT);
        String text = "Appuyez sur ESPACE pour commencer";
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.drawString(text, (width - fm.stringWidth(text)) / 2, height - 100);
    }

    private void waitForIntroAction() {
        while (true) {
            var event = context.pollOrWaitEvent(100);
            if (event == null) continue;
            switch (event) {
                case KeyboardEvent ke -> {
                    switch (ke.key()) {
                        case SPACE -> {
                            return;
                        }
                        case ESCAPE -> {
                            context.dispose();
                            System.exit(0);
                        }
                        default -> {
                        }
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void showGameState(GameState gameState, Set<Integer> selectedIndexes, String lastCombination, Integer lastScore) {
        if (context == null) return;
        var screen = context.getScreenInfo();
        context.renderFrame(g -> render(g, gameState, selectedIndexes, lastCombination, lastScore, screen.width(), screen.height()));
    }

    private void render(Graphics2D graphics2D, GameState gameState, Set<Integer> selectedIndexes, String lastCombination, Integer lastScore, int width, int height) {
        int cardWidth = (width * 10) / 100;
        int cardHeight = (height * 25) / 100;
        drawFullBackground(graphics2D, width, height, Color.DARK_GRAY);
        drawUIHeader(graphics2D, gameState);
        drawPlanetsInfo(graphics2D, gameState);
        if (lastCombination != null) {
            drawLastCombinationMessage(graphics2D, lastCombination, lastScore, height, cardHeight);
        }
        drawHand(graphics2D, gameState, selectedIndexes, width, height, cardWidth, cardHeight);
    }

    private void drawUIHeader(Graphics2D g, GameState gameState) {
        g.setColor(Color.WHITE);
        g.setFont(HEADER_FONT);
        g.drawString("Blind: " + gameState.currentBlind().name() + " | Objectif: " + gameState.currentBlind().score(), 30, 50);
        g.drawString("Score: " + gameState.playerState().totalScore(), 30, 85);
        g.drawString("Deck: " + gameState.deck().size() + " | Mains restantes: " + gameState.playerState().handCount(), 30, 120);
    }

    private void drawPlanetsInfo(Graphics2D graphics2D, GameState gameState) {
        var planetsText = gameState.playerState().planetDrawed().entrySet().stream()
                .map(e -> e.getKey() + "( " + e.getKey().combination() + " )" + " x" + e.getValue())
                .collect(Collectors.joining(" | "));
        graphics2D.drawString("Planètes : " + planetsText, 30, 155);
    }

    private void drawLastCombinationMessage(Graphics2D g, String combination, Integer score, int height, int ch) {
        g.setFont(COMBO_FONT);
        var text = "Vous venez de jouer : " + combination + " (+ " + score + ")";
        g.drawString(text, 30, height - bottomMargin - ch - 40);
    }

    private void drawHand(Graphics2D graphics2D, GameState gameState, Set<Integer> selectedIndexes, int width, int height, int cardWidth, int cardHeight) {
        var hand = gameState.currentHand();
        var xo = xOriginFor(width, hand.size(), cardWidth);
        var yo = yOriginFor(height, cardHeight);
        graphics2D.setFont(CARD_FONT);
        IntStream.range(0, hand.size()).forEach(i -> {
            int x = xo + i * (cardWidth + gap);
            int y = selectedIndexes.contains(i) ? yo - 30 : yo;
            graphics2D.setColor(Color.WHITE);
            graphics2D.fill(new Rectangle2D.Float(x, y, cardWidth, cardHeight));
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawRect(x, y, cardWidth, cardHeight);
            graphics2D.drawString(hand.get(i).toString(), x + 12, y + 28);
        });
    }

    @Override
    public void showBlindSuccess() {
        if (context == null) return;
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            drawOverlay(graphics2D, screen.width(), screen.height(), new Color(255, 255, 255, 150));
            drawCenteredText(graphics2D, "BLIND REUSSI !", screen.width(), screen.height(), Color.GREEN);
        });
        sleep(1000);
    }

    @Override
    public void showGameOver() {
        if (context == null) return;
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            drawOverlay(graphics2D, screen.width(), screen.height(), new Color(0, 0, 0, 150));
            drawCenteredText(graphics2D, "GAME OVER", screen.width(), screen.height(), Color.RED);
        });
        sleep(2000);
    }

    @Override
    public void showGameEnd() {
        if (context == null) return;
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            drawOverlay(graphics2D, screen.width(), screen.height(), new Color(255, 215, 0, 180));
            drawCenteredText(graphics2D, "VICTOIRE !", screen.width(),  screen.height(), Color.WHITE);
        });
        sleep(3000);
    }

    @Override
    public void showPlanetDrawn(Planet planet) {
        if (context == null) return;
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            drawOverlay(graphics2D,  screen.width(), screen.height(), new Color(0, 0, 255, 150));
            drawCenteredText(graphics2D, "Planète tirée : " + planet.name(),  screen.width(), screen.height(), Color.WHITE);
        });
        sleep(1500);
    }

    private void drawOverlay(Graphics2D graphics2D, int width, int height, Color color) {
        graphics2D.setColor(color);
        graphics2D.fillRect(0, 0, width, height);
    }

    private void drawCenteredText(Graphics2D graphics2D, String text, int width, int height, Color color) {
        graphics2D.setColor(color);
        graphics2D.setFont(GraphicView.TITLE_FONT);
       var fm = graphics2D.getFontMetrics();
        graphics2D.drawString(text, (width - fm.stringWidth(text)) / 2, height / 2);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException _) {
        }
    }

    public int cardIndexFromX(float x, int width, int handSize) {
        if (handSize <= 0) return -1;
        var cardWidth = (width * 12) / 100;
        var xo = xOriginFor(width, handSize, cardWidth);
        if (x < xo || x > xo + totalWidthFor(handSize, cardWidth)) return -1;
        var idx = (int) ((x - xo) / (cardWidth + gap));
        return Math.clamp(idx, 0, handSize - 1);
    }

    public boolean isInsideCardArea(float x, float y, int width, int height, int handSize) {
        if (handSize <= 0) return false;
        int cardWidth = (width * 12) / 100;
        int carHeight = (height * 30) / 100;
        int xo = xOriginFor(width, handSize, cardWidth);
        int yo = yOriginFor(height, carHeight);
        return x >= xo && x <= xo + totalWidthFor(handSize, cardWidth) && y >= yo && y <= yo + carHeight;
    }

    private int totalWidthFor(int handSize, int cardWidth) {
        return handSize * cardWidth + Math.max(0, handSize - 1) * gap;
    }

    private int xOriginFor(int width, int handSize, int cardWitdh) {
        return (width - totalWidthFor(handSize, cardWitdh)) / 2;
    }

    private int yOriginFor(int height, int cardHeight) {
        return height - bottomMargin - cardHeight;
    }
}
