package io.github.niterux.niterucks.config;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.niterux.niterucks.config.option.KeyBindOption;
import org.lwjgl.input.Keyboard;

public class Config {
	public final OptionCategory ROOT = OptionCategory.create("niterucks");
	public KeyBindOption flyButton = new KeyBindOption("fly_button", Keyboard.KEY_R);
	public KeyBindOption adjustButton = new KeyBindOption("adjust_fly_speed", Keyboard.KEY_LCONTROL);
	public FloatOption FOV = new FloatOption("options.fov", 90.0F, 30.0F, 110.0F);
	public FloatOption BRIGHTNESS = new FloatOption("Brightness", 0.0F, 0.0F, 1.0F);
	public FloatOption CLOUD_HEIGHT = new FloatOption("Cloud Height", 108.0F, 70.0F, 200.0F);

	public Config() {
		ROOT.add(FOV, BRIGHTNESS, CLOUD_HEIGHT);
		ROOT.add(flyButton, adjustButton);
	}
}
