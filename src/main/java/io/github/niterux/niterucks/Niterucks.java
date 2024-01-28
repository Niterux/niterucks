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

import java.io.IOException;
import java.util.jar.JarFile;

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
	}

	@Override
	public void initClient() {
		LOGGER.info("initialized Niterucks!");
		JarFile gsonJar;
		/*We need the version of Gson because earlier Ornithe installers
		would install 2.2.2, which has bugs causing files to not be read.*/
        try {
			//Find where the Gson class comes from and make a JarFile from it
			gsonJar = new JarFile(Gson.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			//Use that JarFile to find the manifest and get the Bundle-Version
			gsonVersion = gsonJar.getManifest().getMainAttributes().getValue("Bundle-Version");
			gsonJar.close();
        } catch (IOException e) {
            LOGGER.info("Could not read the Gson jar file, assuming the version is 2.2.4!");
        }
		if(gsonVersion == null){
			LOGGER.info("Couldn't read the Gson version from manifest! Assuming the version is 2.2.4");
			gsonVersion = "2.2.4";
		}
	}

	public static Screen getConfigScreen(Screen parent) {
		return ConfigUI.getInstance().getScreen(Niterucks.class.getClassLoader(),
			CONFIG.ROOT, parent);
	}
}
