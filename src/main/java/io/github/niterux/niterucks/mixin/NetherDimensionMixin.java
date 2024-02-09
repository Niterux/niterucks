package io.github.niterux.niterucks.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.world.dimension.NetherDimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherDimension.class)
public class NetherDimensionMixin {
	@ModifyExpressionValue(method = "initBrightnessTable()V", at = @At(value = "CONSTANT", args = "floatValue=0.1F", ordinal = 0))
	private float boostGamma(float constant) {
		return constant * Niterucks.CONFIG.brightness.get();
	}
}
