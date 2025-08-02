package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.screenshots.AsyncScreenshotReaderWriter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Path;

public class AsyncImageIOReaderWriter implements AsyncScreenshotReaderWriter {
	private static IntBuffer screenshotRGBByteBuffer = BufferUtils.createIntBuffer(854 * 480);
	private static BufferedImage localBufferedImageAllocation = createAppropriateBufferedImage(854, 480);
	private final String format;

	public AsyncImageIOReaderWriter(String format) {
		this.format = format;
	}

	private static BufferedImage createAppropriateBufferedImage(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
	}

	public String writeScreenshot(File outputFile, int width, int height, Object RGBPixelData) {
		try {
			if (localBufferedImageAllocation.getWidth() != width || localBufferedImageAllocation.getHeight() != height)
				localBufferedImageAllocation = createAppropriateBufferedImage(width, height);
			int[] RGBPixelInts = ((DataBufferInt) localBufferedImageAllocation.getRaster().getDataBuffer()).getData();
			for (int i = 0; i < height; i++)
				((IntBuffer) RGBPixelData).get((height * width) - (i * width) - width, RGBPixelInts, i * width, width);
			ImageIO.write(localBufferedImageAllocation, getFormatExtension(), outputFile);
			return "Saved screenshot as " + outputFile.getName();
		} catch (Exception e) {
			Niterucks.LOGGER.error(String.valueOf(e));
			return "Exception, could not create screenshot file " + e;
		}
	}

	public Object mainThreadReadPixelData(int width, int height) {
		if (screenshotRGBByteBuffer.capacity() < width * height)
			screenshotRGBByteBuffer = BufferUtils.createIntBuffer(width * height);
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, screenshotRGBByteBuffer);
		return screenshotRGBByteBuffer;
	}

	public String getFormatExtension() {
		return format;
	}

	@Override
	public BufferedImage readImageAsBufferedImage(Path imagePath) throws IOException {
		return ImageIO.read(imagePath.toFile());
	}
}
