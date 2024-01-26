package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.network.packet.LoginPacket;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoginPacket.class)
public class LoginPacketMixin {
	//This communicates to the server that the client is BetaEvo and that BetaEvo packets are accepted!
	@Shadow
	public byte dimension = 40;
}
