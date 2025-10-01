package io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ModernBetaPlayerListPOJO implements JSONDataPlayerListPOJO {
	public ModernBetaBlueMapPOJO[] players;

	@Override
	public String[] getPlayers(String serverAddress) {
		ObjectArrayList<String> playerNames = new ObjectArrayList<>(players.length);
		for (ModernBetaBlueMapPOJO player : players)
			if (player.name != null)
				playerNames.add(player.name);
		return playerNames.toArray(new String[0]);
	}
}
