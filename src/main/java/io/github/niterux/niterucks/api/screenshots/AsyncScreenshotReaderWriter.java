package io.github.niterux.niterucks.api.screenshots;

import java.io.File;

public interface AsyncScreenshotReaderWriter {
	String writeScreenshot(File outputFile, int width, int height, Object pixelData);
	Object synchronouslyReadPixelData(int width, int height);
	String getFormatExtension();
}
