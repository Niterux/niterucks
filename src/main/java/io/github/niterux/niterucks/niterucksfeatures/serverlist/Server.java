package io.github.niterux.niterucks.niterucksfeatures.serverlist;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;

public record Server(String ip, String name) {
	public Server(NbtCompound server) {
		this(server.getString("ip"), server.getString("name"));
	}

	public NbtCompound serialize() {
		NbtCompound serialized = new NbtCompound();
		serialized.put("ip", new NbtString(ip()));
		serialized.put("name", new NbtString(name()));
		return serialized;
	}
}
