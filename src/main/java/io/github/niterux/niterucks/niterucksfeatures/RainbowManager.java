package io.github.niterux.niterucks.niterucksfeatures;

import dev.kdrag0n.colorkt.conversion.ConversionGraph;
import dev.kdrag0n.colorkt.rgb.Srgb;
import dev.kdrag0n.colorkt.ucs.lch.Oklch;
import io.github.niterux.niterucks.Niterucks;

public class RainbowManager {
	private static final double[] RGB = new double[3];
	private static Oklch currColor = new Oklch(0.75, 0.12, 160);

	public static int getColor() {
		int red = (int) (Math.round(RGB[0] * 255) << 16);
		int green = (int) (Math.round(RGB[1] * 255) << 8);
		int blue = (int) (Math.round(RGB[2] * 255));
		return red + green + blue;
	}

	public static double[] getRawRgb() {
		return RGB;
	}

	public static void computeRainbow() {
		currColor = new Oklch(currColor.getLightness(), currColor.getChroma(), getRotation());
		Srgb SRGBColor = (Srgb) ConversionGraph.convert(currColor, kotlin.jvm.JvmClassMappingKt.getKotlinClass(Srgb.class));

		assert SRGBColor != null;
		RGB[0] = SRGBColor.getR();
		RGB[1] = SRGBColor.getG();
		RGB[2] = SRGBColor.getB();
	}

	private static double getRotation() {
		return ((System.currentTimeMillis() * 360 * Niterucks.CONFIG.rainbowSpeed.get()) / 1000) % 360;
	}
}
