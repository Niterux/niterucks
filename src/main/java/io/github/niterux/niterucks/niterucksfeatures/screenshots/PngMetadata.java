package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record PngMetadata(int width, int height) {
	private static final long PNG_SIGNATURE = -8552249625308161526L;
	private static final int IHDR_TYPE = 1229472850;
	private static final int IHDR_LENGTH = 13;

	public static PngMetadata read(InputStream encoded) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(encoded);
		if (dataInputStream.readLong() != PNG_SIGNATURE) {
			throw new IOException("Bad PNG Signature");
		} else if (dataInputStream.readInt() != IHDR_LENGTH) {
			throw new IOException("Bad length for IHDR chunk!");
		} else if (dataInputStream.readInt() != IHDR_TYPE) {
			throw new IOException("Bad type for IHDR chunk!");
		} else {
			int width = dataInputStream.readInt();
			int height = dataInputStream.readInt();
			return new PngMetadata(width, height);
		}
	}

	public static void validate(InputStream encoded) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(encoded);
		if (dataInputStream.readLong() != PNG_SIGNATURE) {
			throw new IOException("Bad PNG Signature");
		} else if (dataInputStream.readInt() != IHDR_LENGTH) {
			throw new IOException("Bad length for IHDR chunk!");
		} else if (dataInputStream.readInt() != IHDR_TYPE) {
			throw new IOException("Bad type for IHDR chunk!");
		}
	}

	public static void validate(Path p) throws IOException {
		validate(Files.newInputStream(p));
	}


	@Override
	public @NotNull String toString() {
		return "PngMetadata{" +
			"width=" + width +
			", height=" + height +
			'}';
	}
}
