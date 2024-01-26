package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Dimension.class)
public class DimensionMixin {
	@ModifyConstant(method = "initBrightnessTable()V", constant = @Constant(floatValue = 0.05F, ordinal = 0))
	private float boostGamma(float constant) {
		return constant * (Niterucks.brightness.get() * 2 + 1.0F);
	}

	@ModifyConstant(method = "getCloudHeight()F", constant = @Constant(floatValue = 108F))
	private float modifyCloudHeight(float original) {
		return Niterucks.cloudHeight.get();
	}
}
