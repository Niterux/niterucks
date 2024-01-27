package io.github.niterux.niterucks;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.niterux.niterucks.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.lwjgl.input.Keyboard;

public class Niterucks implements ClientModInitializer {
	public static final NiteLogger LOGGER = new NiteLogger("Niterucks");
	public static Config CONFIG = new Config();

	static {
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			LOGGER.level = 4;
		ConfigUI.getInstance().runWhenLoaded(() ->
			ConfigUI.getInstance().addWidget("vanilla", "keybinding", "io.github.niterux.niterucks.config.widget.KeyBindWidget"));
		ConfigManager manager = new JsonConfigManager(FabricLoader.getInstance()
			.getConfigDir().resolve("niterucks.json"), CONFIG.ROOT);
		AxolotlClientConfig.getInstance()
			.register(manager);
		manager.load();
	}

	@Override
	public void initClient() {
		LOGGER.info("initialized Niterucks!");
	}

	public static Screen getConfigScreen(Screen parent) {
		return ConfigUI.getInstance().getScreen(Niterucks.class.getClassLoader(),
			CONFIG.ROOT, parent);
	}
}
