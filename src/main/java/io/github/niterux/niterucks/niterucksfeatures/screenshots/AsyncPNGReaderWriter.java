package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.screenshots.AsyncScreenshotReaderWriter;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AsyncPNGReaderWriter implements AsyncScreenshotReaderWriter {
	private static IntBuffer screenshotRGBByteBuffer = BufferUtils.createIntBuffer(854 * 480);
	private static BufferedImage localBufferedImageAllocation = createAppropriateBufferedImage(854, 480);

	private static BufferedImage createAppropriateBufferedImage(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
	}

	private File getOutputScreenshotFile(String formattedCurrentDate) throws IOException {
		Path screenshotDir = FabricLoader.getInstance().getGameDir().resolve("screenshots");
		File newFile = null;
		boolean success = false;
		int i = 1;
		while (!success) {
			newFile = screenshotDir.resolve(formattedCurrentDate + (i == 1 ? "" : "_" + i) + "." + getFormatExtension()).toFile();
			success = newFile.createNewFile();
			i++;
		}
		return newFile;
	}

	public String writeScreenshot(SimpleDateFormat screenshotDateFormat, int width, int height, Object RGBPixelData) {
		try {
			if (localBufferedImageAllocation.getWidth() != width || localBufferedImageAllocation.getHeight() != height)
				localBufferedImageAllocation = createAppropriateBufferedImage(width, height);
			int[] RGBPixelInts = ((DataBufferInt) localBufferedImageAllocation.getRaster().getDataBuffer()).getData();
			for (int i = 0; i < height; i++)
				((IntBuffer) RGBPixelData).get((height * width) - (i * width) - width, RGBPixelInts, i * width, width);
			String formattedDate = screenshotDateFormat.format(new Date());
			File outputScreenshotFile = getOutputScreenshotFile(formattedDate);
			ImageIO.write(localBufferedImageAllocation, getFormatExtension(), outputScreenshotFile);
			return "Saved screenshot as " + outputScreenshotFile.getName();
		} catch (Exception e) {
			Niterucks.LOGGER.error(Arrays.toString(e.getStackTrace()));
			return "Exception, could not create screenshot file " + e;
		}
	}

	public Object synchronouslyReadPixelData(int width, int height) {
		if (screenshotRGBByteBuffer.capacity() < width * height)
			screenshotRGBByteBuffer = BufferUtils.createIntBuffer(width * height);
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, screenshotRGBByteBuffer);
		return screenshotRGBByteBuffer;
	}

	public String getFormatExtension() {
		return "png";
	}
}
