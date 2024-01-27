package io.github.niterux.niterucks.mixin.bevofeatures;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.player.input.GameInput;
import net.minecraft.client.player.input.PlayerInput;
import net.minecraft.entity.living.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.*;

@Mixin(GameInput.class)
public class GameInputMixin extends PlayerInput {
	@Shadow
	private GameOptions options;

	@Inject(method = "m_7792523(IZ)V", at = @At(value = "TAIL"))
	private void addNewInputs(int bl, boolean par2, CallbackInfo ci) {
		byte InputNum = -1;
		if (bl == Niterucks.CONFIG.flyButton.get().keyCode) {
			InputNum = 0;
		}
		if (bl == this.options.jumpKey.keyCode) {
			InputNum = 1;
		}
		if (bl == this.options.sneakKey.keyCode) {
			InputNum = 2;
		}
		if (bl == Niterucks.CONFIG.adjustButton.get().keyCode) {
			InputNum = 3;
		}
		if (InputNum > -1) {
			flyingControls[InputNum] = par2;
		}
	}

	@Inject(method = "m_6793679()V", at = @At("TAIL"))
	private void addResetInputs(CallbackInfo ci) {
		for (int i = 0; i < 3; ++i) {
			flyingControls[i] = false;
		}
	}

	@Inject(method = "tick(Lnet/minecraft/entity/living/player/PlayerEntity;)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/player/input/GameInput;sneaking:Z", shift = At.Shift.BEFORE))
	private void sneakJumpOverride(PlayerEntity par1, CallbackInfo ci) {
		if (flying) {
			this.jumping = false;
			this.sneaking = false;
		}
	}

	@Inject(method = "tick(Lnet/minecraft/entity/living/player/PlayerEntity;)V", at = @At("TAIL"))
	private void speedOverride(PlayerEntity par1, CallbackInfo ci) {
		if (flying) {
			par1.updateVelocity(this.movementSideways, this.movementForward, (float) flySpeed / 20);
		}
	}


}
