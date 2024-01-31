package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientNetworkHandler.class)
public interface ConnectionAccessor {
	@Accessor("connection")
	Connection getConnection();
}
