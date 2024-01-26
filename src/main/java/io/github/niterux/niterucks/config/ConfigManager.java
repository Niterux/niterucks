package io.github.niterux.niterucks.config;

import io.github.niterux.niterucks.Niterucks;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;


public class ConfigManager {
	public static Boolean configRead = false;
	public static File configFile;

	public ConfigManager(String configName) {
		configFile = FabricLoader.getInstance().getConfigDir().resolve(configName + ".json").toFile();
		if (!configFile.exists()) {
			initConfig();
		}
	}

	public static void initConfig() {
		Niterucks.logger.debug("no config!");
	}

	public void readConfig() {

	}

}
