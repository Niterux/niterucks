package io.github.niterux.niterucks.config;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.niterux.niterucks.api.screenshots.ScreenshotFormatRegistry;
import io.github.niterux.niterucks.config.option.AuthMeCategory;
import io.github.niterux.niterucks.config.option.KeyBindOption;
import io.github.niterux.niterucks.config.option.ScreenshotFormatArrayOption;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.mixin.invokers.InitBrightnessTableInvoker;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Config {
	private static final OptionBase.ChangeListener<Float> changeListenerBrightness;
	private static final OptionBase.ChangeListener<Boolean> changeListenerVsync;

	static {
		changeListenerBrightness = newBrightness -> {
			Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
			if (minecraft != null && minecraft.world != null) {
				((InitBrightnessTableInvoker) minecraft.world.dimension).reinitBrightnessTable();
				minecraft.worldRenderer.m_6748042(); //reload chunks
			}
		};
		changeListenerVsync = Display::setVSyncEnabled;
	}

	public final OptionCategory niterucks = OptionCategory.create("niterucks");
	public final OptionCategory controls = OptionCategory.create("Controls");
	public final OptionCategory accessibility = OptionCategory.create("Accessibility");
	public final OptionCategory miscellaneous = OptionCategory.create("Miscellaneous");
	public final OptionCategory staff = OptionCategory.create("Staff");
	public final OptionCategory screenshots = OptionCategory.create("Screenshots");
	public final OptionCategory authMe = new AuthMeCategory();
	public final ScreenshotFormatArrayOption screenshotFormat = new ScreenshotFormatArrayOption("Format", ScreenshotFormatRegistry.getRegisteredScreenshotReaderWriterNames(), "png");
	public FloatOption fov = new FloatOption("FOV", 90.0F, 30.0F, 130.0F);
	public FloatOption brightness = new FloatOption("Brightness", 1.0F, changeListenerBrightness, 0.0F, 3.0F);
	public BooleanOption showSeed = new BooleanOption("Show Seed", "Show the world seed on F3 menu (debug menu)", true);
	public BooleanOption useFeetCoordinates = new BooleanOption("useFeetCoordinates", true);
	public DoubleOption entityDistance = new DoubleOption("Entity Distance", 1.0, 0.5, 5.0);
	public FloatOption cloudHeight = new FloatOption("Cloud Height", 108.0F, 70.0F, 200.0F);
	public IntegerOption hotbarScreenSafety = new IntegerOption("Hotbar Screen Safety", "How far up from the bottom of the screen<br>should the hotbar be displayed in pixels.", 3, 0, 30);
	public BooleanOption useVSync = new BooleanOption("Use VSync", false, changeListenerVsync);
	public BooleanOption rainbowBlockOutline = new BooleanOption("Rainbow Block Outline", false);
	public KeyBindOption zoomButton = new KeyBindOption("zoom_button", Keyboard.KEY_C);
	public KeyBindOption playerListButton = new KeyBindOption("Show Player List", Keyboard.KEY_TAB);
	public BooleanOption swapMouseButtons = new BooleanOption("Swap Mouse Buttons", false);
	public DoubleOption rainbowSpeed = new DoubleOption("Rainbow Speed", "Sets the speed of rainbow rotation in rotations per second.", 0.5, 0.1, 2.0);
	public BooleanOption wolfNameTags = new BooleanOption("Wolf Name Tags", true);
	public BooleanOption useBetacraftAPI = new BooleanOption("Use Betacraft API", "Run the Betacraft API in the background<br>to get a list of players for the servers<br>that you join.<br>Some servers provide their own APIs<br>that can be used to get a player list<br>but this will always run in the background<br>if it is enabled.", false);
	public BooleanOption enableVBO = new BooleanOption("Enable VBOs", "Requires a restart to take effect.", false);
	public BooleanOption pitchBillboarding = new BooleanOption("Billboard Items on Pitch", "Billboard item entities on pitch, makes them face the camera.", false);
	public BooleanOption showItemIDs = new BooleanOption("Show Item ID's", "Shows the ID and metadata value of items in<br>your inventory when hovered over.", false);
	public BooleanOption newSkeleton = new BooleanOption("Beta 1.8 Skeleton Model", true);
	public FloatOption viewmodelFov = new FloatOption("Viewmodel FOV", 70.0F, 30.0F, 130.0F);
	public KeyBindOption flyButton = new KeyBindOption("fly_button", Keyboard.KEY_R);
	public KeyBindOption adjustButton = new KeyBindOption("adjust_fly_speed", Keyboard.KEY_LCONTROL);
	public IntegerOption defaultFlySpeed = new IntegerOption("Default Flight Speed", 6, 0, 14);
	public BooleanOption enableScreenshotEnhancements = new BooleanOption("Enable Screenshot Enhancements", "Whether to enable asynchronous<br>screenshotting with custom formats.<br>Disable this if you are having issues<br>with mod compatibility.", true, screenshotFormat::setEnabled);

	public Config() {
		brightness.set(1.0f);
		niterucks.add(fov, showSeed, useFeetCoordinates, entityDistance, cloudHeight, hotbarScreenSafety, useVSync, rainbowBlockOutline);
		controls.add(zoomButton, playerListButton, swapMouseButtons);
		accessibility.add(rainbowSpeed);
		miscellaneous.add(wolfNameTags, showItemIDs, newSkeleton, viewmodelFov, useBetacraftAPI, enableVBO, pitchBillboarding);
		staff.add(flyButton, adjustButton, defaultFlySpeed);
		screenshots.add(enableScreenshotEnhancements, screenshotFormat);
		niterucks.add(controls, accessibility, miscellaneous, staff, screenshots, authMe);
	}
}
