package io.github.niterux.niterucks.niterucksfeatures.serverlist;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.io.File;

public class ServerNBT {
	private final File serverDataFile;
	private Server[] serverList;

	public ServerNBT(File serverDataFile) {
		this.serverDataFile = serverDataFile;
	}

	private NbtCompound serializeToNBT() {
		NbtCompound rootElement = new NbtCompound();
		NbtList nbtServerList = new NbtList();
		for (Server server : serverList)
			nbtServerList.add(server.serialize());
		rootElement.put("servers", nbtServerList);
		return rootElement;
	}

	public void loadFromFile() {

	}

	public void saveToFile() {

	}
}
