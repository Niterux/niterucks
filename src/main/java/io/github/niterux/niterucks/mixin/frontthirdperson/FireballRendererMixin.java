package io.github.niterux.niterucks.mixin.frontthirdperson;

import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.render.entity.FireballRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireballRenderer.class)
public class FireballRendererMixin {
	@ModifyArg(method = "render(Lnet/minecraft/entity/projectile/ProjectileEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 0, remap = false), index = 0)
	private float fixFireballYaw(float angle) {
		return MiscUtils.fixSpriteYaw(angle);
	}

	@ModifyArg(method = "render(Lnet/minecraft/entity/projectile/ProjectileEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1, remap = false), index = 0)
	private float fixFireballPitch(float angle) {
		return MiscUtils.fixSpritePitch(angle);
	}
}
