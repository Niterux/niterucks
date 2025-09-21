package io.github.niterux.niterucks.mixin.playerlist;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import io.github.niterux.niterucks.api.playerlist.PlayerListProviderRegistry;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
	@Inject(method = "disconnect(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At("HEAD"))
	private void onDisconnectedEvent(String reason, Object[] args, CallbackInfo ci) {
		Niterucks.currentServer = "";
		for (PlayerListProvider playerListProvider : PlayerListProviderRegistry.getRegisteredPlayerListProviders())
			playerListProvider.onDisconnectedFromServer();
	}
}
