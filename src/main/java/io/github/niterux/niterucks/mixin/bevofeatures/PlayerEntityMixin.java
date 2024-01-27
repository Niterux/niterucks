package io.github.niterux.niterucks.mixin.bevofeatures;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(World world) {
		super(world);
	}

	@ModifyExpressionValue(method = "getMiningSpeed(Lnet/minecraft/block/Block;)F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/player/PlayerEntity;onGround:Z"))
	private boolean fixFlyMiningSpeed(boolean original) {
		return flying || original;
	}
}
