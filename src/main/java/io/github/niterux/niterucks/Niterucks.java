package io.github.niterux.niterucks;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.niterux.niterucks.config.Config;
import io.github.niterux.niterucks.config.screen.NiterucksConfigScreen;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class Niterucks implements ClientModInitializer {
	public static final Logger LOGGER = Logger.getLogger("io.github.niterux.niterucks.Niterucks");
	public static Config CONFIG;
	public static final String modVersion = FabricLoader.getInstance().getModContainer("niterucks").get().getMetadata().getVersion().getFriendlyString();

	static {
		CONFIG = new Config();
		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			ConfigUI.getInstance().runWhenLoaded(() -> {
				ConfigUI.getInstance().addWidget("vanilla", "keybinding", "io.github.niterux.niterucks.config.widget.KeyBindWidget");
				ConfigUI.getInstance().addWidget("vanilla", "authMeList", "io.github.niterux.niterucks.config.widget.AuthMeWholeListWidget");
				ConfigUI.getInstance().addScreen("vanilla", NiterucksConfigScreen.class);
			});
		ConfigManager manager = new JsonConfigManager(FabricLoader.getInstance()
			.getConfigDir().resolve("niterucks.json"), CONFIG.niterucks);
		AxolotlClientConfig.getInstance()
			.register(manager);
		manager.load();
	}

	@Override
	public void initClient() {
		LOGGER.info("initialized Niterucks!");
		Display.setVSyncEnabled(Niterucks.CONFIG.useVSync.get());
		ByteBuffer[] icons = new ByteBuffer[3];
		try {
			icons[0] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/128x.png"));
			icons[1] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/32x.png"));
			icons[2] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/16x.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Display.setIcon(icons);
	}

	public static Screen getConfigScreen(Screen parent) {
		return ConfigUI.getInstance().getScreen(Niterucks.class.getClassLoader(),
			CONFIG.niterucks, parent);
	}
}
