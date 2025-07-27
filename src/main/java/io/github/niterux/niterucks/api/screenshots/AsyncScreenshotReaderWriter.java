package io.github.niterux.niterucks.api.screenshots;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public interface AsyncScreenshotReaderWriter {
	/**
	 * This is called after {@link #mainThreadReadPixelData(int, int)} but on an Async thread.
	 * The result of {@link #mainThreadReadPixelData(int, int)} is passed in as {@param pixelData}.
	 * Use this to write out a screenshot file in the desired format.
	 * @param outputFile
	 * This is the file that Niterucks has created for your writer to write to using {@link Files#write(Path, byte[], OpenOption...)} or otherwise
	 * @param width
	 * The integer width of the game window in pixels.
	 * @param height
	 * The integer height of the game window in pixels.
	 * @param pixelData
	 * The pixel data object that you have created using {@link #mainThreadReadPixelData(int, int)} passed in as an Object.
	 * @return
	 * A message to be displayed in chat after you're done writing your screenshot file.
	 */
	String writeScreenshot(File outputFile, int width, int height, Object pixelData);

	/**
	 * @param width
	 * The integer width of the game window in pixels.
	 * @param height
	 * The integer height of the game window in pixels.
	 * @return
	 * The pixel data object you want to store.
	 */
	Object mainThreadReadPixelData(int width, int height);

	/**
	 * The file extension that you declare your reader/writer to be compatible with.
	 * @return
	 * The file extension your {@link AsyncScreenshotReaderWriter} writes in.
	 */
	String getFormatExtension();

	/**
	 * This is for your reader to read out an imagePath and pass it back as a BufferedImage to be rendered in-game.
	 * @param imagePath
	 * The image path for your reader writer to read from.
	 * @return
	 * The completed BufferedImage.
	 */
	BufferedImage readImageAsBufferedImage(Path imagePath) throws IOException;
}
