package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.screenshots.AsyncScreenshotReaderWriter;
import io.github.niterux.niterucks.api.screenshots.ScreenshotFormatRegistry;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * A multithreaded better performing version of Minecraft's built in ScreenshotUtils
 */
public class FastScreenshotUtils {

	public static void takeScreenshot() {
		AsyncScreenshotReaderWriter screenshotReaderWriter = ScreenshotFormatRegistry.getRegisteredScreenshotReaderWriters().get(Niterucks.CONFIG.screenshotFormat.get());
		Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
		int width = minecraft.width;
		int height = minecraft.height;
		Object pixelData = screenshotReaderWriter.synchronouslyReadPixelData(width, height);

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
					return screenshotReaderWriter.writeScreenshot(fileOutput, width, height, pixelData);
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
}
