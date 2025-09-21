package io.github.niterux.niterucks.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.render.entity.ArmoredEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmoredEntityRenderer.class)
public class ArmoredEntityRendererMixin {
	@Unique
	private boolean isBow = false;

	@Inject(method = "renderDecoration(Lnet/minecraft/entity/living/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 3, remap = false))
	private void pushUselessMatrix(LivingEntity entity, float tickDelta, CallbackInfo ci, @Local(ordinal = 0) ItemStack itemStack) {
		isBow = (itemStack.itemId == Item.BOW.id && Niterucks.CONFIG.newSkeleton.get());
		if (isBow)
			GL11.glPushMatrix();
	}

	@Inject(method = "renderDecoration(Lnet/minecraft/entity/living/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 6, shift = At.Shift.AFTER, remap = false))
	private void poseBow(LivingEntity entity, float tickDelta, CallbackInfo ci) {
		if (!isBow)
			return;
		GL11.glPopMatrix();
		GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
		GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(0.625F, -0.625F, 0.625F);
		GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
	}
}
