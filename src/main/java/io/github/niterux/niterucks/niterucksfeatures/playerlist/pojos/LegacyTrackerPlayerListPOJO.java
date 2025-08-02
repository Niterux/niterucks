package io.github.niterux.niterucks.niterucksfeatures.playerlist.pojos;

public class LegacyTrackerPlayerListPOJO implements JSONDataPlayerListPOJO {
	public String[] players;

	@Override
	public String[] getPlayers(String serverAddress) {
		return players;
	}
}
