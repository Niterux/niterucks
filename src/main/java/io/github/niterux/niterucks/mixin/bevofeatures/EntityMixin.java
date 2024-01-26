package io.github.niterux.niterucks.mixin.bevofeatures;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Mixin(value = Entity.class, priority = 1500)
public class EntityMixin {
	@ModifyExpressionValue(method = "move(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)I", ordinal = 0), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)I", ordinal = 1)))
	private int noGroundUnderPlayerIfFlying(int original) {
		return (flying && ((Entity) (Object) this) instanceof InputPlayerEntity) ? 0 : original; // false warning
	}
}
