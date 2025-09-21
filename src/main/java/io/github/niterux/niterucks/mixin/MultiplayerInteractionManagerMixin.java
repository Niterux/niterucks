package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.interaction.MultiplayerInteractionManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiplayerInteractionManager.class)
public class MultiplayerInteractionManagerMixin {
	@WrapOperation(method = "startMiningBlock(IIII)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/interaction/MultiplayerInteractionManager;miningCooldown:I", ordinal = 1, opcode = Opcodes.PUTFIELD))
	private void removeMiningDelay(MultiplayerInteractionManager instance, int value, Operation<Void> original) {
		if (Niterucks.currentServer.equals("retromc.org") || Niterucks.currentServer.equals("mc.retromc.org") || Niterucks.currentServer.equals("betalands.com")) {
			original.call(instance, 0);
		} else {
			original.call(instance, value);
		}
	}
}
