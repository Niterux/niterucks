package io.github.niterux.niterucks.mixin.optimization;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.niterux.niterucks.mixin.accessors.BlockEntityRendererDispatcherAccessor;
import io.github.niterux.niterucks.mixin.accessors.FrustumAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// This at worst doubles fps in super sign dense areas, at best it can triple or quadruple
@Mixin(SignRenderer.class)
public abstract class SignRendererMixin extends BlockEntityRenderer<SignBlockEntity> {
	@ModifyExpressionValue(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 3))
	private int reduceSignViewDistance(int original, SignBlockEntity currentBlockEntity, double x, double y, double z) {
		BlockEntityRenderDispatcher publicDispatcher = ((BlockEntityRendererDispatcherAccessor) this).getRenderDispatcher();
		if (currentBlockEntity.squaredDistanceTo(publicDispatcher.cameraX, publicDispatcher.cameraY, publicDispatcher.cameraZ) < 600.0) {
			return original;
		}
		return Integer.MAX_VALUE;
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
