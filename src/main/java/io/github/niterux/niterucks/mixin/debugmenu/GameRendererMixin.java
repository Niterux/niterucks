package io.github.niterux.niterucks.mixin.debugmenu;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft minecraft;

	@Inject(method = "renderWorld(FJ)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;zoom:D", ordinal = 0))
	private void renderChunkBounds(float tickDelta, long renderTimeLimit, CallbackInfo ci, @Local(ordinal = 0) LivingEntity camera) {
		if (!GameFeaturesStates.chunkBordersEnabled)
			return;
		double renderX = camera.prevTickX + (camera.x - camera.prevTickX) * (double) tickDelta;
		double renderY = camera.prevTickY + (camera.y - camera.prevTickY) * (double) tickDelta;
		double renderZ = camera.prevTickZ + (camera.z - camera.prevTickZ) * (double) tickDelta;
		double feetRenderPos = camera.eyeHeightSneakOffset - camera.eyeHeight;
		int chunkCornerX = MathHelper.floor(renderX / 16.0) * 16;
		int chunkCornerZ = MathHelper.floor(renderZ / 16.0) * 16;
		GL11.glLineWidth(minecraft.height / 480.0F);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glDepthMask(false);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		bufferBuilder.start(GL11.GL_LINES);

		//check for slime chunks
		if (minecraft.world.dimension.id == 0) {
			WorldChunk thisChunk = minecraft.world.getChunk(MathHelper.floor(renderX), MathHelper.floor(renderZ));
			if (thisChunk.getRandomForSlime(987234911L).nextInt(10) == 0) {
				bufferBuilder.color(0, 255, 0, 255);
			} else {
				bufferBuilder.color(255, 0, 0, 255);
			}
		} else {
			bufferBuilder.color(0, 255, 255, 255);
		}
		//render feet pos and height pos
		renderChunkSquare(chunkCornerX - renderX, feetRenderPos, chunkCornerZ - renderZ, bufferBuilder);
		renderChunkSquare(chunkCornerX - renderX, feetRenderPos + camera.height, chunkCornerZ - renderZ, bufferBuilder);
		//render the 4 chunk corners
		bufferBuilder.vertex(chunkCornerX - renderX, 0 - renderY, chunkCornerZ - renderZ);
		bufferBuilder.vertex(chunkCornerX - renderX, 128 - renderY, chunkCornerZ - renderZ);
		bufferBuilder.vertex(chunkCornerX - renderX + 16, 128 - renderY, chunkCornerZ - renderZ);
		bufferBuilder.vertex(chunkCornerX - renderX + 16, 0 - renderY, chunkCornerZ - renderZ);
		bufferBuilder.vertex(chunkCornerX - renderX, 0 - renderY, chunkCornerZ - renderZ + 16);
		bufferBuilder.vertex(chunkCornerX - renderX, 128 - renderY, chunkCornerZ - renderZ + 16);
		bufferBuilder.vertex(chunkCornerX - renderX + 16, 128 - renderY, chunkCornerZ - renderZ + 16);
		bufferBuilder.vertex(chunkCornerX - renderX + 16, 0 - renderY, chunkCornerZ - renderZ + 16);
		renderChunkSquare(chunkCornerX - renderX, 128 - renderY, chunkCornerZ - renderZ, bufferBuilder);
		renderChunkSquare(chunkCornerX - renderX, 0 - renderY, chunkCornerZ - renderZ, bufferBuilder);
		//subchunk loop
		bufferBuilder.color(255, 0, 255, 255);
		for (int ypos = 16; ypos <= 112; ypos += 16)
			renderChunkSquare(chunkCornerX - renderX, ypos - renderY, chunkCornerZ - renderZ, bufferBuilder);
		bufferBuilder.end();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		//GL11.glDepthMask(true);
	}

	@Unique
	private static void renderChunkSquare(double x, double y, double z, BufferBuilder bufferBuilder) {
		bufferBuilder.vertex(x, y, z);
		bufferBuilder.vertex(x, y, z + 16);

		bufferBuilder.vertex(x, y, z + 16);
		bufferBuilder.vertex(x + 16, y, z + 16);

		bufferBuilder.vertex(x + 16, y, z + 16);
		bufferBuilder.vertex(x + 16, y, z);

		bufferBuilder.vertex(x + 16, y, z);
		bufferBuilder.vertex(x, y, z);
	}
}
