package io.github.niterux.niterucks.mixin.debugmenu.hitbox;

import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
	@Shadow
	public static double offsetX;
	@Shadow
	public static double offsetY;
	@Shadow
	public static double offsetZ;

	@Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;DDDFF)V"))
	private void renderHitboxes(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta, CallbackInfo ci) {
		if (!GameFeaturesStates.hitboxEnabled)
			return;
		double x = -offsetX;
		double y = -offsetY;
		double z = -offsetZ;
		float pitch = entity.pitch;
		if (Niterucks.CONFIG.interpolateHitboxes.get()) {
			x = dx - entity.x;
			y = dy - entity.y;
			z = dz - entity.z;
			pitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta;
		} else
			yaw = entity.yaw;

		GL11.glPushAttrib(GL11.GL_LINE_BIT | GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(MinecraftInstanceAccessor.getMinecraft().height / 480.0F);
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		bufferBuilder.start(GL11.GL_LINES);

		Box collisionBox = entity.shape.move(x, y, z);

		// Shows the bounds of the entity<=>world collision
		drawBox(collisionBox, bufferBuilder, 255, 255, 255);

		double eyeHeight = entity.getEyeHeight();
		double lookingOriginX = x + entity.x;
		double lookingOriginY = y + entity.y + eyeHeight;
		double lookingOriginZ = z + entity.z;

		// Shows a square where the eye height of the entity is
		bufferBuilder.color(255, 255, 255);
		drawHorizontalSquare(collisionBox.minX, collisionBox.minZ, collisionBox.maxX, collisionBox.maxZ, lookingOriginY, bufferBuilder);

		double lookingDistance = entity.shape.getAverageSideLength() * 1.5d;
		float yawThing = (float) (Math.toRadians(-yaw) - Math.PI);
		float pitchThing = (float) (Math.toRadians(-pitch));
		double forwardBack = MathHelper.cos(yawThing);
		double leftRight = MathHelper.sin(yawThing);
		double shortener = -MathHelper.cos(pitchThing) * lookingDistance;
		double vertical = MathHelper.sin(pitchThing) * lookingDistance + lookingOriginY;

		// Shows a line where the entity is looking
		bufferBuilder.color(0, 255, 0, 255);
		bufferBuilder.vertex(lookingOriginX, lookingOriginY, lookingOriginZ);
		bufferBuilder.vertex(leftRight * shortener + lookingOriginX, vertical, forwardBack * shortener + lookingOriginZ);

		if (entity.hasCollision()) {
			double expansion = entity.getExtraHitboxSize();
			Box hitbox = collisionBox.expand(expansion, expansion, expansion);

			//Shows the box that intercepts clicking
			drawBox(hitbox, bufferBuilder, 0, 0, 255);

			if (GameFeaturesStates.selfIsHoldingBow) {
				Box arrowHitbox = collisionBox.expand(0.3d, 0.3d, 0.3d);

				// Shows the box that the arrow's raycast must hit to collide
				drawBox(arrowHitbox, bufferBuilder, 255, 0, 255);
			}
		}
		bufferBuilder.end();
		GL11.glPopAttrib();
	}

	@Unique
	private void drawBox(Box box, BufferBuilder bufferBuilder, int r, int g, int b) {
		bufferBuilder.color(r, g, b, 255);
		drawHorizontalSquare(box.minX, box.minZ, box.maxX, box.maxZ, box.minY, bufferBuilder);
		drawHorizontalSquare(box.minX, box.minZ, box.maxX, box.maxZ, box.maxY, bufferBuilder);

		bufferBuilder.vertex(box.minX, box.minY, box.minZ);
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ);

		bufferBuilder.vertex(box.maxX, box.minY, box.minZ);
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ);

		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ);
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ);

		bufferBuilder.vertex(box.minX, box.minY, box.maxZ);
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ);
	}

	@Unique
	private void drawHorizontalSquare(double minX, double minZ, double maxX, double maxZ, double y, BufferBuilder bufferBuilder) {
		bufferBuilder.vertex(minX, y, minZ);
		bufferBuilder.vertex(minX, y, maxZ);

		bufferBuilder.vertex(minX, y, maxZ);
		bufferBuilder.vertex(maxX, y, maxZ);

		bufferBuilder.vertex(maxX, y, maxZ);
		bufferBuilder.vertex(maxX, y, minZ);

		bufferBuilder.vertex(maxX, y, minZ);
		bufferBuilder.vertex(minX, y, minZ);
	}
}
