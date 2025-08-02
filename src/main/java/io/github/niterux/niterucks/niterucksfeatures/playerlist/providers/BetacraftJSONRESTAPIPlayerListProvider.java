package io.github.niterux.niterucks.niterucksfeatures.playerlist.providers;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos.BetacraftPlayersPOJO;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos.BetacraftServerPOJO;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class BetacraftJSONRESTAPIPlayerListProvider extends RESTAPIPlayerListProvider {
	int maxPlayers = -1;

	public BetacraftJSONRESTAPIPlayerListProvider(RESTAPIPlayerListProviderConfig config) throws URISyntaxException {
		super(config);
	}

	@Override
	public String[] dataHandler(String data, String serverAddress) {
		BetacraftServerPOJO[] servers = Niterucks.GSON.fromJson(data, BetacraftServerPOJO[].class);
		for (BetacraftServerPOJO server : servers) {
			if (server.players == null)
				continue;
			if (!server.socket.startsWith(serverAddress))
				continue;
			maxPlayers = (server.max_players == 0) ? -1 : server.max_players;
			ArrayList<String> playersArray = new ArrayList<>();
			for (BetacraftPlayersPOJO player : server.players) {
				if (player.username != null)
					playersArray.add(player.username);
			}
			return playersArray.toArray(new String[0]);
		}
		return null;
	}

	@Override
	public boolean isSupportedServer(String serverAddress) {
		return Niterucks.CONFIG.useBetacraftAPI.get();
	}

	@Override
	public boolean providesPingCount() {
		return false;
	}

	@Override
	public @Nullable Integer[] getPingCounts() {
		return null;
	}

	@Override
	public boolean providesMaxPlayersCount() {
		return maxPlayers != -1;
	}

	@Override
	public int getMaxPlayerCount() {
		return maxPlayers;
	}
}
