package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {

	@ModifyConstant(method = "handleHandshake(Lnet/minecraft/network/packet/HandshakePacket;)V",
		constant = @Constant(stringValue = "http://www.minecraft.net/game/joinserver.jsp?user=", ordinal = 0))
	private String replaceUrl(String oldurl) {
		return "http://session.minecraft.net/game/joinserver.jsp?user=";
	}
}
