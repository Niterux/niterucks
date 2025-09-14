package io.github.niterux.niterucks.niterucksfeatures;

import dev.kdrag0n.colorkt.rgb.Srgb;
import dev.kdrag0n.colorkt.ucs.lch.Oklch;
import io.github.niterux.niterucks.Niterucks;

public class RainbowManager {
	private static final double[] RGB = new double[3];

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
		Srgb SRGBColor = new Oklch(0.75, 0.12, getRotation()).toOklab().toLinearSrgb().toSrgb();
		RGB[0] = SRGBColor.getR();
		RGB[1] = SRGBColor.getG();
		RGB[2] = SRGBColor.getB();
	}

	private static double getRotation() {
		return ((System.currentTimeMillis() * Niterucks.CONFIG.rainbowSpeed.get()) / (1000d / 360d)) % 360d;
	}
}
