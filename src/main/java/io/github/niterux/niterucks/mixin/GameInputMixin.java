package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.player.input.GameInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.KeyStateManager.niterucksControls;

@Mixin(GameInput.class)
public class GameInputMixin {
	@Inject(method = "m_7792523(IZ)V", at = @At(value = "TAIL"))
	private void addNewInputs(int bl, boolean par2, CallbackInfo ci) {
		byte InputNum = -1;
		if (bl == Niterucks.CONFIG.zoomButton.get().keyCode) {
			InputNum = 0;
		}
		if (InputNum > -1) {
			niterucksControls[InputNum] = par2;
		}
	}
	@Inject(method = "m_6793679()V", at = @At("TAIL"))
	private void addResetInputs(CallbackInfo ci) {
		for (int i = 0; i < 1; ++i) {
			niterucksControls[i] = false;
		}
	}
}
