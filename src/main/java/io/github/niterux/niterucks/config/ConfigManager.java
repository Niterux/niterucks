package io.github.niterux.niterucks.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;


public class ConfigManager {
	public static Boolean configRead = false;
	public static File configFile;
	public ConfigManager(String configName) {
		configFile = FabricLoader.getInstance().getConfigDir().resolve(configName + ".json").toFile();
		if(!configFile.exists()){
			initConfig();
		}
	}
	public static void initConfig(){
		System.out.println("no config!");
	}
	public void readConfig(){

	}

}
