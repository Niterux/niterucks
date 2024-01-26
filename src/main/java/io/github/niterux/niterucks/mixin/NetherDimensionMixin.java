package io.github.niterux.niterucks.mixin;


import io.github.niterux.niterucks.Niterucks;
import net.minecraft.world.dimension.NetherDimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetherDimension.class)
public class NetherDimensionMixin {
	@ModifyConstant(method = "initBrightnessTable()V", constant = @Constant(floatValue = 0.1F, ordinal = 0))
	private float boostGamma(float constant) {
		return constant * (Niterucks.brightness.get() * 2 + 1.0F);
	}
}
