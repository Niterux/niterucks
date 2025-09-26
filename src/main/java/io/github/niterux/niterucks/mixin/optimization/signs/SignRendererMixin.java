package io.github.niterux.niterucks.mixin.optimization.signs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.platform.MemoryTracker;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.BlockEntityRendererDispatcherAccessor;
import io.github.niterux.niterucks.mixin.accessors.FrustumAccessor;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.SignBlockEntityInterface;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;
import java.util.Arrays;

// Adds frustum culling, distance culling, and puts the text into a cached drawlist
@Mixin(SignRenderer.class)
public abstract class SignRendererMixin extends BlockEntityRenderer<SignBlockEntity> {
	@Unique
	private static final FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
	@Unique
	private static boolean hasUpdate = false;
	@Unique
	private static boolean skipNextRender = true;

	@Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 0, shift = At.Shift.AFTER, remap = false))
	private void prepareTextRendering(SignBlockEntity currentBlockEntity, double x, double y, double z, float tickDelta, CallbackInfo ci) {
		skipNextRender = true;
		hasUpdate = false;
		BlockEntityRenderDispatcher publicDispatcher = ((BlockEntityRendererDispatcherAccessor) this).getRenderDispatcher();
		if (currentBlockEntity.squaredDistanceTo(publicDispatcher.cameraX, publicDispatcher.cameraY, publicDispatcher.cameraZ) > 600d)
			return;
		if (!MinecraftInstanceAccessor.getMinecraft().worldRenderer.globalBlockEntities.contains(currentBlockEntity)) {
			skipNextRender = false;
			return;
		}
		SignBlockEntityInterface signInterface = (SignBlockEntityInterface) currentBlockEntity;
		String[] oldLines = signInterface.niterucks$getLinesUpdateChecker();
		hasUpdate = !Arrays.equals(currentBlockEntity.lines, oldLines);
		if (!hasUpdate) {
			GL11.glCallList(signInterface.niterucks$getGlCallList());
			return;
		}
		skipNextRender = false;
		int newGLCallList = signInterface.niterucks$getGlCallList();
		if (newGLCallList == -1) {
			newGLCallList = MemoryTracker.getLists(1);
			signInterface.niterucks$setGlCallList(newGLCallList);
		}
		signInterface.niterucks$copyToUpdateChecker(currentBlockEntity.lines);
		Niterucks.SIGN_DRAWLIST_OBJECT_CACHE_LIST.add(currentBlockEntity);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);
		GL11.glPopMatrix();
		GL11.glNewList(newGLCallList, GL11.GL_COMPILE);
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 3))
	private int limitViewDistanceAndOptimizeTextDrawLists(int original, SignBlockEntity currentBlockEntity, double x, double y, double z) {
		if (skipNextRender)
			return Integer.MAX_VALUE;
		return original;
	}

	@Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 1, remap = false))
	private void renderAfterCompiling(SignBlockEntity currentBlockEntity, double x, double y, double z, float tickDelta, CallbackInfo ci) {
		if (!hasUpdate)
			return;
		GL11.glEndList();
		GL11.glPushMatrix();
		GL11.glLoadMatrix(modelviewMatrix);
		GL11.glCallList(((SignBlockEntityInterface) currentBlockEntity).niterucks$getGlCallList());
		hasUpdate = false;
	}

	@Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At("HEAD"), cancellable = true)
	private void cullSign(SignBlockEntity currentBlockEntity, double x, double y, double z, float tickDelta, CallbackInfo ci) {
		((SignBlockEntityInterface) currentBlockEntity).niterucks$setRenderCheck(true);
		if (!FrustumAccessor.getInstanceNoCompute().m_9750073(x, y, z, x + 1, y + 1, z + 1))
			ci.cancel();
	}

	@Unique
	private float signFacingCalculator(SignBlockEntity currentBlockEntity) {
		int metadata = currentBlockEntity.getBlockMetadata();
		if (currentBlockEntity.getBlock() == Block.STANDING_SIGN)
			return 22.5f * metadata;
		return switch (metadata) {
			case 3 -> 0f;
			case 4 -> 90f;
			case 2 -> 180f;
			default -> 270f;
		};
	}
}
