package io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos;

public class ModernBetaPlayerListPOJO implements JSONDataPlayerListPOJO {
	public ModernBetaBlueMapPOJO[] players;

	@Override
	public String[] getPlayers(String serverAddress) {
		String[] playerNames = new String[players.length];
		for (int i = 0; i < players.length; i++) {
			playerNames[i] = players[i].name;
		}
		return playerNames;
	}
}
