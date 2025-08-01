package io.github.niterux.niterucks.bevofeatures;

import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class BetaEVOPlayerListProvider implements PlayerListProvider {
	@Override
	public @Nullable String[] getPlayerNames() {
		if (BetaEVO.playerList.isEmpty() || !BetaEVO.playerListPacketReceived)
			return null;
		ArrayList<String> players = new ArrayList<>();
		for(PlayerNameStatus playerNameStatus : BetaEVO.playerList.values()) {
			String playerName = "";
			if (playerNameStatus.getName() != null)
				playerName = playerNameStatus.getName();
			if (playerNameStatus.getNickname() != null)
				playerName = playerNameStatus.getNickname();
			if (playerNameStatus.getPrefix() != null)
				playerName = playerNameStatus.getPrefix() + playerName;
			if (!Objects.equals(playerName, ""))
				players.add(playerName);
		}
		return players.toArray(new String[0]);
	}

	@Override
	public void onDisconnectedFromServer() {
		BetaEVO.playerList.clear();
		BetaEVO.playerListPacketReceived = false;
	}

	@Override
	public void onConnectedToServer(String serverAddress) {
		BetaEVO.playerList.clear();
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
