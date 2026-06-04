package fr.uge.azathro.view;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.types.planet.Planet;
import fr.uge.azathro.domain.types.rank.Rank;
import fr.uge.azathro.domain.types.suit.Suit;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GraphicView implements View {
	private ApplicationContext context;

	private static final int MESSAGE_DURATION = 1500;
	private static final float CARD_WIDTH_RATIO = 0.09f;
	private static final float BOTTOM_MARGIN_RATIO = 0.05f;
	private static final float GAP_RATIO = 0.012f;
	private static final float HEADER_W_RATIO = 0.48f;
	private static final float HEADER_H_RATIO = 0.55f;
	private static final float HELP_BUTTON_RATIO = 0.045f;
	private static final float PANEL_MARGIN_X = 0.02f;
	private static final float PANEL_MARGIN_Y = 0.02f;
	private static final float TEXT_MARGIN_X = 0.02f;
	private static final float TEXT_MARGIN_Y = 0.04f;

	private final Rectangle helpButton = new Rectangle();
	private boolean showGuide = false;

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public Rectangle getHelpButton() {
		return helpButton;
	}

	public void toggleGuide() {
		this.showGuide = !showGuide;
	}

	private int cardWidth(int screenW) {
		return (int) (screenW * CARD_WIDTH_RATIO);
	}

	private int cardHeight(int screenW) {
		return (int) (cardWidth(screenW) * 1.5f);
	}

	private int gap(int screenW) {
		return (int) (screenW * GAP_RATIO);
	}

	private int bottomMargin(int screenH) {
		return (int) (screenH * BOTTOM_MARGIN_RATIO);
	}

	private int totalHandWidth(int handSize, int cw, int gap) {
		return handSize * cw + Math.max(0, handSize - 1) * gap;
	}

	private int handXOrigin(int screenW, int handSize, int cw, int gap) {
		return (screenW - totalHandWidth(handSize, cw, gap)) / 2;
	}

	private int handYOrigin(int screenH, int ch, int bm) {
		return screenH - bm - ch;
	}

	@Override
	public void showIntro() {
		if (context == null)
			return;
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

	private void drawFullBackground(Graphics2D graphics2D, int width,
			int height, Color fallbackColor) {
		var background = AssetManager.background();
		if (background != null) {
			graphics2D.drawImage(background, 0, 0, width, height, null);
		} else {
			graphics2D.setColor(fallbackColor);
			graphics2D.fill(new Rectangle2D.Float(0, 0, width, height));
		}
	}

	private void drawLogo(Graphics2D graphics2D, int width, int height) {
		var logo = AssetManager.logo();
		if (logo == null)
			return;
		int targetWidth = (width * 60) / 100;
		double scale = Math.min(1.0, (double) targetWidth / logo.getWidth());
		int sw = (int) (logo.getWidth() * scale);
		int sh = (int) (logo.getHeight() * scale);
		graphics2D.drawImage(logo, (width - sw) / 2,
				(height - sh) / 2 - (int) (height * 0.05f), sw, sh, null);
	}

	private void drawIntroText(Graphics2D graphics2D, int width, int height) {
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(AssetManager.buttonFont());
		var text = "Appuyez sur ESPACE pour commencer";
		var fontMetrics = graphics2D.getFontMetrics();
		graphics2D.drawString(text, (width - fontMetrics.stringWidth(text)) / 2,
				height - (int) (height * 0.09f));
	}

	private void waitForIntroAction() {
		while (true) {
			var event = context.pollOrWaitEvent(100);
			if (event == null)
				continue;
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
	public void showGameState(GameState gameState, Set<Integer> selectedIndexes,
			String lastCombination, Integer lastScore) {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(g -> render(g, gameState, selectedIndexes,
				lastCombination, lastScore, screen.width(), screen.height()));
	}

	private void render(Graphics2D graphics2D, GameState gameState,
			Set<Integer> selectedIndexes, String lastCombination,
			Integer lastScore, int width, int height) {
		var cardWidth = cardWidth(width);
		var cardHeight = cardHeight(width);
		var gap = gap(width);
		var bottomMargin = bottomMargin(height);
		drawFullBackground(graphics2D, width, height, Color.DARK_GRAY);
		drawUIHeader(graphics2D, gameState, width, height);
		if (lastCombination != null) {
			drawLastCombinationMessage(graphics2D, lastCombination, lastScore,
					width, height, cardHeight, bottomMargin);
		}
		drawHand(graphics2D, gameState, selectedIndexes, width, height,
				cardWidth, cardHeight, gap, bottomMargin);
		updateHelpButton(width, height);
		drawHelpButton(graphics2D, width, height);
		if (showGuide) {
			drawPopup(graphics2D, width, height);
		}
	}

	private void drawUIHeader(Graphics2D graphics2D, GameState gameState,
			int width, int height) {
		var panelX = (int) (width * PANEL_MARGIN_X);
		var panelY = (int) (height * PANEL_MARGIN_Y);
		var panelWidth = (int) (width * HEADER_W_RATIO);
		var panelHeight = (int) (height * HEADER_H_RATIO);
		drawPanel(graphics2D, panelX, panelY, panelWidth, panelHeight,
				new Color(20, 20, 40, 220), new Color(120, 180, 255));
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(AssetManager.headerFont());
		var textX = panelX + (int) (width * TEXT_MARGIN_X);
		var textY = panelY + (int) (height * TEXT_MARGIN_Y);
		var offsetY = panelHeight / 10;
		graphics2D.drawString("Blind: " + gameState.currentBlind().name(),
				textX, textY);
		graphics2D.drawString("Objectif: " + gameState.currentBlind().score(),
				textX, textY + offsetY);
		graphics2D.drawString("Score: " + gameState.playerState().totalScore(),
				textX, textY + offsetY * 2);
		graphics2D.drawString(
				"Deck: " + gameState.deck().size() + " | Mains restantes: "
						+ gameState.playerState().handCount(),
				textX, textY + offsetY * 3);
		drawPlanetsInfo(graphics2D, gameState, textX, textY + offsetY * 4);
	}

	private void drawPlanetsInfo(Graphics2D graphics2D, GameState gameState,
			int width, int height) {
		var entries = gameState.playerState().planetDrawed().entrySet();
		var lines = entries.stream().map(e -> e.getKey() + "("
				+ e.getKey().combination() + ") x" + e.getValue())
				.collect(Collectors.toList());
		var offsetY = height * 0.15f;
		graphics2D.drawString("Planètes : ", width, height);
		for (var i = 0; i < lines.size(); i++) {
			graphics2D.drawString(" - " + lines.get(i), width + width * 3,
					height + i * offsetY);
		}
	}

	private void drawLastCombinationMessage(Graphics2D graphics2D,
			String combination, Integer score, int width, int height,
			int cardHeight, int bottomMargin) {
		graphics2D.setFont(AssetManager.comboFont());
		var text = "Vous venez de jouer : " + combination + " (+ " + score
				+ ")";
		graphics2D.drawString(text, (int)(width * 0.02f),
				height - bottomMargin - cardHeight - (int)(height * 0.05f));
	}

	private void drawHand(Graphics2D graphics2D, GameState gameState,
			Set<Integer> selectedIndexes, int width, int height, int cardWidth,
			int cardHeight, int gap, int bottomMargin) {
		var hand = gameState.currentHand();
		var xo = handXOrigin(width, hand.size(), cardWidth, gap);
		var yo = handYOrigin(height, cardHeight, bottomMargin);
		IntStream.range(0, hand.size()).forEach(i -> {
			var x = xo + i * (cardWidth + gap);
			var y = selectedIndexes.contains(i)
					? yo - (int) (cardHeight * 0.18f)
					: yo;
			var card = hand.get(i);
			var image = AssetManager.cardImage(card);
			if (image != null) {
				graphics2D.drawImage(image, x, y, cardWidth, cardHeight, null);
			}
			if (selectedIndexes.contains(i)) {
				graphics2D.setColor(Color.WHITE);
				graphics2D.setStroke(new BasicStroke(3));
				graphics2D.drawRoundRect(x - 3, y - 3, cardWidth + 6,
						cardHeight + 6, 20, 20);
			}
		});
	}

	public int cardIndexFromX(float x, int width, int handSize) {
		if (handSize <= 0)
			return -1;
		var cardWidth = cardWidth(width);
		int gap = gap(width);
		var xo = handXOrigin(width, handSize, cardWidth, gap);
		if (x < xo || x > xo + totalHandWidth(handSize, cardWidth, gap))
			return -1;
		var idx = (int) ((x - xo) / (cardWidth + gap));
		return Math.clamp(idx, 0, handSize - 1);
	}

	public boolean isInsideCardArea(float x, float y, int width, int height,
			int handSize) {
		if (handSize <= 0)
			return false;
		var cardWidth = cardWidth(width);
		var carHeight = cardHeight(width);
		var gap = gap(width);
		int bottomMargin = bottomMargin(height);
		var xo = handXOrigin(width, handSize, cardWidth, gap);
		var yo = handYOrigin(height, carHeight, bottomMargin);
		return x >= xo && x <= xo + totalHandWidth(handSize, cardWidth, gap)
				&& y >= yo && y <= yo + carHeight;
	}

	@Override
	public void showBlindSuccess() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
					screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.GREEN);
			drawOverlay(graphics2D, screen.width(), screen.height(),
					new Color(255, 255, 255, 150));
			drawCenteredText(graphics2D, "BLIND REUSSI !", screen.width(),
					screen.height(), Color.GREEN);
		});
		sleep(MESSAGE_DURATION);
	}

	@Override
	public void showGameOver() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
					screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.RED);
			drawOverlay(graphics2D, screen.width(), screen.height(),
					new Color(0, 0, 0, 150));
			drawCenteredText(graphics2D, "GAME OVER", screen.width(),
					screen.height(), Color.RED);
		});
		sleep(MESSAGE_DURATION);
	}

	@Override
	public void showGameEnd() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
					screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.YELLOW);
			drawOverlay(graphics2D, screen.width(), screen.height(),
					new Color(255, 215, 0, 180));
			drawCenteredText(graphics2D, "VICTOIRE !", screen.width(),
					screen.height(), Color.WHITE);
		});
		sleep(MESSAGE_DURATION);
	}

	@Override
	public void showPlanetDrawn(Planet planet) {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3,
					screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.BLUE);
			drawOverlay(graphics2D, screen.width(), screen.height(),
					new Color(0, 0, 255, 150));
			drawCenteredText(graphics2D, "Planète tirée : " + planet.name(),
					screen.width(), screen.height(), Color.WHITE);
		});
		sleep(1500);
	}

	private void drawOverlay(Graphics2D graphics2D, int width, int height,
			Color color) {
		graphics2D.setColor(color);
		graphics2D.fillRect(0, 0, width, height);
	}

	private void drawCenteredText(Graphics2D graphics2D, String text, int width,
			int height, Color color) {
		graphics2D.setColor(color);
		graphics2D.setFont(AssetManager.titleFont());
		var fm = graphics2D.getFontMetrics();
		graphics2D.drawString(text, (width - fm.stringWidth(text)) / 2,
				height / 2 - 20);
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException _) {
		}
	}

	private void drawPanel(Graphics2D graphics2D, int x, int y, int width,
			int height, Color background, Color border) {
		graphics2D.setColor(new Color(0, 0, 0, 100));
		graphics2D.fillRoundRect(x + 5, y + 5, width, height, 25, 25);
		graphics2D.setColor(background);
		graphics2D.fillRoundRect(x, y, width, height, 25, 25);
		graphics2D.setColor(border);
		graphics2D.setStroke(new BasicStroke(4));
		graphics2D.drawRoundRect(x, y, width, height, 25, 25);
	}

	private void drawHelpButton(Graphics2D graphics2D, int width, int height) {
		var size = (int) (width * HELP_BUTTON_RATIO);
		var x = width - (int) (width * 0.07f) - size / 2;
		var y = (int) (height * 0.025f);
		graphics2D.setColor(new Color(40, 40, 40, 220));
		graphics2D.fillOval(x, y, size, size);
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(AssetManager.titleFont());
		graphics2D.drawString("?", x + (int) (width * 0.015f),
				y + (int) (height * 0.05f));
	}

	private void updateHelpButton(int width, int height) {
		var size = (int) (width * HELP_BUTTON_RATIO);
		var x = width - (int) (width * 0.07f) - size / 2;
		var y = (int) (height * 0.025f);
		helpButton.setBounds(x, y, size, size);
	}

	private void drawPopup(Graphics2D graphics2D, int width, int height) {
		var popupWidth = width / 3;
		var popupHeight = (int) (height * 0.6f);
		var x = (width - popupWidth) / 2;
		var y = (height - popupHeight) / 2;
		drawPanel(graphics2D, x, y, popupWidth, popupHeight,
				new Color(0, 0, 0, 240), Color.WHITE);
		var comboguide = AssetManager.comboGuide();
		if (comboguide != null) {
			graphics2D.drawImage(comboguide, x + (int) (width * 0.02f),
					y + (int) (height * 0.02f),
					popupWidth - (int) (width * 0.04f),
					popupHeight - (int) (height * 0.04f), null);
		}
	}
}
