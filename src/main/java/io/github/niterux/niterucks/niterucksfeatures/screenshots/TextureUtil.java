package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

public class TextureUtil {
	public static void putBufferedImageIntoGlId(int glId, BufferedImage bufferedImage) {
		int alignedWidth = calculateNextHighestPowerOf2IfNotAligned(bufferedImage.getWidth());
		int alignedHeight = calculateNextHighestPowerOf2IfNotAligned(bufferedImage.getHeight());
		IntBuffer buffer = BufferUtils.createIntBuffer(alignedWidth * alignedHeight);
		BufferedImage test = new BufferedImage(alignedWidth, alignedHeight, BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = test.createGraphics();
		graphics.drawImage(bufferedImage, 0, 0, null);
		graphics.dispose();
		int[] RGBPixelInts = ((DataBufferInt) test.getRaster().getDataBuffer()).getData();
		buffer.put(RGBPixelInts);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId);
		buffer.position(0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, alignedWidth, alignedHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}

	public static int calculateNextHighestPowerOf2IfNotAligned(int imageDimension) {
		return 1 << (Integer.SIZE - Integer.numberOfLeadingZeros(imageDimension - 1));
	}
	public static float getPowerOf2Ratio(int number) {
		return (float) number / calculateNextHighestPowerOf2IfNotAligned(number);
	}

	public static void renderTexturePortion(int xMin, int yMin, int xMax, int yMax, float uMin, float vMin, float uMax, float vMax) {
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		bufferBuilder.start();
		bufferBuilder.vertex(xMin, yMin, 0.0, uMin, vMin);
		bufferBuilder.vertex(xMin, yMax, 0.0, uMin, vMax);
		bufferBuilder.vertex(xMax, yMax, 0.0, uMax, vMax);
		bufferBuilder.vertex(xMax, yMin, 0.0, uMax, vMin);
		bufferBuilder.end();
	}
}
