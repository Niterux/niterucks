package io.github.niterux.niterucks.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(World world) {
		super(world);
	}

	@Inject(method = "tickAi()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isDead()Z"))
	private void fixSneakingFallBug(CallbackInfo ci){
		if (Math.abs(this.velocityX) < 0.005) {
			this.velocityX = 0.0;
		}

		if (Math.abs(this.velocityY) < 0.005) {
			this.velocityY = 0.0;
		}

		if (Math.abs(this.velocityZ) < 0.005) {
			this.velocityZ = 0.0;
		}
	}
}
