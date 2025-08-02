package io.github.niterux.niterucks.api.playerlist;

import org.jetbrains.annotations.Nullable;

public interface PlayerListProvider {
	@Nullable
	String[] getPlayerNames();

	void onDisconnectedFromServer();

	void onConnectedToServer(String serverAddress);

	boolean providesPingCount();

	@Nullable
	Integer[] getPingCounts();

	boolean providesMaxPlayersCount();

	int getMaxPlayerCount();
}
