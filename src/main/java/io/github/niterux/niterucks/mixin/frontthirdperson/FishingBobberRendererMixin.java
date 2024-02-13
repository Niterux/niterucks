package io.github.niterux.niterucks.mixin.frontthirdperson;

import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.render.entity.FishingBobberRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingBobberRenderer.class)
public class FishingBobberRendererMixin {
	@ModifyArg(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 0, remap = false), index = 0)
	private float fixBobberYaw(float angle){
		return MiscUtils.fixSpriteYaw(angle);
	}
	@ModifyArg(method = "render(Lnet/minecraft/entity/FishingBobberEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1, remap = false), index = 0)
	private float fixBobberPitch(float angle){
		return MiscUtils.fixSpritePitch(angle);
	}
}
