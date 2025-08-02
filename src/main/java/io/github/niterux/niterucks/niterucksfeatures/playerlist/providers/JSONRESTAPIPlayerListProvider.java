package io.github.niterux.niterucks.niterucksfeatures.playerlist.providers;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos.JSONDataPlayerListPOJO;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;

public class JSONRESTAPIPlayerListProvider extends RESTAPIPlayerListProvider {
	private final Class<? extends JSONDataPlayerListPOJO> dataPOJO;

	public JSONRESTAPIPlayerListProvider(RESTAPIPlayerListProviderConfig config, Class<? extends JSONDataPlayerListPOJO> dataPOJO) throws URISyntaxException {
		super(config);
		this.dataPOJO = dataPOJO;
	}

	@Override
	public String[] dataHandler(String data, String serverAddress) {
		return Niterucks.GSON.fromJson(data, dataPOJO).getPlayers(serverAddress);
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
		return false;
	}

	@Override
	public int getMaxPlayerCount() {
		return 0;
	}
}
