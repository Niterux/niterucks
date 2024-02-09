package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.frontThirdPersonCamera;
import static io.github.niterux.niterucks.niterucksfeatures.KeyStateManager.niterucksControls;
import static io.github.niterux.niterucks.niterucksfeatures.RainbowManager.adjustRainbow;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Unique
	boolean handFov = false;

	@Shadow
	private float getFov(float f) {
		return f;
	}

	@Shadow
	private double zoom;
	@Shadow
	private double zoomX;
	@Shadow
	private double zoomY;
	@Shadow
	private Minecraft minecraft;
	@Shadow
	private float viewDistance;

	@ModifyArg(method = "transformCamera(F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", remap = false, ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugCamera:Z", ordinal = 2, opcode = Opcodes.GETFIELD)), index = 0)
	private float changeCameraRotationX(float angle) {
		return (frontThirdPersonCamera) ? -angle : angle;
	}

	@ModifyArg(method = "transformCamera(F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", remap = false, ordinal = 1), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugCamera:Z", ordinal = 2, opcode = Opcodes.GETFIELD)), index = 0)
	private float changeCameraRotationY(float angle) {
		return (frontThirdPersonCamera) ? angle + 180.0F : angle;
	}

	@ModifyVariable(method = "transformCamera(F)V", ordinal = 4, at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;pitch:F", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTrace(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/world/HitResult;", shift = At.Shift.AFTER)))
	private double invertCameraPosX(double X) {
		return (frontThirdPersonCamera) ? -X : X;
	}

	@ModifyVariable(method = "transformCamera(F)V", ordinal = 5, at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;pitch:F", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTrace(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/world/HitResult;", shift = At.Shift.AFTER)))
	private double invertCameraPosY(double Y) {
		return (frontThirdPersonCamera) ? -Y : Y;
	}

	@ModifyVariable(method = "transformCamera(F)V", ordinal = 6, at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;pitch:F", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTrace(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/world/HitResult;", shift = At.Shift.AFTER)))
	private double invertCameraPosZ(double Z) {
		return (frontThirdPersonCamera) ? -Z : Z;
	}

	@ModifyExpressionValue(method = "getFov(F)F", at = @At(value = "CONSTANT", args = "floatValue=70.0F", ordinal = 0))
	private float changeFov(float seventy, @Share("seventy") LocalFloatRef fovRef) {
		fovRef.set(seventy);
		return this.handFov ? Niterucks.CONFIG.viewmodelFov.get() : Niterucks.CONFIG.fov.get();
	}

	@ModifyExpressionValue(method = "getFov(F)F", at = @At(value = "CONSTANT", args = "floatValue=60.0F", ordinal = 0))
	private float changeWaterFov(float sixty, @Share("seventy") LocalFloatRef fovRef) {
		return this.handFov ? Niterucks.CONFIG.viewmodelFov.get() * sixty / fovRef.get() : Niterucks.CONFIG.fov.get() * sixty / fovRef.get();
	}

	@Inject(method = "setupCamera(FI)V", at = @At(value = "HEAD"))
	private void injectHandFovMode(CallbackInfo info) {
		this.handFov = false;
	}

	@Inject(method = "renderItemInHand(FI)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", remap = false))
	private void changePerspective(float tickDelta, int anaglyphFilter, CallbackInfo ci) {
		this.handFov = true;
		if (zoom != 1.0) {
			GL11.glTranslatef((float) zoomX, (float) (-zoomY), 0.0F);
			GL11.glScaled(zoom, zoom, 1.0);
			GLU.gluPerspective(getFov(tickDelta), (float) minecraft.width / (float) minecraft.height, 0.05F, viewDistance * 2.0F);
		} else {
			GLU.gluPerspective(getFov(tickDelta), (float) minecraft.width / (float) minecraft.height, 0.05F, viewDistance * 2.0F);
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}

	@Inject(method = "renderItemInHand(FI)V", at = @At("HEAD"))
	private void injectGlMode(CallbackInfo info) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
	}

	@ModifyExpressionValue(method = "render(F)V", at = @At(value = "CONSTANT", args = "floatValue=0.6F", ordinal = 0))
	private float addZoomFunctionality(float constant) {
		zoom = niterucksControls[0] ? Math.pow((double) GameFeaturesStates.zoomAmount / 4, 2) : 1.0;
		return niterucksControls[0] ? (float) (constant / Math.pow((double) GameFeaturesStates.zoomAmount / 4, 2)) : constant;
	}

	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(FJ)V", ordinal = 1)))
	private int fixPerformanceLimiter(int fpsLimit) {
		// Notch meant to write this.minecraft.options.fpsLimit > 0 instead they wrote
		// this.minecraft.options.fpsLimit == 2 which means the fps limit never applies on balanced
		return (fpsLimit > 0) ? 2 : -1;
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void updateRainbow(CallbackInfo ci) {
		adjustRainbow();
	}

	@Inject(method = "renderWorld(FJ)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;zoom:D", ordinal = 0))
	private void renderChunkBounds(float tickDelta, long renderTimeLimit, CallbackInfo ci, @Local(ordinal = 0) LivingEntity camera) {
		if (GameFeaturesStates.chunkBordersEnabled) {
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
			BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
			//render the 4 chunk corners
			GL11.glColor3f(0.0F, 1.0F, 0.0F);
			bufferBuilder.start(GL11.GL_LINES);
			bufferBuilder.vertex(chunkCornerX - renderX, 0 - renderY, chunkCornerZ - renderZ);
			bufferBuilder.vertex(chunkCornerX - renderX, 128 - renderY, chunkCornerZ - renderZ);
			bufferBuilder.vertex(chunkCornerX - renderX + 16, 128 - renderY, chunkCornerZ - renderZ);
			bufferBuilder.vertex(chunkCornerX - renderX + 16, 0 - renderY, chunkCornerZ - renderZ);
			bufferBuilder.vertex(chunkCornerX - renderX, 0 - renderY, chunkCornerZ - renderZ + 16);
			bufferBuilder.vertex(chunkCornerX - renderX, 128 - renderY, chunkCornerZ - renderZ + 16);
			bufferBuilder.vertex(chunkCornerX - renderX + 16, 128 - renderY, chunkCornerZ - renderZ + 16);
			bufferBuilder.vertex(chunkCornerX - renderX + 16, 0 - renderY, chunkCornerZ - renderZ + 16);
			bufferBuilder.end();
			MiscUtils.renderChunkSquare(chunkCornerX - renderX, 128 - renderY, chunkCornerZ - renderZ, bufferBuilder);
			MiscUtils.renderChunkSquare(chunkCornerX - renderX, 0 - renderY, chunkCornerZ - renderZ, bufferBuilder);
			//subchunk loop
			GL11.glColor3f(0.0F, 1.0F, 1.0F);
			for (int ypos = 16; ypos <= 112; ypos += 16) {
				MiscUtils.renderChunkSquare(chunkCornerX - renderX, ypos - renderY, chunkCornerZ - renderZ, bufferBuilder);
			}
			//render feet pos and height pos
			GL11.glColor3f(1.0F, 1.0F, 0.0F);
			MiscUtils.renderChunkSquare(chunkCornerX - renderX, feetRenderPos, chunkCornerZ - renderZ, bufferBuilder);
			MiscUtils.renderChunkSquare(chunkCornerX - renderX, feetRenderPos + camera.height, chunkCornerZ - renderZ, bufferBuilder);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_FOG);
			//GL11.glDepthMask(true);
		}
	}
}
