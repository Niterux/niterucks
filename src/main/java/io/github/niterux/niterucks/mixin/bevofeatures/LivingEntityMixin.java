package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flyingTouchedGround;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	/*
	This removes fall damage from the client
	This prevents the loud step noise from playing when you fall on a server with flying
	This doesn't actually prevent fall damage unless you are flying in singleplayer which is currently impossible
	See bevofeatures.LocalPlayerEntityMixin to see how I prevent fall damage with a field redirect
	*/
	@ModifyVariable(method = "applyFallDamage(F)V", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
	private float removeFallDamageClient(float value) {
		// the warning here is wrong
		if (!flyingTouchedGround && ((LivingEntity) (Object) this instanceof InputPlayerEntity)) {
			value = 0F;
		}
		return value;
	}
}
