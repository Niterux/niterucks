package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.screenshots.AsyncScreenshotReaderWriter;
import io.github.niterux.niterucks.api.screenshots.ScreenshotFormatRegistry;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A multithreaded better performing version of Minecraft's built in ScreenshotUtils
 */
public class FastScreenshotUtils {
	private static final BufferedImage missingScreenshot;

	static {
		BufferedImage missingScreenshot1;
		try {
			missingScreenshot1 = ImageIO.read(Objects.requireNonNull(Niterucks.class.getResource("/assets/niterucks/broken_screenshot.png")));
		} catch (IOException e) {
			Niterucks.LOGGER.error("Couldn't read the broken screenshot placeholder file!", e);
			missingScreenshot1 = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
			missingScreenshot1.setRGB(0, 0, 0xFF00FF);
			missingScreenshot1.setRGB(1, 1, 0xFF00FF);
			missingScreenshot1.setRGB(0, 1, 0x000000);
			missingScreenshot1.setRGB(1, 0, 0x000000);
		}
		missingScreenshot = missingScreenshot1;
	}

	public static void takeScreenshot() {
		AsyncScreenshotReaderWriter screenshotReaderWriter = ScreenshotFormatRegistry.getRegisteredScreenshotReaderWriters().get(Niterucks.CONFIG.screenshotFormat.get());
		Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
		int width = minecraft.width;
		int height = minecraft.height;
		Object pixelData = screenshotReaderWriter.mainThreadReadPixelData(width, height);

		CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
			{
				File fileOutput;
				try {
					fileOutput = getOutputScreenshotFile(screenshotReaderWriter.getFormatExtension());
				} catch (IOException e) {
					Niterucks.LOGGER.error(String.valueOf(e));
					return "Failed to obtain access to a screenshot file to write to." + e.getMessage();
				}
				try {
					String message = screenshotReaderWriter.writeScreenshot(fileOutput, width, height, pixelData);
					getThumbnail(new ScreenshotInfo(fileOutput.toPath()));
					return message;
				} catch (Exception e) {
					return "An error occurred while writing your screenshot!" + e.getMessage();
				}
			}
		);

		future.thenAccept(s -> minecraft.gui.addChatMessage(s));
	}

	private static File getOutputScreenshotFile(String extension) throws IOException {
		String formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
		Path screenshotDir = FabricLoader.getInstance().getGameDir().resolve("screenshots");
		File newFile = null;
		boolean success = false;
		int i = 1;
		while (!success) {
			newFile = screenshotDir.resolve(formattedCurrentDate + (i == 1 ? "" : "_" + i) + "." + extension).toFile();
			success = newFile.createNewFile();
			i++;
		}
		return newFile;
	}

	public static BufferedImage getThumbnail(ScreenshotInfo screenshotInfo) {
		Path cache = getThumbFile(screenshotInfo);
		if (Files.exists(cache)) {
			try {
				return ImageIO.read(Files.newInputStream(cache));
			} catch (IOException e) {
				Niterucks.LOGGER.debug("Failed to read cached thumbnail file, regenerating!");
			}
		}

		BufferedImage originalImage = screenshotInfo.getImage();
		if (screenshotInfo.isBroken()) {
			screenshotInfo.clearBufferedImage();
			return missingScreenshot;
		}
		int thumbWidth = Math.min(128, screenshotInfo.getWidth());
		int thumbHeight = (int) Math.min(128, (thumbWidth / (float) screenshotInfo.getWidth()) * screenshotInfo.getHeight());
		BufferedImage scaled = new BufferedImage(thumbWidth, thumbHeight, originalImage.getType());
		Graphics2D scaledGraphics = scaled.createGraphics();
		scaledGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		scaledGraphics.drawImage(originalImage, 0, 0, thumbWidth, thumbHeight, 0, 0, screenshotInfo.getWidth(), screenshotInfo.getHeight(), null);
		screenshotInfo.clearBufferedImage();
		try {
			ImageIO.write(scaled, "png", Files.newOutputStream(cache));
		} catch (IOException e) {
			Niterucks.LOGGER.error("Failed to write thumbnail cache for {}!", screenshotInfo.getImagePath());
			Niterucks.LOGGER.error("An error occurred: ", e);
		}

		return scaled;
	}

	private static Path getThumbFile(ScreenshotInfo screenshotInfo) {
		try {
			String hash = Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("MD5")
				.digest(screenshotInfo.getImagePath().getFileName().toString().getBytes(StandardCharsets.UTF_8)));
			return createThumbnailDir().resolve(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static Path createThumbnailDir() {
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

	@Nullable
	public static AsyncScreenshotReaderWriter getReaderForPath(Path imagePath) {
		if (!isCompatiblePath(imagePath))
			return null;
		String filename = imagePath.getFileName().toString();
		return ScreenshotFormatRegistry.getRegisteredScreenshotReaderWriters().get(filename.substring(filename.lastIndexOf('.') + 1));
	}

	public static boolean isCompatiblePath(Path imagePath) {
		String filename = imagePath.getFileName().toString();
		return ScreenshotFormatRegistry.getRegisteredScreenshotReaderWriters().containsKey(filename.substring(filename.lastIndexOf('.') + 1));
	}
}
