package io.github.niterux.niterucks.mixin.entity;

import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
	@Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;DDDFF)V"))
	private void renderHitbox(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta, CallbackInfo ci) {
		if (!GameFeaturesStates.hitboxEnabled)
			return;
		Box hitbox = entity.shape.move(dx - entity.x, dy - entity.y, dz - entity.z);
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(MinecraftInstanceAccessor.getMinecraft().height / 480.0F);
		GL11.glColor3f(1, 1, 1);
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		bufferBuilder.start(GL11.GL_LINES);
		bufferBuilder.vertex(hitbox.minX, hitbox.minY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.minX, hitbox.minY, hitbox.maxZ);

		bufferBuilder.vertex(hitbox.minX, hitbox.minY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.minX, hitbox.maxY, hitbox.minZ);

		bufferBuilder.vertex(hitbox.minX, hitbox.minY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.maxX, hitbox.minY, hitbox.minZ);

		bufferBuilder.vertex(hitbox.maxX, hitbox.minY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.maxX, hitbox.maxY, hitbox.minZ);

		bufferBuilder.vertex(hitbox.maxX, hitbox.minY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.maxX, hitbox.minY, hitbox.maxZ);

		bufferBuilder.vertex(hitbox.maxX, hitbox.minY, hitbox.maxZ);
		bufferBuilder.vertex(hitbox.maxX, hitbox.maxY, hitbox.maxZ);

		bufferBuilder.vertex(hitbox.maxX, hitbox.minY, hitbox.maxZ);
		bufferBuilder.vertex(hitbox.minX, hitbox.minY, hitbox.maxZ);

		bufferBuilder.vertex(hitbox.minX, hitbox.minY, hitbox.maxZ);
		bufferBuilder.vertex(hitbox.minX, hitbox.maxY, hitbox.maxZ);

		bufferBuilder.vertex(hitbox.minX, hitbox.maxY, hitbox.maxZ);
		bufferBuilder.vertex(hitbox.maxX, hitbox.maxY, hitbox.maxZ);

		bufferBuilder.vertex(hitbox.maxX, hitbox.maxY, hitbox.maxZ);
		bufferBuilder.vertex(hitbox.maxX, hitbox.maxY, hitbox.minZ);

		bufferBuilder.vertex(hitbox.maxX, hitbox.maxY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.minX, hitbox.maxY, hitbox.minZ);

		bufferBuilder.vertex(hitbox.minX, hitbox.maxY, hitbox.minZ);
		bufferBuilder.vertex(hitbox.minX, hitbox.maxY, hitbox.maxZ);
		bufferBuilder.end();
		GL11.glPopAttrib();
	}
}
