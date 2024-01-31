package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.client.interaction.MultiplayerInteractionManager;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiplayerInteractionManager.class)
public interface NetworkHandlerAccessor {
	@Accessor("networkHandler")
	ClientNetworkHandler getNetworkHandler();
}
