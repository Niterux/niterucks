package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.entity.player.PlayerInventory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.*;
import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flySpeed;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@Shadow
	public int selectedSlot;

	@ModifyVariable(method = "scrollInHotbar(I)V", at = @At(value = "FIELD",
		target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
		opcode = Opcodes.GETFIELD, ordinal = 0), ordinal = 0, argsOnly = true)
	private int intersectScrolling(int scrollAmount) {
		if (flying && flyingControls[3]) {
			flySpeed += scrollAmount;
			if (flySpeed > 16)
				flySpeed = 16;
			else if (flySpeed < 0)
				flySpeed = 0;
			return 0;
		}
		return scrollAmount;
	}

}
