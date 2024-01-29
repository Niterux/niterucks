package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Calendar;

import static io.github.niterux.niterucks.Niterucks.modVersion;

@Mixin(value = TitleScreen.class, priority = 2000)
public class TitleScreenMixin extends Screen {
	@Shadow
	private String splashText;

	@Inject(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/locale/LanguageManager;getInstance()Lnet/minecraft/locale/LanguageManager;"))
	private void splashInject(CallbackInfo info, @Local(ordinal = 0) Calendar var1) {
		if (var1.get(Calendar.MONTH) == Calendar.NOVEMBER) {
			splashText = "Trans Rights!";
		}
	}

	@Inject(method = "render(IIF)V", at = @At("TAIL"))
	private void vdf(CallbackInfo ci) {
		this.drawString(this.textRenderer, "Niterucks Client " + modVersion, 2, this.height - 10, 0x5336A2);
	}
}
