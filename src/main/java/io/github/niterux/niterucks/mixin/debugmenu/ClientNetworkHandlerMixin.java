package io.github.niterux.niterucks.mixin.debugmenu;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {
	@WrapOperation(method = "handleChatMessage(Lnet/minecraft/network/packet/ChatMessagePacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GameGui;addChatMessage(Ljava/lang/String;)V"))
	private void hideChatMessagesUponRequest(GameGui instance, String text, Operation<Void> original) {
		if (!GameFeaturesStates.chatHidden)
			original.call(instance, text);
	}
}
