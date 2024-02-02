package io.github.niterux.niterucks.bevofeatures;

import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class BetaEVOPacket extends Packet {
	public String message;

	public BetaEVOPacket(String message) {
		this.message = message;
	}

	public BetaEVOPacket() {
	}

	public void read(DataInputStream var1) {
		this.message = BetaEVOPacket.readString(var1, 1000);
		BetaEVOPacketDecoder.decode(this.message);
	}

	public void write(DataOutputStream var1) {
		BetaEVOPacket.writeString(this.message, var1);
	}

	public void handle(PacketHandler var1) {

	}

	public int getSize() {
		return 0;
	}
}

