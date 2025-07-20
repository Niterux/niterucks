package io.github.niterux.niterucks.niterucksfeatures;

import io.github.niterux.niterucks.mixin.accessors.TextRendererCharacterWidthsAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.render.TextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.frontThirdPersonCamera;

public class MiscUtils {
	public static void printDebugKeys(GameGui gui) {
		gui.addChatMessage("§e[Debug]:§f Key bindings:");
		gui.addChatMessage("F3 + A = Reload chunks");
		gui.addChatMessage("F3 + D = Clear chat");
		gui.addChatMessage("F3 + G = Show chunk boundaries");
		gui.addChatMessage("F3 + Q = Show this list");
		gui.addChatMessage("F3 + S = Reload all assets");
	}

	//moehreag
	public static ByteBuffer readImageBuffer(InputStream inputStream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		int[] is = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
		ByteBuffer byteBuffer = ByteBuffer.allocate(4 * is.length);

		for (int k : is) {
			byteBuffer.putInt(k << 8 | k >> 24 & 0xFF);
		}

		byteBuffer.flip();
		return byteBuffer;
	}

	public static int fastTextPosGet(String text, int xPos, TextRenderer textRenderer) {
		int newWidth;
		if (text == null) {
			return 0;
		} else {
			if (xPos > textRenderer.getWidth(text)) {
				return text.length();
			}
			int textWidth = 0;

			int currentCharacter;
			for (currentCharacter = 0; currentCharacter < text.length(); ++currentCharacter) {
				if (text.charAt(currentCharacter) == 167) {
					++currentCharacter;
				} else {
					int validCharacter = SharedConstants.VALID_CHAT_CHARACTERS.indexOf(text.charAt(currentCharacter));
					if (validCharacter >= 0) {
						newWidth = textWidth + ((TextRendererCharacterWidthsAccessor) textRenderer).getCharacterWidths()[validCharacter + 32];
						if (newWidth > xPos) {
							return currentCharacter;
						} else {
							textWidth = newWidth;
						}
					}
				}
			}

			return currentCharacter;
		}
	}

	public static String textGradientGenerator(String text, char[] colorCodes, TextRenderer textRenderer) {
		int stringLength = textRenderer.getWidth(text);
		double insertionPoints = (double) stringLength / (colorCodes.length);
		text = insertText(text, "§" + colorCodes[0], 0);
		for (int i = 1; i < colorCodes.length; i++) {
			text = insertText(text, "§" + colorCodes[i], fastTextPosGet(text, (int) Math.round(insertionPoints * i), textRenderer));
		}
		return text;
	}

	public static String insertText(String text, String textToInsert, int index) {
		if (index < 0 || index > text.length())
			throw new IllegalArgumentException("Invalid parameters for insertText, index = " + index + " text length = " + text.length());
		return text.substring(0, index) + textToInsert + text.substring(index);
	}

	public static String insertText(String text, char textToInsert, int index) {
		if (index < 0 || index > text.length())
			throw new IllegalArgumentException("Invalid parameters for insertText, index = " + index + " text length = " + text.length());
		return text.substring(0, index) + textToInsert + text.substring(index);
	}

	/**
	 * replace ampersands with §
	 */
	public static String colorify(String text) {
		return text.replace('&', '§');
	}

	public static float fixSpriteYaw(float angle) {
		return (frontThirdPersonCamera) ? angle + 180.0F : angle;
	}

	public static float fixSpritePitch(float angle) {
		return (frontThirdPersonCamera) ? -angle : angle;
	}
}
