package io.github.niterux.niterucks.bevofeatures;

import net.minecraft.network.PacketHandler;
import net.minecraft.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BetaEVOPacket extends Packet {
	public String message;

	public BetaEVOPacket(String message) {
		this.message = message;
	}

	public BetaEVOPacket() {
	}

	public void read(DataInputStream inputStream) throws IOException {
		this.message = BetaEVOPacket.readString(inputStream, 1000);
		BetaEVOPacketDecoder.decode(this.message);
	}

	public void write(DataOutputStream outputStream) throws IOException {
		BetaEVOPacket.writeString(this.message, outputStream);
	}

	public void handle(PacketHandler packetHandler) {

	}

	public int getSize() {
		return 0;
	}
}

