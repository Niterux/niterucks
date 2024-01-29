package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.mixin.accessors.BlockEntityRendererDispatcherAccessor;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//this singular mixin quadruples my fps at retromc spawn lmaooooooo
@Mixin(SignRenderer.class)
public abstract class SignRendererMixin extends BlockEntityRenderer {
	@ModifyExpressionValue(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDF)V", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 3))
	private int reduceSignViewDistance(int original, @Local(ordinal = 0, argsOnly = true) SignBlockEntity currentBlockEntity){
		BlockEntityRenderDispatcher publicDispatcher = ((BlockEntityRendererDispatcherAccessor) this).getRenderDispatcher();
		if (currentBlockEntity.squaredDistanceTo(publicDispatcher.cameraX, publicDispatcher.cameraY, publicDispatcher.cameraZ) < 600.0) {
			return original;
		}
		return 200;
	}
}
