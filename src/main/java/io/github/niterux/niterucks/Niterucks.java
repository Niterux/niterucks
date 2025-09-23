package io.github.niterux.niterucks;

import com.google.gson.Gson;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.niterux.niterucks.api.playerlist.PlayerListProviderRegistry;
import io.github.niterux.niterucks.api.screenshots.ScreenshotFormatRegistry;
import io.github.niterux.niterucks.bevofeatures.BetaEVOPlayerListProvider;
import io.github.niterux.niterucks.config.Config;
import io.github.niterux.niterucks.config.optionstorage.AuthMeWholeListOptionStorage;
import io.github.niterux.niterucks.config.screen.NiterucksConfigScreen;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.PlayerListUtil;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos.LegacyTrackerPlayerListPOJO;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos.ModernBetaPlayerListPOJO;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.providers.BetacraftJSONRESTAPIPlayerListProvider;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.providers.RESTAPIPlayerListProviderConfig;
import io.github.niterux.niterucks.niterucksfeatures.screenshots.AsyncImageIOReaderWriter;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.MinecraftEvents;
import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class Niterucks implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Niterucks");
	public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer("niterucks").orElseThrow().getMetadata().getVersion().getFriendlyString();
	public static final Gson GSON = new Gson();
	public static Config CONFIG;
	/* This is used to avoid drawlist memory leaks, we add each sign with a
	drawlist to this list and each frame we check the globalBlockEntities to see
	if the worldrenderer still has the sign entity, if it doesn't then we
	release the list and dereference the sign in our list. blockentities have
	no method that is called when they are removed from the client/world so
	asides from manually mixing into every method that removes blockentities
	(which I don't prefer because that assumes that other mods aren't installed)
	this is the best that we can do.*/
	public static final ReferenceArraySet<SignBlockEntity> SIGN_DRAWLIST_OBJECT_CACHE_LIST = new ReferenceArraySet<>();
	// Used to keep track of the currently connected server domain to avoid
	// having to use reverse DNS lookups, Minecraft doesn't track this itself.
	public static String currentServer = "";

	static {
		ScreenshotFormatRegistry.register(new AsyncImageIOReaderWriter("png"));
		ScreenshotFormatRegistry.register(new AsyncImageIOReaderWriter("jpg"));
		ScreenshotFormatRegistry.register(new AsyncImageIOReaderWriter("bmp"));
		PlayerListProviderRegistry.register(new BetaEVOPlayerListProvider());
		PlayerListUtil.simpleRegisterJSONPlayerListProvider(new String[]{"mc.retromc.org", "betalands.com"}, "https://api.retromc.org/api/v1/server/players", ModernBetaPlayerListPOJO.class);
		PlayerListUtil.simpleRegisterJSONPlayerListProvider("sweetwaterbeta.us", "https://servers.legacyminecraft.com/api/getPlayersOnline?id=42", LegacyTrackerPlayerListPOJO.class);
		PlayerListUtil.simpleRegisterJSONPlayerListProvider("2beta2t.net", "https://servers.legacyminecraft.com/api/getPlayersOnline?id=9", LegacyTrackerPlayerListPOJO.class);
		PlayerListUtil.simpleRegisterJSONPlayerListProvider("betamc.org", "https://servers.legacyminecraft.com/api/getPlayersOnline?id=55", LegacyTrackerPlayerListPOJO.class);
		PlayerListUtil.simpleRegisterJSONPlayerListProvider(new String[]{"mc.retromc.org", "betalands.com"}, "https://servers.legacyminecraft.com/api/getPlayersOnline?id=10", LegacyTrackerPlayerListPOJO.class);
		PlayerListUtil.simpleRegisterJSONPlayerListProvider(new String[]{"oldschoolminecraft.net", "os-mc.net"}, "https://servers.legacyminecraft.com/api/getPlayersOnline?id=7", LegacyTrackerPlayerListPOJO.class);
		PlayerListUtil.simpleRegisterJSONPlayerListProvider("betacraft.uk", "https://servers.legacyminecraft.com/api/getPlayersOnline?id=3", LegacyTrackerPlayerListPOJO.class);
		try {
			PlayerListProviderRegistry.register(new BetacraftJSONRESTAPIPlayerListProvider(new RESTAPIPlayerListProviderConfig(new String[]{""}, URI.create("https://api.betacraft.uk/v2/server_list"))));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		PlayerListUtil.simpleRegisterJSONPlayerListProvider("beta.modernbeta.org", "https://map.modernbeta.org/maps/world/live/players.json", ModernBetaPlayerListPOJO.class);

		CONFIG = new Config();
		ConfigUI.getInstance().runWhenLoaded(() -> {
			ConfigUI.getInstance().addWidget("vanilla", "keybinding", "io.github.niterux.niterucks.config.widget.KeyBindWidget");
			ConfigUI.getInstance().addWidget("vanilla", "screenshotformat[]", "io.github.niterux.niterucks.config.widget.ScreenshotFormatArrayWidget");
			ConfigUI.getInstance().addWidget("vanilla", "authme_category", "io.github.niterux.niterucks.config.widget.AuthMeCategoryWidget");
			ConfigUI.getInstance().addScreen("vanilla", NiterucksConfigScreen.class);
		});
		ConfigManager manager = new JsonConfigManager(FabricLoader.getInstance()
			.getConfigDir().resolve("niterucks.json"), CONFIG.niterucks);
		AxolotlClientConfig.getInstance()
			.register(manager);
		manager.load();
	}

	public static Screen getConfigScreen(Screen parent) {
		return ConfigUI.getInstance().getScreen(Niterucks.class.getClassLoader(),
			CONFIG.niterucks, parent);
	}

	@Override
	public void initClient() {
		AuthMeWholeListOptionStorage.getInstance().load();

		ByteBuffer[] icons = new ByteBuffer[3];
		try {
			icons[0] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/128x.png"));
			icons[1] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/32x.png"));
			icons[2] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/16x.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Display.setIcon(icons);
		CONFIG.screenshotFormat.setEnabled(CONFIG.enableScreenshotEnhancements.get());
		MinecraftEvents.READY.register(minecraft -> Display.setVSyncEnabled(Niterucks.CONFIG.useVSync.get()));

		//noinspection ResultOfMethodCallIgnored
		FabricLoader.getInstance().getGameDir().resolve("screenshots").toFile().mkdir();

		LOGGER.info("initialized Niterucks!");
	}
}
