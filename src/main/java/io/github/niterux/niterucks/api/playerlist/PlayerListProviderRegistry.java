package io.github.niterux.niterucks.api.playerlist;

import java.util.ArrayList;

public class PlayerListProviderRegistry {
	private static final ArrayList<PlayerListProvider> registeredPlayerListProviders = new ArrayList<>();

	public static void register(PlayerListProvider h) {
		registeredPlayerListProviders.add(h);
	}

	public static ArrayList<PlayerListProvider> getRegisteredPlayerListProviders() {
		return registeredPlayerListProviders;
	}
}
