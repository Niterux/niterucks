package io.github.niterux.niterucks.mixin.bevofeatures;

import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.client.entity.living.player.LocalPlayerEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.*;

@Mixin(LocalPlayerEntity.class)
public class LocalPlayerEntityMixin extends InputPlayerEntity {
	private LocalPlayerEntityMixin(Minecraft minecraft, World world, Session session, int dimensionId) {
		super(minecraft, world, session, dimensionId);
	}

	@Inject(method = "updateMovement()V", at = @At("TAIL"))
	private void flyMovement(CallbackInfo ci) {
		if (!flyAllowed) {
			flying = false;
		}
		if (flying) {
			flyingTouchedGround = false;
			velocityY = 0;
		} else if (this.onGround) {
			flyingTouchedGround = true;
		}
		if (MinecraftInstanceAccessor.getMinecraft().screen == null) {
			if (flyingControls[0]) {
				if (flyAllowed & !flyingButtonHeld) flying = !flying;
				flyingButtonHeld = true;
			} else {
				flyingButtonHeld = false;
			}
			if (flying) {
				if (flyingControls[1]) {
					velocityY = (flySpeed + 1) * 0.15625;
				}
				if (flyingControls[2]) {
					velocityY = (flySpeed + 1) * -0.15625;
				}
			}
		}


	}

	@Redirect(method = "updateMovement()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/living/player/LocalPlayerEntity;onGround:Z", opcode = Opcodes.GETFIELD), slice = @Slice(to = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/packet/PlayerMovePacket;<init>(Z)V")))
	private boolean onGroundOverride(LocalPlayerEntity instance) {
		return !flyingTouchedGround || instance.onGround;
	}
}
