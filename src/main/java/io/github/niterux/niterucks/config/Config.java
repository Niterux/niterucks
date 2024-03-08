package io.github.niterux.niterucks.config;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.niterux.niterucks.config.option.KeyBindOption;
import io.github.niterux.niterucks.mixin.invokers.InitBrightnessTableInvoker;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Config {
	private static final OptionBase.ChangeListener<Float> changeListenerBrightness;
	private static final OptionBase.ChangeListener<Boolean> changeListenerVsync;
	public final OptionCategory niterucks = OptionCategory.create("niterucks");
	public FloatOption fov = new FloatOption("FOV", 90.0F, 30.0F, 130.0F);
	public FloatOption brightness = new FloatOption("Brightness", 1.0F, changeListenerBrightness, 0.0F, 3.0F);
	public DoubleOption entityDistance = new DoubleOption("Entity Distance", 1.0, 0.5, 5.0);
	public FloatOption cloudHeight = new FloatOption("Cloud Height", 108.0F, 70.0F, 200.0F);
	public DoubleOption hotbarScreenSafety = new DoubleOption("Hotbar Screen Safety", 3.0, 0.0, 30.0);
	public BooleanOption useVSync = new BooleanOption("Use VSync", false, changeListenerVsync);
	public BooleanOption rainbowBlockOutline = new BooleanOption("Rainbow Block Outline", false);
	public final OptionCategory controls = OptionCategory.create("Controls");
	public KeyBindOption zoomButton = new KeyBindOption("zoom_button", Keyboard.KEY_C);
	public KeyBindOption playerListButton = new KeyBindOption("Show Player List", Keyboard.KEY_TAB);
	public BooleanOption swapMouseButtons = new BooleanOption("Swap Mouse Buttons", false);
	public final OptionCategory accessibility = OptionCategory.create("Accessibility");
	public DoubleOption rainbowSpeed = new DoubleOption("Rainbow Speed", 3.0, 1.0, 6.0);
	public final OptionCategory miscellaneous = OptionCategory.create("Miscellaneous");
	public BooleanOption wolfNameTags = new BooleanOption("Wolf Name Tags", true);
	public BooleanOption showItemIDs = new BooleanOption("Show Item ID's", false);
	public BooleanOption newSkeleton = new BooleanOption("Beta 1.8 Skeleton Model", true);
	public FloatOption viewmodelFov = new FloatOption("Viewmodel FOV", 70.0F, 30.0F, 130.0F);
	public final OptionCategory staff = OptionCategory.create("Staff");
	public KeyBindOption flyButton = new KeyBindOption("fly_button", Keyboard.KEY_R);
	public KeyBindOption adjustButton = new KeyBindOption("adjust_fly_speed", Keyboard.KEY_LCONTROL);
	public IntegerOption defaultFlySpeed = new IntegerOption("Default Flight Speed", 6, 0, 14);

	public Config() {
		niterucks.add(fov, brightness, entityDistance, cloudHeight, hotbarScreenSafety, useVSync, rainbowBlockOutline);
		controls.add(zoomButton, playerListButton, swapMouseButtons);
		accessibility.add(rainbowSpeed);
		miscellaneous.add(wolfNameTags, showItemIDs, newSkeleton, viewmodelFov);
		staff.add(flyButton, adjustButton, defaultFlySpeed);
		niterucks.add(controls, accessibility, miscellaneous, staff);
	}

	static {
		changeListenerBrightness = newBrightness -> {
			Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
			if (minecraft.world != null) {
				((InitBrightnessTableInvoker) minecraft.world.dimension).reinitBrightnessTable();
				minecraft.worldRenderer.m_6748042(); //reload chunks
			}
		};
		changeListenerVsync = Display::setVSyncEnabled;
	}
}
