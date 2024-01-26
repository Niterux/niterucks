package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow
	private int distanceOnNextBlock;
	@Shadow
	public float horizontalVelocity;
	@Unique
	private int belowCurrentBlockId;
	@Unique
	private int currentBlockId;

	@Inject(method = "move(DDD)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;horizontalVelocity:F", ordinal = 1, shift = At.Shift.BEFORE))
	private void fixStep(CallbackInfo ci) {
		if (horizontalVelocity > distanceOnNextBlock && !(currentBlockId != 0 || belowCurrentBlockId != 0)) {
			//this is annoying, because distanceOnNextBlock is an integer, I have to convert it to an int
			//meaning there will be a random variance between the time you land and start walking for footsteps to start
			distanceOnNextBlock = (int) Math.ceil(horizontalVelocity);
		}
	}

	@ModifyExpressionValue(method = "move(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)I", ordinal = 0))
	private int captureBlock(int original) {
		return currentBlockId = original;
	}

	@ModifyExpressionValue(method = "move(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)I", ordinal = 2))
	private int captureBelowBlock(int original) {
		return belowCurrentBlockId = original;
	}

	@ModifyExpressionValue(method = "move(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)I", ordinal = 1))
	private int resetBelowBlock(int original) {
		if (original != Block.FENCE.id) {
			belowCurrentBlockId = 0;
		}
		return original;
	}
}
