package fr.uge.azathro.view;

import fr.uge.azathro.domain.Card;
import fr.uge.azathro.domain.types.rank.Rank;
import fr.uge.azathro.domain.types.suit.Suit;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssetManager {
	private static final String ASSETS_PATH = "src/fr/uge/azathro/view/assets/";

	private static final Map<Card, BufferedImage> cardImages = new HashMap<>();
	private static final BufferedImage BACKGROUND;
	private static final BufferedImage LOGO;
	private static final BufferedImage COMBOGUIDE;
	private static final Font HEADER_FONT;
	private static final Font COMBO_FONT;
	private static final Font BUTTON_FONT;
	private static final Font TITLE_FONT;

	static {
		BACKGROUND = loadImage(ASSETS_PATH + "background.png");
		LOGO = loadImage("assets/logo.png");
		COMBOGUIDE = loadImage(ASSETS_PATH + "hands.png");
		loadCards();
		Font font = loadFont();
		HEADER_FONT = derive(font, 26f);
		COMBO_FONT = derive(font, 28f);
		BUTTON_FONT = derive(font, 32f);
		TITLE_FONT = derive(font, 50f);
	}

	public static BufferedImage cardImage(Card card) {
		Objects.requireNonNull(card);
		return cardImages.get(card);
	}

	public static BufferedImage background() {
		return BACKGROUND;
	}

	public static BufferedImage logo() {
		return LOGO;
	}

	public static BufferedImage comboGuide() {
		return COMBOGUIDE;
	}

	public static Font headerFont() {
		return HEADER_FONT;
	}

	public static Font comboFont() {
		return COMBO_FONT;
	}

	public static Font buttonFont() {
		return BUTTON_FONT;
	}

	public static Font titleFont() {
		return TITLE_FONT;
	}

	private static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			return createFallbackImage();
		}
	}

	private static BufferedImage createFallbackImage() {
		var image = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
		var graphics = image.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 100, 100);
		return image;
	}

	private static void loadCards() {
		for (var suit : Suit.values()) {
			for (var rank : Rank.values()) {
				var path = ASSETS_PATH + "cards/" + rank.name()
						+ "_" + suit.name() + ".png";
				try {
					var image = ImageIO.read(new File(path));
					cardImages.put(new Card(suit, rank), image);
				} catch (IOException e) {
					return;
				}
			}
		}
	}

	private static Font loadFont() {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, new File("src/fr/uge/azathro/view/assets/Pix32.ttf"));
		} catch (IOException | FontFormatException e) {
			return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}
	}

	private static Font derive(Font font, float size) {
		return font == null ? null : font.deriveFont(size);
	}

}
