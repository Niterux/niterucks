package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.Socket;

@Mixin(Connection.class)
public interface SocketAccessor {
	@Accessor("socket")
	Socket getSocket();
}
