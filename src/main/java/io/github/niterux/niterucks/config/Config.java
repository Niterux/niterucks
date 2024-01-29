package io.github.niterux.niterucks.config;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.DoubleOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionBase;
import io.github.niterux.niterucks.config.option.KeyBindOption;
import io.github.niterux.niterucks.mixin.invokers.initBrightnessTableInvoker;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Config {
	private static final OptionBase.ChangeListener<Float> changeListener;
	public final OptionCategory ROOT = OptionCategory.create("niterucks");
	public KeyBindOption zoomButton = new KeyBindOption("zoom_button", Keyboard.KEY_C);
	public KeyBindOption flyButton = new KeyBindOption("fly_button", Keyboard.KEY_R);
	public KeyBindOption adjustButton = new KeyBindOption("adjust_fly_speed", Keyboard.KEY_LCONTROL);
	public FloatOption FOV = new FloatOption("FOV", 90.0F, 30.0F, 130.0F);
	public FloatOption BRIGHTNESS = new FloatOption("Brightness", 0.0F, changeListener,0.0F , 1.0F);
	public FloatOption CLOUD_HEIGHT = new FloatOption("Cloud Height", 108.0F, 70.0F, 200.0F);
	public DoubleOption ENTITYDISTANCE = new DoubleOption("Entity Distance", 1.0, 0.5, 5.0);

	public Config() {
		ROOT.add(ENTITYDISTANCE);
		ROOT.add(FOV, BRIGHTNESS, CLOUD_HEIGHT);
		ROOT.add(zoomButton, flyButton, adjustButton);
	}
	static {
		changeListener = updateBrightness -> {
			Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
			if(minecraft.world != null) {
				((initBrightnessTableInvoker) minecraft.world.dimension).reinitBrightnessTable();
				minecraft.worldRenderer.m_6748042(); //reload chunks
			}
		};
	}
}
