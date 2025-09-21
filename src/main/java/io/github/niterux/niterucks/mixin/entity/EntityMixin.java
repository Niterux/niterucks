package io.github.niterux.niterucks.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow
	public float horizontalVelocity;
	@Shadow
	public World world;
	@Shadow
	public double x;
	@Shadow
	public double z;
	@Shadow
	public double velocityX;
	@Shadow
	public double velocityZ;
	@Shadow
	private int distanceOnNextBlock;
	@Unique
	private int belowCurrentBlockId;
	@Unique
	private int currentBlockId;

	@Inject(method = "move(DDD)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;horizontalVelocity:F", ordinal = 1, shift = At.Shift.BEFORE))
	private void fixStep(CallbackInfo ci) {
		if (horizontalVelocity > distanceOnNextBlock && (currentBlockId == 0 && belowCurrentBlockId == 0)) {
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

	/*	the view distance scaling disproportionately affects small entities and large
		entities, this is because the game *squares* the dimensions of the entity instead
		of just leaving them alone, to counteract this I've sorta "unsquared" things
		IMO this makes the entity distance culling work way better :P*/
	@ModifyExpressionValue(method = "isWithinViewDistance(D)Z", at = @At(value = "CONSTANT", args = "doubleValue=64", ordinal = 0))
	private double increaseEntityDistance(double original) {
		return original * original * Niterucks.CONFIG.entityDistance.get();
	}

	@ModifyVariable(method = "isWithinViewDistance(D)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private double unSquare(double original) {
		return original * original;
	}

	@ModifyReturnValue(method = "getBrightness(F)F", at = @At(value = "RETURN", ordinal = 1))
	private float fixEntityBrightnessFallback(float minEntityBrightness) {
		//Fixes entities turning black above Y 129 and the vignette
		return Math.max(this.world.dimension.brightnessTable[15 - this.world.ambientDarkness], minEntityBrightness);
	}

	@Inject(method = "move(DDD)V", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;", ordinal = 0)), at = @At(value = "CONSTANT", args = "doubleValue=0.0", ordinal = 0))
	private void fixSneakingOffBlocksXAxis(double dx, double dy, double dz, CallbackInfo ci) {
		this.velocityX = 0;
	}

	@Inject(method = "move(DDD)V", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;", ordinal = 1)), at = @At(value = "CONSTANT", args = "doubleValue=0.0", ordinal = 0))
	private void fixSneakingOffBlocksZAxis(double dx, double dy, double dz, CallbackInfo ci) {
		this.velocityZ = 0;
	}
}
