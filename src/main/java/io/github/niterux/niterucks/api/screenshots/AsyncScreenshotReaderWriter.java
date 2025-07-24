package io.github.niterux.niterucks.api.screenshots;

import java.text.SimpleDateFormat;

public interface AsyncScreenshotReaderWriter {
	String writeScreenshot(SimpleDateFormat screenshotDateFormat, int width, int height, Object pixelData);
	Object synchronouslyReadPixelData(int width, int height);
	String getFormatExtension();
}
