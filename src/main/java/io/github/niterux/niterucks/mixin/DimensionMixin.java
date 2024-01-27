package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Dimension.class)
public class DimensionMixin {
	@ModifyExpressionValue(method = "initBrightnessTable()V", at = @At(value = "CONSTANT", args = "floatValue=0.05F", ordinal = 0))
	private float boostGamma(float constant) {
		return constant * (Niterucks.CONFIG.BRIGHTNESS.get() * 2 + 1.0F);
	}

	@ModifyConstant(method = "getCloudHeight()F", constant = @Constant(floatValue = 108F))
	private float modifyCloudHeight(float original) {
		return Niterucks.CONFIG.CLOUD_HEIGHT.get();
	}
}
