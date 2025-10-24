package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.interaction.MultiplayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiplayerInteractionManager.class)
public class MultiplayerInteractionManagerMixin {
	@ModifyExpressionValue(method = "startMiningBlock(IIII)V", at = @At(value = "CONSTANT", args = "intValue=5"))
	private int removeMiningDelay(int original) {
		if (Niterucks.currentServer.equals("retromc.org") || Niterucks.currentServer.equals("mc.retromc.org") || Niterucks.currentServer.equals("betalands.com"))
			return 0;
		return original;
	}
}
