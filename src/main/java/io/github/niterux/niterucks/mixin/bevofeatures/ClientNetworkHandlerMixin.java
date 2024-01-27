package io.github.niterux.niterucks.mixin.bevofeatures;

import io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.packet.LoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flyAllowed;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {
	@ModifyConstant(method = "handleHandshake(Lnet/minecraft/network/packet/HandshakePacket;)V",
		constant = @Constant(stringValue = "http://www.minecraft.net/game/joinserver.jsp?user=", ordinal = 0))
	private String replaceUrl(String oldurl) {
		return "http://session.minecraft.net/game/joinserver.jsp?user=";
	}

	@Inject(method = "handleLogin(Lnet/minecraft/network/packet/LoginPacket;)V", at = @At("HEAD"))
	private void resetFly(LoginPacket par1, CallbackInfo ci) {
		/*this shouldn't be required due to connectionmixin, but it is a good idea just to make sure. I don't know if connectionmixin gets
		triggered on a timeout due to not being able to test that so this just double checks that fly is disabled before joining a server*/
		BetaEVOFlyHelper.resetFly();
		flyAllowed = false;
	}
}
