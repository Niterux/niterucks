package io.github.niterux.niterucks.mixin.debugmenu.hitbox;

import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import net.minecraft.client.render.entity.ArrowRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRenderer.class)
public class ArrowRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/projectile/ArrowEntity;DDDFF)V", at = @At("HEAD"))
	private void addRayCastVisual(ArrowEntity arrowEntity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
		if (!GameFeaturesStates.selfIsHoldingBow || !GameFeaturesStates.hitboxEnabled)
			return;
		if (!Niterucks.CONFIG.interpolateHitboxes.get()) {
			double inverseTickDelta = (1.0f - tickDelta);
			x = x - ((arrowEntity.prevTickX - arrowEntity.x) * inverseTickDelta);
			y = y - ((arrowEntity.prevTickY - arrowEntity.y) * inverseTickDelta);
			z = z - ((arrowEntity.prevTickZ - arrowEntity.z) * inverseTickDelta);
		}

		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		GL11.glPushAttrib(GL11.GL_LINE_BIT | GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(MinecraftInstanceAccessor.getMinecraft().height / 120.0F);
		bufferBuilder.start(GL11.GL_LINES);
		bufferBuilder.color(255, 0, 255, 255);
		bufferBuilder.vertex(x, y, z);
		bufferBuilder.vertex(x + arrowEntity.velocityX, y + arrowEntity.velocityY, z + arrowEntity.velocityZ);
		bufferBuilder.end();
		GL11.glPopAttrib();
	}
}
