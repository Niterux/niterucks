package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.mixin.accessors.ConnectionAccessor;
import io.github.niterux.niterucks.mixin.accessors.NetworkHandlerAccessor;
import io.github.niterux.niterucks.mixin.accessors.SocketAccessor;
import net.minecraft.client.interaction.MultiplayerInteractionManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(MultiplayerInteractionManager.class)
public class MultiplayerInteractionManagerMixin {
	@WrapOperation(method = "startMiningBlock(IIII)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/interaction/MultiplayerInteractionManager;miningCooldown:I", ordinal = 1, opcode = Opcodes.PUTFIELD))
	private void removeMiningDelay(MultiplayerInteractionManager instance, int value, Operation<Void> original) {
		String currentServer = ((SocketAccessor) ((ConnectionAccessor) (((NetworkHandlerAccessor) this).getNetworkHandler())).getConnection()).getSocket().getInetAddress().getHostName();
		if (Objects.equals(currentServer, "retromc.org") || Objects.equals(currentServer, "mc.retromc.org") || Objects.equals(currentServer, "betalands.com")) {
			original.call(instance, 0);
		} else {
			original.call(instance, value);
		}
	}
}
