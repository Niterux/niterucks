package io.github.niterux.niterucks.mixin.bevofeatures.playerlist;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.bevofeatures.BetaEVOPlayerListHelper;
import net.minecraft.client.player.input.GameInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameInput.class)
public class GameInputMixin {
	@Inject(method = "m_7792523(IZ)V", at = @At(value = "TAIL"))
	private void addNewInputs(int i, boolean bl, CallbackInfo ci) {
		if (i == Niterucks.CONFIG.playerListButton.get().keyCode) {
			BetaEVOPlayerListHelper.playerListKeyDown = bl;
		}
	}

	@Inject(method = "m_6793679()V", at = @At("TAIL"))
	private void addResetInputs(CallbackInfo ci) {
		BetaEVOPlayerListHelper.playerListKeyDown = false;
	}
}
