package io.github.niterux.niterucks.api.screenshots;

import java.util.LinkedHashMap;

public class ScreenshotFormatRegistry {
	private static final LinkedHashMap<String, AsyncScreenshotReaderWriter> registeredScreenshotReaderWriters = new LinkedHashMap<>();

	public static void register(AsyncScreenshotReaderWriter h) {
		registeredScreenshotReaderWriters.put(h.getFormatExtension(), h);
	}

	public static LinkedHashMap<String, AsyncScreenshotReaderWriter> getRegisteredScreenshotReaderWriters() {
		return registeredScreenshotReaderWriters;
	}

	public static String[] getRegisteredScreenshotReaderWriterNames() {
		return registeredScreenshotReaderWriters.keySet().toArray(new String[0]);

	}
}
