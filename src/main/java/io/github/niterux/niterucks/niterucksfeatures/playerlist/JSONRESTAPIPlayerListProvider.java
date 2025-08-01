package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JSONRESTAPIPlayerListProvider extends RESTAPIPlayerListProvider {
	public JSONRESTAPIPlayerListProvider(RESTAPIPlayerListProviderConfig config) {
		super(config);
	}

	@Override
	public @Nullable String[] getPlayerNames() {
		return new String[0];
	}

	@Override
	public void onDisconnectedFromServer() {

	}

	@Override
	public void onConnectedToServer(String serverAddress) {
		for (String supportedServerAddress : getConfig().supportedServerAddresses()) {
			if (supportedServerAddress.equals(serverAddress)) {
				setEnabled(true);
			}
		}
	}

	@Override
	public @NotNull Boolean providesPingCount() {
		return false;
	}

	@Override
	public @Nullable Integer[] getPingCounts() {
		return null;
	}
}
