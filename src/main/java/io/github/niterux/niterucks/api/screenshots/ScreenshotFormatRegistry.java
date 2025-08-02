package io.github.niterux.niterucks.api.screenshots;

import io.github.niterux.niterucks.Niterucks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ScreenshotFormatRegistry {
	private static final LinkedHashMap<String, AsyncScreenshotReaderWriter> registeredScreenshotReaderWriters = new LinkedHashMap<>();

	public static void register(AsyncScreenshotReaderWriter h) {
		registeredScreenshotReaderWriters.put(h.getFormatExtension(), h);
		if (Objects.nonNull(Niterucks.CONFIG))
			Niterucks.CONFIG.screenshotFormat.addFormat(h.getFormatExtension());
	}

	public static LinkedHashMap<String, AsyncScreenshotReaderWriter> getRegisteredScreenshotReaderWriters() {
		return registeredScreenshotReaderWriters;
	}

	public static ArrayList<String> getRegisteredScreenshotReaderWriterNames() {
		return new ArrayList<>(registeredScreenshotReaderWriters.keySet());
	}
}
