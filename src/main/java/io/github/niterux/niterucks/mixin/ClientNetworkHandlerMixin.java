package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {
	@ModifyExpressionValue(method = "handleHandshake(Lnet/minecraft/network/packet/HandshakePacket;)V",
		at = @At(value = "CONSTANT", args = "stringValue=http://www.minecraft.net/game/joinserver.jsp?user=", ordinal = 0))
	private String replaceUrl(String oldurl) {
		return Niterucks.CONFIG.useHTTPS.get() ? "https://session.minecraft.net/game/joinserver.jsp?user=" : "http://session.minecraft.net/game/joinserver.jsp?user=";
	}
}
