package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Calendar;

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
		this.drawString(this.textRenderer, "Niterucks Client", 2, this.height - 10, 5453474);
	}

	@Redirect(method = "init()V", at =
	@At(value = "NEW", target = "net/minecraft/client/gui/widget/ButtonWidget", ordinal = 2))
	private ButtonWidget makeTexturePacksButtonSmaller(int int1, int int2, int int3, String string1) {
		if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.TitleMenuButtonStyle.CLASSIC) {
			return new ButtonWidget(int1, int2 + 102, int3, 98, 20, "Texture Packs");
		}
		return new ButtonWidget(int1, int2, int3, string1);
	}
}
