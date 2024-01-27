package io.github.niterux.niterucks.mixin.bevofeatures;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flySpeed;
import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {
	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			ordinal = 3,
			target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
			remap = false
		)
	)
	private void addFlyText(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci, @Local(ordinal = 0) TextRenderer var8, @Local(ordinal = 3) int height) {
		if (flying) {
			this.drawString(var8, "Flying", 2, height - 25, 0x55FF55);
			this.drawString(var8, "Fly Speed: " + flySpeed, 2, height - 15, 0x55FF55);
		}
	}

}
