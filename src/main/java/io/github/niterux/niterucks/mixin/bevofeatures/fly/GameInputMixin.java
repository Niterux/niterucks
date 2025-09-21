package io.github.niterux.niterucks.mixin.bevofeatures.fly;

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
	private void addNewInputs(int keyCode, boolean pushed, CallbackInfo ci) {
		byte inputNum = -1;
		if (keyCode == Niterucks.CONFIG.flyButton.get().keyCode)
			inputNum = 0;
		if (keyCode == this.options.jumpKey.keyCode)
			inputNum = 1;
		if (keyCode == this.options.sneakKey.keyCode)
			inputNum = 2;
		if (keyCode == Niterucks.CONFIG.adjustButton.get().keyCode)
			inputNum = 3;
		if (inputNum > -1)
			flyingControls[inputNum] = pushed;
	}

	@Inject(method = "m_6793679()V", at = @At("TAIL"))
	private void addResetInputs(CallbackInfo ci) {
		for (int i = 0; i < 3; ++i)
			flyingControls[i] = false;
	}

	@Inject(method = "tick(Lnet/minecraft/entity/living/player/PlayerEntity;)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/player/input/GameInput;sneaking:Z"))
	private void sneakJumpOverride(PlayerEntity playerEntity, CallbackInfo ci) {
		if (flying) {
			this.jumping = false;
			this.sneaking = false;
		}
	}

	@Inject(method = "tick(Lnet/minecraft/entity/living/player/PlayerEntity;)V", at = @At("TAIL"))
	private void speedOverride(PlayerEntity playerEntity, CallbackInfo ci) {
		if (flying)
			playerEntity.updateVelocity(this.movementSideways, this.movementForward, (float) flySpeed / 20);
	}
}
