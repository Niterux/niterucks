package com.niterucks.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

	@Inject(method = "render", at = @At("TAIL"))
	public void niterucks$onInit(CallbackInfo ci) {
		this.drawString(this.minecraft.textRenderer, "crrepper aww sman hahahahaha am i rirght ellow minecrafers????:)))))))", 2, 12, 0xff0000);
	}
}
