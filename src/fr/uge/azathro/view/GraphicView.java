package fr.uge.azathro.view;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import fr.uge.azathro.model.GameState;
import fr.uge.azathro.domain.types.planet.Planet;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public final class GraphicView implements View {
	private ApplicationContext context;

	private static final int MESSAGE_DURATION = 1500;
	private static final double CARD_WIDTH_RATIO = 0.09d;
	private static final double BOTTOM_MARGIN_RATIO = 0.05d;
	private static final double GAP_RATIO = 0.012d;
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

	private int cardWidth(int screenW) {
		return (int) (screenW * CARD_WIDTH_RATIO);
	}

	private int cardHeight(int screenW) {
		return (int) (cardWidth(screenW) * 1.5d);
	}

	private int gap(int screenW) {
		return (int) (screenW * GAP_RATIO);
	}

	private int bottomMargin(int screenH) {
		return (int) (screenH * BOTTOM_MARGIN_RATIO);
	}

	private int totalHandWidth(int handSize, int cardWidth, int gap) {
		return handSize * cardWidth + Math.max(0, handSize - 1) * gap;
	}

	private int handXOrigin(int screenW, int handSize, int cardWidth, int gap) {
		return (screenW - totalHandWidth(handSize, cardWidth, gap)) / 2;
	}

	private int handYOrigin(int screenH, int cardHeight, int bottomMargin) {
		return screenH - bottomMargin - cardHeight;
	}

	@Override
	public void showIntro() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(g -> {
			drawFullBackground(g, screen.width(), screen.height(), Color.BLACK);
			drawLogo(g, screen.width(), screen.height());
			drawIntroText(g, screen.width(), screen.height());
		});
		waitForIntroAction();
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
		var targetWidth = (width * 60) / 100;
		var scale = Math.min(1.0, (double) targetWidth / logo.getWidth());
		var screenWidth = (int) (logo.getWidth() * scale);
		var screenHeight = (int) (logo.getHeight() * scale);
		graphics2D.drawImage(logo, (width - screenWidth) / 2,
				(height - screenHeight) / 2 - (int) (height * 0.05d),
				screenWidth, screenHeight, null);
	}

	private void drawIntroText(Graphics2D graphics2D, int width, int height) {
		Objects.requireNonNull(graphics2D);
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(AssetManager.buttonFont());
		var text = "Appuyez sur ESPACE pour commencer";
		var fontMetrics = graphics2D.getFontMetrics();
		graphics2D.drawString(text, (width - fontMetrics.stringWidth(text)) / 2,
				height - (int) (height * 0.09d));
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
		Objects.requireNonNull(gameState, "gameState must not be null");
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
		drawActionHints(graphics2D, width, height);
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
				"Deck: " + gameState.deck().size() + " | Mains: "
						+ gameState.playerState().handCount()
						+ " | Défausses: " + gameState.playerState().discardCount(),
				textX, textY + offsetY * 3);
		drawPlanetsInfo(graphics2D, gameState, textX, textY + offsetY * 4);
	}


	private void drawActionHints(Graphics2D graphics2D, int width, int height) {
		var cardHeight = cardHeight(width);
		var bottomMargin = bottomMargin(height);
		var yo = handYOrigin(height, cardHeight, bottomMargin);
		var lineH = (int) (height * 0.035d);
		var textX = (int) (width * 0.60d);
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(AssetManager.headerFont());
		graphics2D.drawString("D : pour défausser après sélection", textX, yo - lineH * 2);
		graphics2D.drawString("ESPACE : pour jouer après sélection", textX, yo - lineH);
	}

	private void drawPlanetsInfo(Graphics2D graphics2D, GameState gameState,
			int width, int height) {
		var entries = gameState.playerState().planetDrawed().entrySet();
		var lines = entries.stream().map(e -> e.getKey() + "("
				+ e.getKey().combination() + ") x" + e.getValue())
				.toList();
		var offsetY = (int) (height * 0.15d);
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
		var text = score != null
				? "Vous venez de jouer : " + combination + " (+ " + score + ")"
				: combination + " effectuée";
		graphics2D.drawString(text, (int) (width * 0.02d),
				height - bottomMargin - cardHeight - (int) (height * 0.05d));
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
					? yo - (int) (cardHeight * 0.18d)
					: yo;
			var card = hand.get(i);
			var image = AssetManager.cardImage(card);
			if (image != null) {
				graphics2D.drawImage(image, x, y, cardWidth, cardHeight, null);
			} else {
				graphics2D.drawRect(x, y, cardWidth, cardHeight);
				graphics2D.drawString(hand.get(i).toString(), x + 12, y + 28);
			}
			if (selectedIndexes.contains(i)) {
				graphics2D.setColor(Color.WHITE);
				graphics2D.setStroke(new BasicStroke(3));
				graphics2D.drawRoundRect(x, y, cardWidth, cardHeight, 20, 20);
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
		var cardHeight = cardHeight(width);
		var gap = gap(width);
		var bottomMargin = bottomMargin(height);
		var xo = handXOrigin(width, handSize, cardWidth, gap);
		var yo = handYOrigin(height, cardHeight, bottomMargin);
		return x >= xo && x <= xo + totalHandWidth(handSize, cardWidth, gap)
				&& y >= yo && y <= yo + cardHeight;
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
		Objects.requireNonNull(planet);
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
		var fontMetrics = graphics2D.getFontMetrics();
		graphics2D.drawString(text, (float) (width - fontMetrics.stringWidth(text)) / 2,
				(float) height / 2 - height * 0.02f);
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
		graphics2D.fillRoundRect(x + (int) (x * 0.01d), y + (int) (y * 0.01d),
				width, height, 25, 25);
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
		graphics2D.drawString("?", x + (int) (width * 0.015d),
				y + (int) (height * 0.06d));
	}

	private void updateHelpButton(int width, int height) {
		var size = (int) (width * HELP_BUTTON_RATIO);
		var x = width - (int) (width * 0.07d) - size / 2;
		var y = (int) (height * 0.025d);
		helpButton.setBounds(x, y, size, size);
	}

	private void drawPopup(Graphics2D graphics2D, int width, int height) {
		var popupWidth = width / 3;
		var popupHeight = (int) (height * 0.6f);
		var x = (width - popupWidth) / 2;
		var y = (height - popupHeight) / 2;
		drawPanel(graphics2D, x, y, popupWidth, popupHeight,
				new Color(0, 0, 0, 240), Color.WHITE);
		var comboGuide = AssetManager.comboGuide();
		if (comboGuide != null) {
			graphics2D.drawImage(comboGuide, x + (int) (width * 0.02d),
					y + (int) (height * 0.02d),
					popupWidth - (int) (width * 0.04d),
					popupHeight - (int) (height * 0.04d), null);
		}
	}
}
