package io.github.niterux.niterucks;
import io.github.niterux.niterucks.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class Niterucks implements ClientModInitializer {
	public static final NiteLogger logger = new NiteLogger("Niterucks");
	public static final ConfigManager configManager = new ConfigManager("niterucks");
	public static boolean keyboardPressed() {
		if (Keyboard.isKeyDown(0)) {
			return false;
		}
		return Keyboard.getEventKeyState();
	}
	static {
		if(FabricLoader.getInstance().isDevelopmentEnvironment())
			logger.level = 4;
	}
	@Override
	public void initClient() {
		logger.info("initialized Niterucks!");
	}
}
