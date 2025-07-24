package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.screenshots.AsyncScreenshotReaderWriter;
import io.github.niterux.niterucks.api.screenshots.ScreenshotFormatRegistry;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.Minecraft;

import java.text.SimpleDateFormat;
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
			screenshotReaderWriter.writeScreenshot(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss"), width, height, pixelData)
		);

		future.thenAccept(s -> minecraft.gui.addChatMessage(s));
	}
}
