package io.github.niterux.niterucks.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TitleScreen.class, priority = 1500)
public class ModMenuFixMixin extends Screen {
	@TargetHandler(
		mixin = "com.terraformersmc.modmenu.mixin.MixinTitleScreen",
		name = "onInit"
	)
	@Redirect(
		method = "@MixinSquared:Handler",
		at = @At(
			value = "NEW",
			target = "com/terraformersmc/modmenu/gui/widget/ModMenuButtonWidget",
			ordinal = 0,
			remap = false
		)
	)
	private ModMenuButtonWidget reduceModMenuButtonSize(int int1, int int2, int in3, int in4, int int5, String string1, CallbackInfo info) {
		return new ModMenuButtonWidget(int1, int2, in3, 98, int5, string1);
	}
}
