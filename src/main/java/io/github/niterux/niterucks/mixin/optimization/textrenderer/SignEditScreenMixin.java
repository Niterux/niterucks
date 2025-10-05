package io.github.niterux.niterucks.mixin.optimization.textrenderer;

import net.minecraft.client.gui.screen.inventory.menu.SignEditScreen;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class SignEditScreenMixin {
	@Inject(method = "render(IIF)V", at = @At("HEAD"))
	private void signModelColorFix(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
		// Needed to avoid unexpected glcolor from gui title text not adjusting glcolor
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
	}
}
