package io.github.niterux.niterucks.mixin;

import net.minecraft.entity.player.PlayerInventory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.zoomAmount;
import static io.github.niterux.niterucks.niterucksfeatures.KeyStateManager.niterucksControls;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@ModifyVariable(method = "scrollInHotbar(I)V", at = @At(value = "FIELD",
		target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
		opcode = Opcodes.GETFIELD, ordinal = 0), ordinal = 0, argsOnly = true)
	private int intersectScrolling(int scrollAmount) {
		if (niterucksControls[0]) {
			zoomAmount += scrollAmount;
			if (zoomAmount > 10)
				zoomAmount = 10;
			else if (zoomAmount < 3)
				zoomAmount = 3;
			return 0;
		}
		return scrollAmount;
	}

}
