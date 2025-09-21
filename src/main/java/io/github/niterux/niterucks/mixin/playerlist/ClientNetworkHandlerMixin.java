package io.github.niterux.niterucks.mixin.playerlist;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import io.github.niterux.niterucks.api.playerlist.PlayerListProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {
	@Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/lang/String;I)V", at = @At("TAIL"))
	private void keepAddress(Minecraft minecraft, String address, int port, CallbackInfo ci) {
		Niterucks.currentServer = address;
		for (PlayerListProvider playerListProvider : PlayerListProviderRegistry.getRegisteredPlayerListProviders())
			playerListProvider.onConnectedToServer(address);
	}
}
