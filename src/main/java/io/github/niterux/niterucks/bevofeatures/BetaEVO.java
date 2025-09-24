package io.github.niterux.niterucks.bevofeatures;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class BetaEVO {
	public static Object2ObjectOpenHashMap<String, PlayerNameStatus> playerList = new Object2ObjectOpenHashMap<>();
	public static boolean playerListPacketReceived = false;
}
