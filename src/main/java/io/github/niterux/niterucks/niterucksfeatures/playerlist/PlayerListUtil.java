package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import io.github.niterux.niterucks.api.playerlist.PlayerListProviderRegistry;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos.JSONDataPlayerListPOJO;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.providers.JSONRESTAPIPlayerListProvider;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.providers.RESTAPIPlayerListProviderConfig;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class PlayerListUtil {
	private static String[] cachedPlayerList;

	public static boolean updateCacheReturnsFalseIfInvalidated(String[] players) {
		boolean valid = Arrays.equals(players, cachedPlayerList);
		if (valid)
			return true;
		cachedPlayerList = Arrays.copyOf(players, players.length);
		return false;
	}

	public static void invalidateDrawListCache() {
		cachedPlayerList = null;
	}

	public static void simpleRegisterJSONPlayerListProvider(String server, String url, Class<? extends JSONDataPlayerListPOJO> dataPOJO) {
		simpleRegisterJSONPlayerListProvider(new String[]{server}, url, dataPOJO);
	}

	public static void simpleRegisterJSONPlayerListProvider(String[] servers, String url, Class<? extends JSONDataPlayerListPOJO> dataPOJO) {
		try {
			RESTAPIPlayerListProviderConfig config = new RESTAPIPlayerListProviderConfig(servers, URI.create(url));
			PlayerListProviderRegistry.register(new JSONRESTAPIPlayerListProvider(config, dataPOJO));
		} catch (URISyntaxException e) {
			Niterucks.LOGGER.debug("Failed to add {} as a supported PlayerListProvider! {}", url, e);
		}
	}

	@Nullable
	public static String[] getPlayerList() {
		for (PlayerListProvider playerListProvider : PlayerListProviderRegistry.getRegisteredPlayerListProviders()) {
			String[] players = playerListProvider.getPlayerNames();
			if (players == null || players.length == 0)
				continue;
			return players;
		}
		return null;
	}

	public static int getMaxPlayers() {
		for (PlayerListProvider playerListProvider : PlayerListProviderRegistry.getRegisteredPlayerListProviders()) {
			if (playerListProvider.providesMaxPlayersCount())
				return playerListProvider.getMaxPlayerCount();
		}
		return 0;
	}
}
