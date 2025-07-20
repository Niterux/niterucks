package io.github.niterux.niterucks.mixin.frontthirdperson;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.entity.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
	@ModifyExpressionValue(method = "renderParticles(Lnet/minecraft/entity/Entity;F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
	private float fixParticleYaw(float angle) {
		return MiscUtils.fixSpriteYaw(angle);
	}

	@ModifyExpressionValue(method = "renderParticles(Lnet/minecraft/entity/Entity;F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
	private float fixParticlePitch(float angle) {
		return MiscUtils.fixSpritePitch(angle);
	}
}
