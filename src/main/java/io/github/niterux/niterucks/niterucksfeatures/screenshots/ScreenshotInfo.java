package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Objects;


public class ScreenshotInfo {
	private final Path imagePath;
	private int glId = -1;
	private BufferedImage image;
	private boolean broken = false;

	public ScreenshotInfo(Path imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isBroken() {
		return broken;
	}

	public int getWidth() {
		return getImage().getWidth();
	}

	public BufferedImage getImage() {
		if (this.image == null)
			loadImage();
		return this.image;
	}

	private void loadImage() {
		try {
			this.image = Objects.requireNonNull(FastScreenshotUtils.getReaderForPath(imagePath)).readImageAsBufferedImage(imagePath);
			this.broken = false;
		} catch (Exception e) {
			this.broken = true;
			StringWriter stackTrace = new StringWriter();
			PrintWriter writer = new PrintWriter(stackTrace);
			e.printStackTrace(writer);
			BufferedImage error = new BufferedImage(550, 256, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = error.createGraphics();
			int y = 15;
			for (String line : stackTrace.toString().split("\n")) {
				graphics.drawString(line, 2, y);
				y += 11;
			}
			this.image = error;
		}
	}

	public int getHeight() {
		return getImage().getHeight();
	}

	public int getGlId() {
		if (glId == -1) {
			glId = TextureUtil.putBufferedImageIntoGlId(getImage());
		}
		return glId;
	}

	public Path getImagePath() {
		return imagePath;
	}

	public void release() {
		if (glId != -1) {
			GL11.glDeleteTextures(glId);
			glId = -1;
		}
	}

	public void clearBufferedImage() {
		this.image = null;
	}
}
