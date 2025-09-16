package io.github.niterux.niterucks.mixin.optimization;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.MemoryTracker;
import io.github.niterux.niterucks.mixin.accessors.BlockEntityRendererDispatcherAccessor;
import io.github.niterux.niterucks.mixin.accessors.FrustumAccessor;
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

// This at worst doubles fps in super sign dense areas, at best it can triple or quadruple
@Mixin(SignRenderer.class)
public abstract class SignRendererMixin extends BlockEntityRenderer<SignBlockEntity> {
	@Unique
	private static final FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
	@Unique
	private static boolean hasUpdate = false;

	@ModifyExpressionValue(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 3))
	private int limitViewDistanceAndOptimizeTextDrawLists(int original, SignBlockEntity currentBlockEntity, double x, double y, double z) {
		BlockEntityRenderDispatcher publicDispatcher = ((BlockEntityRendererDispatcherAccessor) this).getRenderDispatcher();
		if (currentBlockEntity.squaredDistanceTo(publicDispatcher.cameraX, publicDispatcher.cameraY, publicDispatcher.cameraZ) > 600.0) {
			return Integer.MAX_VALUE;
		}
		String[] oldLines = ((SignBlockEntityInterface) currentBlockEntity).niterucks$getLinesUpdateChecker();
		hasUpdate = !Arrays.equals(currentBlockEntity.lines, oldLines);
		if (hasUpdate) {
			System.out.println("update!");
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrix);
			GL11.glPopMatrix();
			((SignBlockEntityInterface) currentBlockEntity).niterucks$copyToUpdateChecker(currentBlockEntity.lines);
			int newGLCallList = MemoryTracker.getLists(1);
			((SignBlockEntityInterface) currentBlockEntity).niterucks$setGlCallList(newGLCallList);
			GL11.glNewList(newGLCallList, GL11.GL_COMPILE);
			return original;
		}
		GL11.glCallList(((SignBlockEntityInterface) currentBlockEntity).niterucks$getGlCallList());
		return Integer.MAX_VALUE;
	}

	@Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 1))
	private void renderAfterCompiling(SignBlockEntity currentBlockEntity, double d, double e, double f4, float g5, CallbackInfo ci) {
		if (!hasUpdate)
			return;
		GL11.glEndList();
		GL11.glPushMatrix();
		GL11.glLoadMatrix(modelviewMatrix);
		GL11.glCallList(((SignBlockEntityInterface) currentBlockEntity).niterucks$getGlCallList());
		hasUpdate = false;
	}

	@WrapMethod(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V")
	private void cullSign(SignBlockEntity signBlockEntity, double x, double y, double z, float g5, Operation<Void> original) {
		if (FrustumAccessor.getInstanceNoCompute().m_9750073(x, y, z, x + 1, y + 1, z + 1))
			original.call(signBlockEntity, x, y, z, g5);
	}

	@Unique
	private float signFacingCalculator(SignBlockEntity currentBlockEntity) {
		int metadata = currentBlockEntity.getBlockMetadata();
		if (currentBlockEntity.getBlock() == Block.STANDING_SIGN) {
			return 22.5f * metadata;
		}
		return switch (metadata) {
			case 3 -> 0f;
			case 4 -> 90f;
			case 2 -> 180f;
			default -> 270f;
		};
	}
}
