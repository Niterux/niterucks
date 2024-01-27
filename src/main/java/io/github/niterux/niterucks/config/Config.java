package io.github.niterux.niterucks.config;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionBase;
import io.github.niterux.niterucks.config.option.KeyBindOption;
import io.github.niterux.niterucks.mixin.invokers.initBrightnessTableInvoker;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import org.lwjgl.input.Keyboard;

public class Config {
	public final OptionCategory ROOT = OptionCategory.create("niterucks");
	public KeyBindOption zoomButton = new KeyBindOption("zoom_button", Keyboard.KEY_C);
	public KeyBindOption flyButton = new KeyBindOption("fly_button", Keyboard.KEY_R);
	public KeyBindOption adjustButton = new KeyBindOption("adjust_fly_speed", Keyboard.KEY_LCONTROL);
	public FloatOption FOV = new FloatOption("FOV", 90.0F, 30.0F, 130.0F);
	private static final OptionBase.ChangeListener<Float> changeListener;
	public FloatOption BRIGHTNESS = new FloatOption("Brightness", 0.0F, changeListener,0.0F , 1.0F);
	public FloatOption CLOUD_HEIGHT = new FloatOption("Cloud Height", 108.0F, 70.0F, 200.0F);

	public Config() {
		ROOT.add(FOV, BRIGHTNESS, CLOUD_HEIGHT);
		ROOT.add(zoomButton, flyButton, adjustButton);
	}
	static {
		changeListener = updateBrightness -> {
			((initBrightnessTableInvoker) MinecraftInstanceAccessor.getMinecraft().world.dimension).reinitBrightnessTable();
			MinecraftInstanceAccessor.getMinecraft().worldRenderer.m_6748042(); //reload chunks
		};
	}
}
