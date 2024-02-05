package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import com.mojang.blaze3d.platform.MemoryTracker;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
	private static final IntBuffer BUFFER = MemoryTracker.createIntBuffer(4194304);

	public static int genTextures() {
		return GL11.glGenTextures();
	}

	public static void deleteTextures(int i) {
		GL11.glDeleteTextures(i);
	}

	public static int uploadTexture(int i, BufferedImage bufferedImage) {
		return upload(i, bufferedImage, false, false);
	}

	public static int upload(int i, BufferedImage bufferedImage, boolean bl, boolean bl2) {
		prepare(i, bufferedImage.getWidth(), bufferedImage.getHeight());
		return upload(i, bufferedImage, 0, 0, bl, bl2);
	}

	public static void prepare(int i, int j, int k) {
		prepareImage(i, 0, j, k);
	}

	public static void prepareImage(int i, int j, int k, int l) {
		deleteTextures(i);
		bind(i);
		if (j >= 0) {
			GL11.glTexParameteri(3553, 33085, j);
			GL11.glTexParameterf(3553, 33082, 0.0F);
			GL11.glTexParameterf(3553, 33083, (float) j);
			GL11.glTexParameterf(3553, 34049, 0.0F);
		}

		for (int m = 0; m <= j; ++m) {
			GL11.glTexImage2D(3553, m, 6408, k >> m, l >> m, 0, 32993, 33639, (IntBuffer) null);
		}
	}

	public static int upload(int i, BufferedImage bufferedImage, int j, int k, boolean bl, boolean bl2) {
		bind(i);
		upload(bufferedImage, j, k, bl, bl2);
		return i;
	}

	private static void upload(BufferedImage bufferedImage, int i, int j, boolean bl, boolean bl2) {
		int k = bufferedImage.getWidth();
		int l = bufferedImage.getHeight();
		int m = 4194304 / k;
		int[] is = new int[m * k];
		setFilterWithBlur(bl);
		setTextureClamp(bl2);

		for (int n = 0; n < k * l; n += k * m) {
			int o = n / k;
			int p = Math.min(m, l - o);
			int q = k * p;
			bufferedImage.getRGB(0, o, k, p, is, 0, k);
			putInBuffer(is, q);
			GL11.glTexSubImage2D(3553, 0, i, j + o, k, p, 32993, 33639, BUFFER);
		}
	}

	private static void setTextureClamp(boolean bl) {
		if (bl) {
			GL11.glTexParameteri(3553, 10242, 10496);
			GL11.glTexParameteri(3553, 10243, 10496);
		} else {
			GL11.glTexParameteri(3553, 10242, 10497);
			GL11.glTexParameteri(3553, 10243, 10497);
		}
	}

	private static void setFilterWithBlur(boolean bl) {
		setTextureFilter(bl, false);
	}

	private static void setTextureFilter(boolean bl, boolean bl2) {
		if (bl) {
			GL11.glTexParameteri(3553, 10241, bl2 ? 9987 : 9729);
			GL11.glTexParameteri(3553, 10240, 9729);
		} else {
			GL11.glTexParameteri(3553, 10241, bl2 ? 9986 : 9728);
			GL11.glTexParameteri(3553, 10240, 9728);
		}
	}

	private static void putInBuffer(int[] is, int i) {
		putInBufferAt(is, 0, i);
	}

	private static void putInBufferAt(int[] is, int i, int j) {
		int[] js = is;
		if (MinecraftInstanceAccessor.getMinecraft().options.anaglyph) {
			js = getAnaglyphColors(is);
		}

		BUFFER.clear();
		BUFFER.put(js, i, j);
		BUFFER.position(0).limit(j);
	}

	static void bind(int i) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, i);
	}

	public static BufferedImage readImage(InputStream inputStream) throws IOException {
		BufferedImage var1;
		try {
			var1 = ImageIO.read(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var1;
	}

	public static int[] getAnaglyphColors(int[] is) {
		int[] js = new int[is.length];

		for (int i = 0; i < is.length; ++i) {
			js[i] = getAnaglyphColor(is[i]);
		}

		return js;
	}

	public static int getAnaglyphColor(int i) {
		int j = i >> 24 & 0xFF;
		int k = i >> 16 & 0xFF;
		int l = i >> 8 & 0xFF;
		int m = i & 0xFF;
		int n = (k * 30 + l * 59 + m * 11) / 100;
		int o = (k * 30 + l * 70) / 100;
		int p = (k * 30 + m * 70) / 100;
		return j << 24 | n << 16 | o << 8 | p;
	}
}
