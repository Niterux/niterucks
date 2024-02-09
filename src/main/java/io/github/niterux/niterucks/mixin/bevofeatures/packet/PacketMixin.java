package io.github.niterux.niterucks.mixin.bevofeatures.packet;

import io.github.niterux.niterucks.bevofeatures.BetaEVOPacket;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Packet.class)
public class PacketMixin {
	@Shadow
	static void register(int id, boolean s2c, boolean c2s, @SuppressWarnings("rawtypes") Class type) {
	}

	@Inject(method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;register(IZZLjava/lang/Class;)V", ordinal = 0))
	private static void newPacket(CallbackInfo info) {
		register(220, true, true, BetaEVOPacket.class);
	}
}
