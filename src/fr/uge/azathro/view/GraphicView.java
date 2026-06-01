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
	private final int gap;
	private final int bottomMargin;
	private ApplicationContext context;
	private final static Map<Card, BufferedImage> cardImages = new HashMap<>();

	private static final BufferedImage BACKGROUND = loadBackground();
	private static final BufferedImage LOGO = loadLogo();
	private static final BufferedImage CARDS = loadCards();
	private static final BufferedImage COMBOGUIDE = loadGuide();

	private static final Font HEADER_FONT = loadFont("src/fr/uge/azathro/view/assets/Pix32.ttf", 30f);
	private static final Font COMBO_FONT = loadFont("src/fr/uge/azathro/view/assets/Pix32.ttf", 28f);
	private static final Font BUTTON_FONT = loadFont("src/fr/uge/azathro/view/assets/Pix32.ttf", 32f);
	private static final Font TITLE_FONT = loadFont("src/fr/uge/azathro/view/assets/Pix32.ttf", 50f);

	private static final int MESSAGE_DURATION = 1500;

	private Rectangle helpButton;
	private boolean showGuide = false;

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

	private static BufferedImage loadGuide() {
		try {
			return ImageIO.read(new File("src/fr/uge/azathro/view/assets/hands.png"));
		} catch (IOException e) {
			return null;
		}
	}

	private static BufferedImage loadCards() {
		for (var suit : Suit.values()) {
			for (var rank : Rank.values()) {
				var path = "src/fr/uge/azathro/view/assets/cards/" + rank.name().toLowerCase() + "_" + suit.name() + ".png";
				try {
					var image = ImageIO.read(new File(path));
					cardImages.put(new Card(suit, rank), image);
				} catch (IOException e) {
					return null;
				}
			}
		}
		return null;
	}

	private static Font loadFont(String path, float size) {
		try {
			var font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
			return font.deriveFont(size);

		} catch (IOException | FontFormatException e) {
			return null;
		}
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	public Rectangle getHelpButton() {
		return helpButton;
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

	private void drawFullBackground(Graphics2D graphics2D, int width, int height, Color fallbackColor) {
		if (BACKGROUND != null) {
			graphics2D.drawImage(BACKGROUND, 0, 0, width, height, null);
		} else {
			graphics2D.setColor(fallbackColor);
			graphics2D.fill(new Rectangle2D.Float(0, 0, width, height));
		}
	}

	private void drawLogo(Graphics2D graphics2D, int width, int height) {
		if (LOGO == null)
			return;
		int targetWidth = (width * 60) / 100;
		double scale = Math.min(1.0, (double) targetWidth / LOGO.getWidth());
		int sw = (int) (LOGO.getWidth() * scale);
		int sh = (int) (LOGO.getHeight() * scale);
		graphics2D.drawImage(LOGO, (width - sw) / 2, (height - sh) / 2 - 50, sw, sh, null);
	}

	private void drawIntroText(Graphics2D graphics2D, int width, int height) {
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(BUTTON_FONT);
		var text = "Appuyez sur ESPACE pour commencer";
		var fontMetrics = graphics2D.getFontMetrics();
		graphics2D.drawString(text, (width - fontMetrics.stringWidth(text)) / 2, height - 100);
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
	public void showGameState(GameState gameState, Set<Integer> selectedIndexes, String lastCombination,
			Integer lastScore) {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(
				g -> render(g, gameState, selectedIndexes, lastCombination, lastScore, screen.width(), screen.height()));
	}

	private void render(Graphics2D graphics2D, GameState gameState, Set<Integer> selectedIndexes, String lastCombination,
			Integer lastScore, int width, int height) {
		var cardWidth = width / 10;
		var cardHeight = (int) (cardWidth * 1.5);
		drawFullBackground(graphics2D, width, height, Color.DARK_GRAY);
		drawUIHeader(graphics2D, gameState, width, height);
		if (lastCombination != null) {
			drawLastCombinationMessage(graphics2D, lastCombination, lastScore, height, cardHeight);
		}
		drawHand(graphics2D, gameState, selectedIndexes, width, height, cardWidth, cardHeight);
		drawHelpButton(graphics2D, width);
		if (showGuide) {
			drawPopup(graphics2D, width, height);
			
		}
	}

	private void drawUIHeader(Graphics2D graphics2D, GameState gameState, int width, int height) {
		var panelX = width / 55;
		var panelY = height / 45;
		var panelWidth = width / 2;
		var panelHeight = height / 3;
		drawPanel(graphics2D, panelX, panelY, panelWidth, panelHeight, new Color(20, 20, 40, 220),
				new Color(120, 180, 255));
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(HEADER_FONT);
		var textX = panelX + 20;
		var textY = panelY + 40;
		var offsetY = panelHeight / 6;
		graphics2D.drawString("Blind: " + gameState.currentBlind().name(), textX, textY);
		graphics2D.drawString("Objectif: " + gameState.currentBlind().score(), textX, textY + offsetY);
		graphics2D.drawString("Score: " + gameState.playerState().totalScore(), textX, textY + offsetY * 2);
		graphics2D.drawString(
				"Deck: " + gameState.deck().size() + " | Mains restantes: " + gameState.playerState().handCount(), textX,
				textY + offsetY * 3);
		drawPlanetsInfo(graphics2D, gameState, textX, textY + offsetY * 4);
	}

	private void drawPlanetsInfo(Graphics2D graphics2D, GameState gameState, int width, int height) {
		var planetsText = gameState.playerState().planetDrawed().entrySet().stream()
				.map(e -> e.getKey() + " (" + e.getKey().combination() + ") " + " x" + e.getValue())
				.collect(Collectors.joining(" | "));
		graphics2D.drawString("Planètes : " + planetsText, width, height);
	}

	private void drawLastCombinationMessage(Graphics2D graphics2D, String combination, Integer score, int height,
			int ch) {
		graphics2D.setFont(COMBO_FONT);
		var text = "Vous venez de jouer : " + combination + " (+ " + score + ")";
		graphics2D.drawString(text, 30, height - bottomMargin - ch - 40);
	}

	private void drawHand(Graphics2D graphics2D, GameState gameState, Set<Integer> selectedIndexes, int width, int height,
			int cardWidth, int cardHeight) {
		var hand = gameState.currentHand();
		var xo = xOriginFor(width, hand.size(), cardWidth);
		var yo = yOriginFor(height, cardHeight);
		IntStream.range(0, hand.size()).forEach(i -> {
			var x = xo + i * (cardWidth + gap);
			var y = selectedIndexes.contains(i) ? yo - 30 : yo;
			var card = hand.get(i);
			var image = cardImages.get(card);
			if (image != null) {
				graphics2D.drawImage(image, x, y, cardWidth, cardHeight, null);
			}
			if (selectedIndexes.contains(i)) {
				graphics2D.setColor(Color.WHITE);
				graphics2D.setStroke(new BasicStroke(3));
				graphics2D.drawRoundRect(x - 3, y - 3, cardWidth + 4, cardHeight + 6, 20, 20);
			}
		});
	}

	@Override
	public void showBlindSuccess() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3, screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.GREEN);
			drawOverlay(graphics2D, screen.width(), screen.height(), new Color(255, 255, 255, 150));
			drawCenteredText(graphics2D, "BLIND REUSSI !", screen.width(), screen.height(), Color.GREEN);
		});
		sleep(MESSAGE_DURATION);
	}

	@Override
	public void showGameOver() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3, screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.RED);
			drawOverlay(graphics2D, screen.width(), screen.height(), new Color(0, 0, 0, 150));
			drawCenteredText(graphics2D, "GAME OVER", screen.width(), screen.height(), Color.RED);
		});
		sleep(MESSAGE_DURATION);
	}

	@Override
	public void showGameEnd() {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3, screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.YELLOW);
			drawOverlay(graphics2D, screen.width(), screen.height(), new Color(255, 215, 0, 180));
			drawCenteredText(graphics2D, "VICTOIRE !", screen.width(), screen.height(), Color.WHITE);
		});
		sleep(MESSAGE_DURATION);
	}

	@Override
	public void showPlanetDrawn(Planet planet) {
		if (context == null)
			return;
		var screen = context.getScreenInfo();
		context.renderFrame(graphics2D -> {
			drawPanel(graphics2D, screen.width() / 4, screen.height() / 3, screen.width() / 2, screen.height() / 4,
					new Color(20, 20, 40, 230), Color.BLUE);
			drawOverlay(graphics2D, screen.width(), screen.height(), new Color(0, 0, 255, 150));
			drawCenteredText(graphics2D, "Planète tirée : " + planet.name(), screen.width(), screen.height(), Color.WHITE);
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
		graphics2D.drawString(text, (width - fm.stringWidth(text)) / 2, height / 2 - 20);
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException _) {
		}
	}

	public int cardIndexFromX(float x, int width, int handSize) {
		if (handSize <= 0)
			return -1;
		var cardWidth = (width * 10) / 100;
		var xo = xOriginFor(width, handSize, cardWidth);
		if (x < xo || x > xo + totalWidthFor(handSize, cardWidth))
			return -1;
		var idx = (int) ((x - xo) / (cardWidth + gap));
		return Math.clamp(idx, 0, handSize - 1);
	}

	public boolean isInsideCardArea(float x, float y, int width, int height, int handSize) {
		if (handSize <= 0)
			return false;
		var cardWidth = (width * 10) / 100;
		var carHeight = (height * 30) / 100;
		var xo = xOriginFor(width, handSize, cardWidth);
		var yo = yOriginFor(height, carHeight);
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

	private void drawPanel(Graphics2D graphics2D, int x, int y, int width, int height, Color background, Color border) {
		graphics2D.setColor(new Color(0, 0, 0, 100));
		graphics2D.fillRoundRect(x + 5, y + 5, width, height, 25, 25);
		graphics2D.setColor(background);
		graphics2D.fillRoundRect(x, y, width, height, 25, 25);
		graphics2D.setColor(border);
		graphics2D.setStroke(new BasicStroke(4));
		graphics2D.drawRoundRect(x, y, width, height, 25, 25);
	}
	
	public void toggleGuide() {
		showGuide = !showGuide;
	}

	private void drawHelpButton(Graphics2D graphics2D, int width) {
		int size = 50;
		int x = width - 70;
		int y = 20;
		helpButton = new Rectangle(x, y, size, size);
		graphics2D.setColor(new Color(40, 40, 40, 220));
		graphics2D.fillOval(x, y, size, size);
		graphics2D.setColor(Color.WHITE);
		graphics2D.setFont(TITLE_FONT);
		graphics2D.drawString("?", x + 15, y + 40);
	}

	private void drawPopup(Graphics2D graphics2D, int width, int height) {
		int popupWidth = width / 3;
		int popupHeight = height / 2;
		int x = (width - popupWidth) / 2;
		int y = (height - popupHeight) / 2;
		drawPanel(graphics2D, x, y, popupWidth, popupHeight, new Color(0, 0, 0, 240), Color.WHITE);
		if (COMBOGUIDE != null) {
			graphics2D.drawImage(COMBOGUIDE, x + 20, y + 20, popupWidth - 40, popupHeight - 40, null);
		}
	}
}
