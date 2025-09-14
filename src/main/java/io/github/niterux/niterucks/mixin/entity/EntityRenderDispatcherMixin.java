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
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(MinecraftInstanceAccessor.getMinecraft().height / 480.0F);
		GL11.glColor3f(1, 1, 1);
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		float renderPosX = (float) (dx - entity.x);
		float renderPosY = (float) (dy - entity.y);
		float renderPosZ = (float) (dz - entity.z);
		bufferBuilder.addOffset(renderPosX, renderPosY, renderPosZ);
		bufferBuilder.start(GL11.GL_LINE_LOOP);
		Box shape = entity.shape;
		// Hamiltonian path
		bufferBuilder.vertex(shape.minX, shape.minY, shape.minZ);
		bufferBuilder.vertex(shape.minX, shape.minY, shape.maxZ);
		bufferBuilder.vertex(shape.minX, shape.maxY, shape.maxZ);
		bufferBuilder.vertex(shape.maxX, shape.maxY, shape.maxZ);
		bufferBuilder.vertex(shape.maxX, shape.minY, shape.maxZ);
		bufferBuilder.vertex(shape.maxX, shape.minY, shape.minZ);
		bufferBuilder.vertex(shape.maxX, shape.maxY, shape.minZ);
		bufferBuilder.vertex(shape.minX, shape.maxY, shape.minZ);
		bufferBuilder.end();
		// Extra edges
		bufferBuilder.start(GL11.GL_LINES);
		bufferBuilder.vertex(shape.minX, shape.minY, shape.minZ);
		bufferBuilder.vertex(shape.maxX, shape.minY, shape.minZ);

		bufferBuilder.vertex(shape.minX, shape.minY, shape.maxZ);
		bufferBuilder.vertex(shape.maxX, shape.minY, shape.maxZ);

		bufferBuilder.vertex(shape.maxX, shape.maxY, shape.minZ);
		bufferBuilder.vertex(shape.maxX, shape.maxY, shape.maxZ);

		bufferBuilder.vertex(shape.minX, shape.maxY, shape.minZ);
		bufferBuilder.vertex(shape.minX, shape.maxY, shape.maxZ);
		bufferBuilder.end();
		bufferBuilder.addOffset(-renderPosX, -renderPosY, -renderPosZ);
		GL11.glPopAttrib();
	}
}
