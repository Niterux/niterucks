package io.github.niterux.niterucks.mixin.authme;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.niterux.niterucks.config.optionstorage.AuthMeWholeListOptionStorage;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.packet.HandshakePacket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.network.packet.Packet;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin {
	@Unique
	private ObjectObjectImmutablePair<String, String> usernamePassword = null;

	@Shadow
	public abstract void sendPacket(Packet packet);

	@Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/lang/String;I)V", at = @At("TAIL"))
	private void keepAddress(Minecraft minecraft, String address, int port, CallbackInfo ci) {
		this.usernamePassword = getAuthMeEntry(address, port);
	}

	@SuppressWarnings("MixinAnnotationTarget")
	@ModifyExpressionValue(method = "*", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/Session;username:Ljava/lang/String;"), require = 0)
	private String overrideUsername(String original) {
		if (usernamePassword != null) {
			return usernamePassword.left();
		}
		return original;
	}

	@ModifyExpressionValue(method = "handleHandshake", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 0))
	private boolean forceOffline(boolean original) {
		//noinspection ConstantValue
		if (usernamePassword != null && !MinecraftInstanceAccessor.getMinecraft().session.username.equals(usernamePassword.left())) {
			return true;
		}
		return original;
	}

	@WrapMethod(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V")
	private void overrideHandshakeUsername(Packet packet, Operation<Void> original) {
		if (packet instanceof HandshakePacket handshakePacket && usernamePassword != null)
			handshakePacket.key = usernamePassword.left();
		original.call(packet);
	}

	@Inject(method = "handleLogin(Lnet/minecraft/network/packet/LoginPacket;)V", at = @At("TAIL"))
	private void sendLoginChatMessage(LoginPacket packet, CallbackInfo ci) {
		if (usernamePassword != null) {
			this.sendPacket(new net.minecraft.network.packet.ChatMessagePacket("/login " + usernamePassword.right()));
		}
	}

	@Unique
	private ObjectObjectImmutablePair<String, String> getAuthMeEntry(String address, int port) {
		String addressAndPort = address + ":" + port;
		var authMeHashMap = AuthMeWholeListOptionStorage.getInstance().toHashMap();
		var result = authMeHashMap.get(addressAndPort);
		if (result == null && port == 25565) {
			result = authMeHashMap.get(address);
		}
		return result;
	}
}
