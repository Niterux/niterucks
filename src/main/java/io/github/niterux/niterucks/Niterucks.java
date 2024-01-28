package io.github.niterux.niterucks;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.niterux.niterucks.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;


public class Niterucks implements ClientModInitializer {
	public static final NiteLogger LOGGER = new NiteLogger("Niterucks");
	public static Config CONFIG;
	public static String gsonVersion;

	static {
		CONFIG = new Config();
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			LOGGER.level = 4;
		ConfigUI.getInstance().runWhenLoaded(() ->
			ConfigUI.getInstance().addWidget("vanilla", "keybinding", "io.github.niterux.niterucks.config.widget.KeyBindWidget"));
		ConfigManager manager = new JsonConfigManager(FabricLoader.getInstance()
			.getConfigDir().resolve("niterucks.json"), CONFIG.ROOT);
		AxolotlClientConfig.getInstance()
			.register(manager);
		manager.load();
		String gsonVersion = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Niterucks.class.getClassLoader().getResourceAsStream("/META-INF/MANIFEST.MF")))).lines().map(s -> s.split(": ")).filter(s -> s[0].equals("Bundle-Version")).map(s -> s[1]).findFirst().orElse("");
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
