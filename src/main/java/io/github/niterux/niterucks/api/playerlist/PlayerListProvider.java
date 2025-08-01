package io.github.niterux.niterucks.api.playerlist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlayerListProvider {
	@Nullable
	String[] getPlayerNames();
	void onDisconnectedFromServer();
	void onConnectedToServer(String serverAddress);
	@NotNull
	Boolean providesPingCount();
	@Nullable
	Integer[] getPingCounts();
}
