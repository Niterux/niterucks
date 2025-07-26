package io.github.niterux.niterucks.api.screenshots;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface AsyncScreenshotReaderWriter {
	String writeScreenshot(File outputFile, int width, int height, Object pixelData);
	Object mainThreadReadPixelData(int width, int height);
	String getFormatExtension();
	BufferedImage readImageAsBufferedImage(Path imagePath) throws IOException;
}
