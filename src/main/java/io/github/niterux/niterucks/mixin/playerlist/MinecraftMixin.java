package io.github.niterux.niterucks.mixin.playerlist;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import io.github.niterux.niterucks.api.playerlist.PlayerListProviderRegistry;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Unique
	private boolean connectionStatus = false;

	@Inject(method = "run()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;resetCache()V"))
	private void jankyConnectionChecker(CallbackInfo ci) {
		if (this.isMultiplayer() == connectionStatus)
			return;
		if (connectionStatus) {
			for (PlayerListProvider playerListProvider : PlayerListProviderRegistry.getRegisteredPlayerListProviders())
				playerListProvider.onDisconnectedFromServer();
		} else
			for (PlayerListProvider playerListProvider : PlayerListProviderRegistry.getRegisteredPlayerListProviders())
				playerListProvider.onConnectedToServer(Niterucks.currentServer);
		connectionStatus = !connectionStatus;
	}

	@Shadow
	public abstract boolean isMultiplayer();
}
