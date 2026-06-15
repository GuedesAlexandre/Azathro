package fr.uge.azathro.view;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.types.planet.Planet;

import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public final class GraphicView implements View {
    private ApplicationContext context;

    private static final int MESSAGE_DURATION = 1500;
    private static final double HEADER_W_RATIO = 0.48d;
    private static final double HEADER_H_RATIO = 0.55d;
    private static final double HELP_BUTTON_RATIO = 0.05d;
    private static final double PANEL_MARGIN_X = 0.02d;
    private static final double PANEL_MARGIN_Y = 0.02d;
    private static final double TEXT_MARGIN_X = 0.02d;
    private static final double TEXT_MARGIN_Y = 0.04d;

    private final Rectangle helpButton = new Rectangle();
    private boolean showGuide = false;

    public void setContext(ApplicationContext context) {
        Objects.requireNonNull(context);
        this.context = context;
    }

    public Rectangle getHelpButton() {
        return helpButton;
    }

    public void toggleGuide() {
        this.showGuide = !showGuide;
    }

    @Override
    public void showIntro() {
        if (context == null) {
            return;
        }
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            DrawUtils.drawFullBackground(graphics2D, screen.width(), screen.height(), Color.BLACK);
            drawLogo(graphics2D, screen.width(), screen.height());
            drawIntroText(graphics2D, screen.width(), screen.height());
        });
        waitForIntroAction();
    }

    private void drawLogo(Graphics2D graphics2D, int screenWidth, int screenHeight) {
        var logo = AssetManager.logo();
        if (logo == null) {
            return;
        }
        var targetLogoWidth = (screenWidth * 60) / 100;
        var scale = Math.min(1.0, (double) targetLogoWidth / logo.getWidth());
        var scaledLogoWidth = (int) (logo.getWidth() * scale);
        var scaledLogoHeight = (int) (logo.getHeight() * scale);
        graphics2D.drawImage(logo,
                (screenWidth - scaledLogoWidth) / 2,
                (screenHeight - scaledLogoHeight) / 2 - (int) (screenHeight * 0.05d),
                scaledLogoWidth, scaledLogoHeight, null);
    }

    private void drawIntroText(Graphics2D graphics2D, int screenWidth, int screenHeight) {
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(AssetManager.buttonFont());
        var introText = "Appuyez sur ESPACE pour commencer";
        var fontMetrics = graphics2D.getFontMetrics();
        graphics2D.drawString(introText,
                (screenWidth - fontMetrics.stringWidth(introText)) / 2,
                screenHeight - (int) (screenHeight * 0.09d));
    }

    private void waitForIntroAction() {
        while (true) {
            var event = context.pollOrWaitEvent(100);
            if (event == null) {
                continue;
            }
            switch (event) {
                case KeyboardEvent ke -> {
                    switch (ke.key()) {
                        case SPACE -> { return; }
                        case ESCAPE -> { context.dispose(); System.exit(0); }
                        default -> {}
                    }
                }
                default -> {}
            }
        }
    }

    @Override
    public void showGameState(GameState gameState, Set<Integer> selectedIndexes,
                              String lastCombination, Integer lastScore) {
        Objects.requireNonNull(gameState, "gameState must not be null");
        if (context == null) {
            return;
        }
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> render(graphics2D, gameState, selectedIndexes,
                lastCombination, lastScore, screen.width(), screen.height()));
    }

    private void render(Graphics2D graphics2D, GameState gameState,
                        Set<Integer> selectedIndexes, String lastCombination,
                        Integer lastScore, int screenWidth, int screenHeight) {
        var handLayout = new HandLayout(screenWidth, screenHeight);
        DrawUtils.drawFullBackground(graphics2D, screenWidth, screenHeight, Color.DARK_GRAY);
        drawUIHeader(graphics2D, gameState, screenWidth, screenHeight);
        if (lastCombination != null) {
            drawLastCombinationMessage(graphics2D, lastCombination, lastScore,
                    screenWidth, screenHeight, handLayout);
        }
        drawHand(graphics2D, gameState, selectedIndexes, handLayout);
        drawActionHints(graphics2D, handLayout);
        updateHelpButton(screenWidth, screenHeight);
        drawHelpButton(graphics2D, screenWidth, screenHeight);
        if (showGuide) {
            drawPopup(graphics2D, screenWidth, screenHeight);
        }
    }

    private void drawUIHeader(Graphics2D graphics2D, GameState gameState,
                              int screenWidth, int screenHeight) {
        var panelX = (int) (screenWidth * PANEL_MARGIN_X);
        var panelY = (int) (screenHeight * PANEL_MARGIN_Y);
        var panelWidth = (int) (screenWidth * HEADER_W_RATIO);
        var panelHeight = (int) (screenHeight * HEADER_H_RATIO);
        DrawUtils.drawPanel(graphics2D, panelX, panelY, panelWidth, panelHeight,
                new Color(20, 20, 40, 220), new Color(120, 180, 255));
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(AssetManager.headerFont());
        var fontMetrics = graphics2D.getFontMetrics();
        var textX = panelX + (int) (screenWidth * TEXT_MARGIN_X);
        var textY = panelY + (int) (screenHeight * TEXT_MARGIN_Y);
        var lineHeight = fontMetrics.getHeight() + fontMetrics.getLeading();
        graphics2D.drawString("Blind: " + gameState.currentBlind().name(), textX, textY);
        graphics2D.drawString("Objectif: " + gameState.currentBlind().score(), textX, textY + lineHeight);
        graphics2D.drawString("Score: " + gameState.playerState().totalScore(), textX, textY + lineHeight * 2);
        graphics2D.drawString(
                "Deck: " + gameState.deck().size() + " | Mains: "
                        + gameState.playerState().handCount()
                        + " | Défausses: " + gameState.playerState().discardCount(),
                textX, textY + lineHeight * 3);
        drawPlanetsInfo(graphics2D, gameState, textX, textY + lineHeight * 4);
    }

    private void drawPlanetsInfo(Graphics2D graphics2D, GameState gameState,
                                 int originX, int originY) {
        var planetEntries = gameState.playerState().planetDrawed().entrySet();
        var planetLines = planetEntries.stream()
                .map(entry -> entry.getKey() + "(" + entry.getKey().combination() + ") x" + entry.getValue())
                .toList();
        var fontMetrics = graphics2D.getFontMetrics();
        var lineHeight = fontMetrics.getHeight() + fontMetrics.getLeading();
        graphics2D.drawString("Planètes : ", originX, originY);
        for (var lineIndex = 0; lineIndex < planetLines.size(); lineIndex++) {
            graphics2D.drawString(" - " + planetLines.get(lineIndex), originX,
                    originY + (lineIndex + 1) * lineHeight);
        }
    }

    private void drawLastCombinationMessage(Graphics2D graphics2D, String combination,
                                            Integer score, int screenWidth, int screenHeight,
                                            HandLayout handLayout) {
        graphics2D.setFont(AssetManager.comboFont());
        var combinationText = score != null
                ? "Vous venez de jouer : " + combination + " (+ " + score + ")"
                : combination + " effectuée";
        graphics2D.drawString(combinationText,
                (int) (screenWidth * 0.02d),
                handLayout.handYOrigin() - (int) (screenHeight * 0.05d));
    }

    private void drawHand(Graphics2D graphics2D, GameState gameState,
                          Set<Integer> selectedIndexes, HandLayout handLayout) {
        var hand = gameState.currentHand();
        var handOriginX = handLayout.handXOrigin(hand.size());
        var handOriginY = handLayout.handYOrigin();
        IntStream.range(0, hand.size()).forEach(cardIndex -> {
            var cardX = handOriginX + cardIndex * (handLayout.cardWidth() + handLayout.gap());
            var cardY = selectedIndexes.contains(cardIndex)
                    ? handOriginY - (int) (handLayout.cardHeight() * 0.18d)
                    : handOriginY;
            var card = hand.get(cardIndex);
            var cardImage = AssetManager.cardImage(card);
            if (cardImage != null) {
                graphics2D.drawImage(cardImage, cardX, cardY,
                        handLayout.cardWidth(), handLayout.cardHeight(), null);
            } else {
                graphics2D.drawRect(cardX, cardY, handLayout.cardWidth(), handLayout.cardHeight());
                graphics2D.drawString(card.toString(), cardX + 12, cardY + 28);
            }
            if (selectedIndexes.contains(cardIndex)) {
                graphics2D.setColor(Color.WHITE);
                graphics2D.setStroke(new BasicStroke(3));
                graphics2D.drawRoundRect(cardX, cardY,
                        handLayout.cardWidth(), handLayout.cardHeight(), 20, 20);
            }
        });
    }

    private void drawActionHints(Graphics2D graphics2D, HandLayout handLayout) {
        var handVerticalOrigin = handLayout.handYOrigin();
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(AssetManager.headerFont());
        var fontMetrics = graphics2D.getFontMetrics();
        var lineHeight = fontMetrics.getHeight() + fontMetrics.getLeading();
        var discardHint = "D : pour défausser après sélection";
        var playHint = "ESPACE : pour jouer après sélection";
        var longestHintWidth = Math.max(fontMetrics.stringWidth(discardHint), fontMetrics.stringWidth(playHint));
        var hintsRightAlignedX = handLayout.screenWidth() - longestHintWidth - (int) (handLayout.screenWidth() * 0.02d);
        graphics2D.drawString(discardHint, hintsRightAlignedX, handVerticalOrigin - lineHeight * 2);
        graphics2D.drawString(playHint, hintsRightAlignedX, handVerticalOrigin - lineHeight);
    }

    public int cardIndexFromX(float x, int screenWidth, int handSize) {
        return new HandLayout(screenWidth, 0).cardIndexFromX(x, handSize);
    }

    public boolean isInsideCardArea(float x, float y, int screenWidth, int screenHeight, int handSize) {
        return new HandLayout(screenWidth, screenHeight).isInsideCardArea(x, y, handSize);
    }

    @Override
    public void showBlindSuccess() {
        if (context == null) {
            return;
        }
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            DrawUtils.drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
                    screen.width() / 2, screen.height() / 4,
                    new Color(20, 20, 40, 230), Color.GREEN);
            DrawUtils.drawOverlay(graphics2D, screen.width(), screen.height(),
                    new Color(255, 255, 255, 150));
            DrawUtils.drawCenteredText(graphics2D, "BLIND REUSSI !",
                    screen.width(), screen.height(), Color.GREEN);
        });
        sleep();
    }

    @Override
    public void showGameOver() {
        if (context == null) {
            return;
        }
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            DrawUtils.drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
                    screen.width() / 2, screen.height() / 4,
                    new Color(20, 20, 40, 230), Color.RED);
            DrawUtils.drawOverlay(graphics2D, screen.width(), screen.height(),
                    new Color(0, 0, 0, 150));
            DrawUtils.drawCenteredText(graphics2D, "GAME OVER",
                    screen.width(), screen.height(), Color.RED);
        });
        sleep();
    }

    @Override
    public void showGameEnd() {
        if (context == null) {
            return;
        }
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            DrawUtils.drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
                    screen.width() / 2, screen.height() / 4,
                    new Color(20, 20, 40, 230), Color.YELLOW);
            DrawUtils.drawOverlay(graphics2D, screen.width(), screen.height(),
                    new Color(255, 215, 0, 180));
            DrawUtils.drawCenteredText(graphics2D, "VICTOIRE !",
                    screen.width(), screen.height(), Color.WHITE);
        });
        sleep();
    }

    @Override
    public void showPlanetDrawn(Planet planet) {
        Objects.requireNonNull(planet);
        if (context == null) {
            return;
        }
        var screen = context.getScreenInfo();
        context.renderFrame(graphics2D -> {
            DrawUtils.drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
                    screen.width() / 2, screen.height() / 4,
                    new Color(20, 20, 40, 230), Color.BLUE);
            DrawUtils.drawOverlay(graphics2D, screen.width(), screen.height(),
                    new Color(0, 0, 255, 150));
            DrawUtils.drawCenteredText(graphics2D, "Planète tirée : " + planet.name(),
                    screen.width(), screen.height(), Color.WHITE);
        });
        sleep();
    }

    private void drawHelpButton(Graphics2D graphics2D, int screenWidth, int screenHeight) {
        var buttonSize = (int) (screenWidth * HELP_BUTTON_RATIO);
        var buttonX = screenWidth - (int) (screenWidth * 0.07f) - buttonSize / 2;
        var buttonY = (int) (screenHeight * 0.025f);
        graphics2D.setColor(new Color(40, 40, 40, 220));
        graphics2D.fillOval(buttonX, buttonY, buttonSize, buttonSize);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(AssetManager.titleFont());
        var fontMetrics = graphics2D.getFontMetrics();
        var questionMarkX = buttonX + (buttonSize - fontMetrics.stringWidth("?")) / 2;
        var questionMarkY = buttonY + (buttonSize + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
        graphics2D.drawString("?", questionMarkX, questionMarkY);
    }

    private void updateHelpButton(int screenWidth, int screenHeight) {
        var buttonSize = (int) (screenWidth * HELP_BUTTON_RATIO);
        var buttonX = screenWidth - (int) (screenWidth * 0.07d) - buttonSize / 2;
        var buttonY = (int) (screenHeight * 0.025d);
        helpButton.setBounds(buttonX, buttonY, buttonSize, buttonSize);
    }

    private void drawPopup(Graphics2D graphics2D, int screenWidth, int screenHeight) {
        var popupWidth = screenWidth / 3;
        var popupHeight = (int) (screenHeight * 0.6f);
        var popupX = (screenWidth - popupWidth) / 2;
        var popupY = (screenHeight - popupHeight) / 2;
        DrawUtils.drawPanel(graphics2D, popupX, popupY, popupWidth, popupHeight,
                new Color(0, 0, 0, 240), Color.WHITE);
        var comboGuide = AssetManager.comboGuide();
        if (comboGuide != null) {
            graphics2D.drawImage(comboGuide,
                    popupX + (int) (screenWidth * 0.02d),
                    popupY + (int) (screenHeight * 0.02d),
                    popupWidth - (int) (screenWidth * 0.04d),
                    popupHeight - (int) (screenHeight * 0.04d), null);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(GraphicView.MESSAGE_DURATION);
        } catch (InterruptedException _) {
        }
    }
}
