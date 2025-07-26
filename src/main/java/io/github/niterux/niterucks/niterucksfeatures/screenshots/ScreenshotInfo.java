package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import static io.github.niterux.niterucks.niterucksfeatures.screenshots.FastScreenshotUtils.getReaderForPath;

public class ScreenshotInfo {
	private final Path imagePath;
	private int glId = -1;
	private int thumbGlId = -1;
	private BufferedImage image;

	public ScreenshotInfo(Path imagePath) {
		this.imagePath = imagePath;
	}

	public BufferedImage getImage() {
		if (this.image == null)
			loadImage();
		return this.image;
	}

	private void loadImage() {
		try {
			this.image = Objects.requireNonNull(getReaderForPath(imagePath)).readImageAsBufferedImage(imagePath);
		} catch (Exception e) {
			StringWriter stackTrace = new StringWriter();
			PrintWriter writer = new PrintWriter(stackTrace);
			e.printStackTrace(writer);
			e.printStackTrace();
			BufferedImage error = new BufferedImage(550, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = error.createGraphics();
			int y = 15;
			for (String line : stackTrace.toString().split("\n")) {
				graphics.drawString(line, 2, y);
				y += 11;
			}
			graphics.dispose();
			this.image = error;
		}
	}

	private BufferedImage generateThumb() {
		Path cache = getThumbFile();
		if (Files.exists(cache)) {
			try {
				return ImageIO.read(Files.newInputStream(cache));
			} catch (IOException e) {
				Niterucks.LOGGER.warn("Failed to read cached thumbnail file, regenerating!");
			}
		}

		int thumbWidth = Math.min(128, getImage().getWidth());
		int thumbHeight = (int) Math.min(128, (thumbWidth / (float) getImage().getWidth()) * getImage().getHeight());
		Image i = getImage().getScaledInstance(thumbWidth, thumbHeight, BufferedImage.SCALE_SMOOTH);
		BufferedImage scaled = new BufferedImage(thumbWidth, thumbHeight, getImage().getType());
		Graphics2D graphics = scaled.createGraphics();
		graphics.drawImage(i, 0, 0, null);
		graphics.dispose();

		try {
			ImageIO.write(scaled, "png", Files.newOutputStream(cache));
		} catch (IOException e) {
			Niterucks.LOGGER.error("Failed to write thumbnail cache for {}!", getImagePath());
			Niterucks.LOGGER.error("An error occurred: ", e);
		}

		return scaled;
	}


	public int getWidth() {
		return getImage().getWidth();
	}

	public int getHeight() {
		return getImage().getHeight();
	}

	public int getGlId() {
		if (glId == -1) {
			glId = TextureUtil.genTextures();
			TextureUtil.uploadTexture(glId, getImage());
		}
		return glId;
	}

	public int getThumbGlId() {
		if (thumbGlId == -1) {
			thumbGlId = TextureUtil.genTextures();

			TextureUtil.uploadTexture(thumbGlId, generateThumb());
		}
		return thumbGlId;
	}

	public Path getImagePath() {
		return imagePath;
	}

	public void release() {
		if (glId != -1) {
			TextureUtil.deleteTextures(glId);
			glId = -1;
		}
	}

	public void clearBufferedImage() {
		this.image = null;
	}

	private Path getThumbFile() {
		try {
			String hash = Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("MD5")
				.digest(Files.readAllBytes(getImagePath())));
			return createThumbnailDir().resolve(hash);
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Path createThumbnailDir() {
		Path dir = FabricLoader.getInstance().getGameDir()
			.resolve(".cache")
			.resolve("niterucks")
			.resolve("thumbnails");
		try {
			Files.createDirectories(dir);
			return dir;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
