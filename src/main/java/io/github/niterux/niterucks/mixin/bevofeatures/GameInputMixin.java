package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.client.player.input.GameInput;
import net.minecraft.client.player.input.PlayerInput;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flySpeed;
import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Mixin(GameInput.class)
public class GameInputMixin extends PlayerInput {
	@Shadow
	private boolean[] f_6643588 = new boolean[10];

	@Inject(method = "tick(Lnet/minecraft/entity/living/player/PlayerEntity;)V", at = @At("HEAD"))
	public void keyOverride(PlayerEntity par1, CallbackInfo ci){
		if(flying) {
			f_6643588[4] = false;
			f_6643588[5] = false;
		}
	}
	@Inject(method = "tick(Lnet/minecraft/entity/living/player/PlayerEntity;)V", at = @At("TAIL"))
	public void speedOverride(PlayerEntity par1, CallbackInfo ci){
		if (flying) {
			par1.updateVelocity(this.movementSideways, this.movementForward, (float)flySpeed);
		}
	}


}
