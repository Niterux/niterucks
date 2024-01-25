package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.client.entity.living.player.LocalPlayerEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity {
	public PlayerEntityMixin(World world) {
		super(world);
	}

	@Redirect(method = "tickAi()V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
		target = "Lnet/minecraft/entity/living/player/PlayerEntity;cameraPitch:F"))
	private void fixPitch(PlayerEntity instance, float value){
		this.cameraPitch = value;
		if(flying && instance instanceof LocalPlayerEntity)
			this.cameraPitch = 0F;
	}
}
