package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.entity.player.PlayerInventory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.*;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@ModifyVariable(method = "scrollInHotbar(I)V", at = @At(value = "FIELD",
		target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
		opcode = Opcodes.GETFIELD, ordinal = 0), ordinal = 0, argsOnly = true)
	private int intersectScrolling(int scrollAmount) {
		if (flying && flyingControls[3]) {
			flySpeed += scrollAmount;
			if (flySpeed > 12)
				flySpeed = 12;
			else if (flySpeed < 0)
				flySpeed = 0;
			return 0;
		}
		return scrollAmount;
	}

}
