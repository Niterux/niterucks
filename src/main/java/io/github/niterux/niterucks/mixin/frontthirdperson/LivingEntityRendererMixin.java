package io.github.niterux.niterucks.mixin.frontthirdperson;

import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@ModifyArg(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;Ljava/lang/String;DDDI)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 0, remap = false), index = 0)
	private float fixNametagYaw(float angle) {
		return MiscUtils.fixSpriteYaw(angle);
	}

	@ModifyArg(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;Ljava/lang/String;DDDI)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1, remap = false), index = 0)
	private float fixNametagPitch(float angle) {
		return MiscUtils.fixSpritePitch(angle);
	}
}
