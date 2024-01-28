package io.github.niterux.niterucks;

import com.google.gson.Gson;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.niterux.niterucks.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Niterucks implements ClientModInitializer {
	public static final NiteLogger LOGGER = new NiteLogger("Niterucks");
	public static Config CONFIG;
	public static String gsonVersion = "2.2.4";

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
	}

	@Override
	public void initClient() {
		LOGGER.info("initialized Niterucks!");

        File gsonFile;
        try {
            gsonFile = new File(Gson.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL().getPath());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Manifest gsonManifest;
        try {
            gsonManifest = new JarFile(gsonFile).getManifest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gsonVersion = gsonManifest.getMainAttributes().getValue("Bundle-Version");
	}

	public static Screen getConfigScreen(Screen parent) {
		return ConfigUI.getInstance().getScreen(Niterucks.class.getClassLoader(),
			CONFIG.ROOT, parent);
	}
}
