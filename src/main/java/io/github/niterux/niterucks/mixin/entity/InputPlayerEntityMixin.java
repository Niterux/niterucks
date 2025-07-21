package io.github.niterux.niterucks.mixin.entity;

import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputPlayerEntity.class)
public abstract class InputPlayerEntityMixin extends PlayerEntity {
	public InputPlayerEntityMixin(World world) {
		super(world);
	}

	@Inject(method = "tickAi()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;tickAi()V"))
	private void fixPlayerFallingOffBlocksWhileSneaking(CallbackInfo ci){
		if (this.isSneaking() && (((this.velocityX * this.velocityX) + (this.velocityZ * this.velocityZ)) < 0.00005d)) {
			this.velocityX = 0;
			this.velocityZ = 0;
		}
	}

	@Shadow
	public abstract boolean isSneaking();
}
