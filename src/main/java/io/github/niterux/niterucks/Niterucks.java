package io.github.niterux.niterucks;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.FloatOption;
import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.lwjgl.input.Keyboard;

public class Niterucks implements ClientModInitializer {
	public static final NiteLogger logger = new NiteLogger("Niterucks");
	public static FloatOption fov = new FloatOption("options.fov", 90.0F, 30.0F, 110.0F);
	public static FloatOption brightness = new FloatOption("Brightness", 0.0F, 0.0F, 1.0F);
	public static FloatOption cloudHeight = new FloatOption("Cloud Height", 108.0F, 70.0F, 200.0F);

	public static boolean keyboardPressed() {
		if (Keyboard.getEventKey() == 0) {
			return false;
		}
		return Keyboard.getEventKeyState();
	}

	static {
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			logger.level = 4;
		OptionCategory Niterucks = OptionCategory.create("Niterucks");
		Niterucks.add(fov);
		Niterucks.add(brightness);
		Niterucks.add(cloudHeight);
		AxolotlClientConfig.getInstance().register(new JsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve("niterucks" + ".json"), Niterucks));
	}

	@Override
	public void initClient() {
		logger.info("initialized Niterucks!");
	}
}
