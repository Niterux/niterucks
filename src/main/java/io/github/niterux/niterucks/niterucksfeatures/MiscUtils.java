package io.github.niterux.niterucks.niterucksfeatures;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.gui.GameGui;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MiscUtils {
	public static void printDebugKeys(GameGui gui) {
		gui.addChatMessage("§e[Debug]:§f Key bindings:");
		gui.addChatMessage("F3 + A = Reload chunks");
		gui.addChatMessage("F3 + D = Clear chat");
		gui.addChatMessage("F3 + G = Show chunk boundaries");
		gui.addChatMessage("F3 + Q = Show this list");
		gui.addChatMessage("F3 + S = Reload all assets");
	}

	public static void renderChunkSquare(double x, double y, double z, BufferBuilder bufferBuilder) {
		bufferBuilder.start(GL11.GL_LINE_LOOP);
		bufferBuilder.vertex(x, y, z);
		bufferBuilder.vertex(x, y, z + 16);
		bufferBuilder.vertex(x + 16, y, z + 16);
		bufferBuilder.vertex(x + 16, y, z);
		bufferBuilder.end();
	}

	//https://stackoverflow.com/a/48875283
	public static ByteBuffer decodePng(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		// Load texture contents into a byte buffer
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * width * height);

		// decode image
		// ARGB format to -> RGBA
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				int argb = image.getRGB(w, h);
				buf.put((byte) (0xFF & (argb >> 16)));
				buf.put((byte) (0xFF & (argb >> 8)));
				buf.put((byte) (0xFF & (argb)));
				buf.put((byte) (0xFF & (argb >> 24)));
			}
		buf.flip();
		return buf;
	}
	public static ByteBuffer readImageBuffer(InputStream inputStream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		int[] is = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
		ByteBuffer byteBuffer = ByteBuffer.allocate(4 * is.length);

		for(int k : is) {
			byteBuffer.putInt(k << 8 | k >> 24 & 0xFF);
		}

		byteBuffer.flip();
		return byteBuffer;
	}
}
