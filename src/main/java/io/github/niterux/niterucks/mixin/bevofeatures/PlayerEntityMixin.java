package io.github.niterux.niterucks.mixin.bevofeatures;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.entity.living.player.LocalPlayerEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(World world) {
		super(world);
	}

	@Redirect(method = "tickAi()V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
		target = "Lnet/minecraft/entity/living/player/PlayerEntity;cameraPitch:F"))
	private void fixPitch(PlayerEntity instance, float value) {
		this.cameraPitch = (flying && instance instanceof LocalPlayerEntity) ? 0F : value;
	}

	@ModifyExpressionValue(method = "getMiningSpeed(Lnet/minecraft/block/Block;)F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/player/PlayerEntity;onGround:Z"))
	private boolean fixFlyMiningSpeed(boolean original) {
		return flying || original;
	}
}
