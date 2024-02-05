package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PngMetadata {
	private static final long PNG_SIGNATURE = -8552249625308161526L;
	private static final int IHDR_TYPE = 1229472850;
	private static final int IHDR_LENGTH = 13;
	private final int width, height;
	public PngMetadata(int width, int height) {
		this.width = width;
		this.height = height;
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PngMetadata that = (PngMetadata) o;
		return width == that.width && height == that.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height);
	}

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}

	@Override
	public String toString() {
		return "PngMetadata{" +
			"width=" + width +
			", height=" + height +
			'}';
	}
}
