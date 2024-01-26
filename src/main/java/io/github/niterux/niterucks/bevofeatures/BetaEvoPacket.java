package io.github.niterux.niterucks.bevofeatures;

import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class BetaEvoPacket extends Packet {
	public String message;

	public BetaEvoPacket(String message) {
		this.message = message;
	}

	public BetaEvoPacket() {
	}

	public void read(DataInputStream var1) {
		this.message = BetaEvoPacket.readString(var1, 1000);
		BetaEvoPacketDecoder.decode(this.message);
	}

	public void write(DataOutputStream var1) {
		BetaEvoPacket.writeString(this.message, var1);
	}

	public void handle(PacketHandler var1) {

	}

	public int getSize() {
		return 0;
	}
}

