package io.github.niterux.niterucks.niterucksfeatures;

import dev.kdrag0n.colorkt.conversion.ConversionGraph;
import dev.kdrag0n.colorkt.rgb.Srgb;
import dev.kdrag0n.colorkt.ucs.lch.Oklch;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.HueAccessor;

public class RainbowManager {
	private static final double[] RGB = new double[3];
	private static final Oklch currColor = new Oklch(0.75, 0.12, 160);

	public static int getColor() {
		int red = (int) (Math.round(RGB[0] * 255) << 16);
		int green = (int) (Math.round(RGB[1] * 255) << 8);
		int blue = (int) (Math.round(RGB[2] * 255));
		return red + green + blue;
	}

	public static double[] getRawRgb() {
		return RGB;
	}

	public static void adjustRainbow() {
		//The hue is immutable so instead of creating a new object I just mixined into colorkt for the lulz
		((HueAccessor) (Object) currColor).setHue(((double) System.currentTimeMillis() / Niterucks.CONFIG.rainbowSpeed.get()) % 360);
		Srgb SRGBColor = (Srgb) ConversionGraph.convert(currColor, kotlin.jvm.JvmClassMappingKt.getKotlinClass(Srgb.class));

		assert SRGBColor != null;
		RGB[0] = SRGBColor.getR();
		RGB[1] = SRGBColor.getG();
		RGB[2] = SRGBColor.getB();
	}
}
