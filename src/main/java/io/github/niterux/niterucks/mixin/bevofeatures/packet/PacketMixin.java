package io.github.niterux.niterucks.mixin.bevofeatures.packet;

import io.github.niterux.niterucks.bevofeatures.BetaEVOPacket;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ornithemc.osl.networking.impl.mixin.common.PacketAccessor.register;

@Mixin(Packet.class)
public class PacketMixin {
	@Inject(method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;register(IZZLjava/lang/Class;)V", ordinal = 0))
	private static void newPacket(CallbackInfo info) {
		register(220, true, true, BetaEVOPacket.class);
	}
}
