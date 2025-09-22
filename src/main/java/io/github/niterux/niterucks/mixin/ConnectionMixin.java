package io.github.niterux.niterucks.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Socket;
import java.net.SocketException;

@Mixin(Connection.class)
public class ConnectionMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/net/Socket;setTrafficClass(I)V"))
	private void thing(Socket socket, String address, PacketHandler listener, CallbackInfo ci) throws SocketException {
		socket.setTcpNoDelay(true);
	}
}
