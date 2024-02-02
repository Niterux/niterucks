package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow
	private int distanceOnNextBlock;
	@Shadow
	public float horizontalVelocity;
	@Shadow
	public World world;
	@Shadow
	public double x;
	@Shadow
	public double z;
	@Shadow
	public float f_4350946; //inventory brightness override
	@Final
	@Shadow
	public Box shape;
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

	/*	the view distance scaling disproportionately affects small entities and large
		entities, this is because the game *squares* the dimensions of the entity instead
		of just leaving them alone, to counteract this I've sorta "unsquared" things
		IMO this makes the entity distance culling work way better :P*/
	@ModifyExpressionValue(method = "isWithinViewDistance(D)Z", at = @At(value = "CONSTANT", args = "doubleValue=64", ordinal = 0))
	private double increaseEntityDistance(double original) {
		return original * original * Niterucks.CONFIG.ENTITYDISTANCE.get();
	}

	@ModifyVariable(method = "isWithinViewDistance(D)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private double unSquare(double original) {
		return original * original;
	}

	@Inject(method = "getBrightness(F)F", cancellable = true, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;f_4350946:F", ordinal = 2))
	private void fixEntityBlack(float par1, CallbackInfoReturnable<Float> cir) {
		//Fixes entities turning black above Y 129 and the vignette
		//noinspection SuspiciousNameCombination
		if (f_4350946 == 0.0F && MathHelper.floor(this.shape.maxY) > 128 && this.world
			.isAreaLoaded(
				MathHelper.floor(this.shape.minX),
				127,
				MathHelper.floor(this.shape.minZ),
				MathHelper.floor(this.shape.maxX),
				127,
				MathHelper.floor(this.shape.maxZ)
			))
			cir.setReturnValue(this.world.getBrightness(MathHelper.floor(this.x), 127, MathHelper.floor(this.z)));
	}
}
