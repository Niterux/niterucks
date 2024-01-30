package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TexturePackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
	@SuppressWarnings("unchecked")
	@Inject(method = "init", at = @At("TAIL"))
	private void addTextureButton(CallbackInfo ci) {
		switch (ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue()) {
			case BELOW_ACHIEVEMENTS:
			case BELOW_ACHIEVEMENTS_AND_STATISTICS:
				this.buttons.add(new ButtonWidget(700009, this.width / 2 + 2, this.height / 4 + 56, 98, 20, "Texture Packs"));
				break;
			case BELOW_STATISTICS:
				this.buttons.add(new ButtonWidget(700009, this.width / 2 - 100, this.height / 4 + 56, 98, 20, "Texture Packs"));
				break;
			default:
				this.buttons.add(new ButtonWidget(700009, this.width / 2 - 100, this.height / 4 + 56, "Texture Packs"));
		}
		this.buttons.add(new ButtonWidget(900009, this.width / 2 - 49, this.height / 4 + 128, 98, 20, "Screenshots"));
	}

	@Inject(method = "buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("TAIL"))
	private void addTextureButtonHandler(ButtonWidget par1, CallbackInfo ci) {
		if (par1.id == 700009) {
			this.minecraft.openScreen(new TexturePackScreen(this));
		}
		if (par1.id == 900009) {
			Sys.openURL("file://" + new File(String.valueOf(FabricLoader.getInstance().getGameDir()), "screenshots").getAbsolutePath());
		}
	}

	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "CONSTANT", args = "intValue=20", ordinal = 0))
	private int fixGhostSaving(int original) {
		return this.minecraft.isMultiplayer() ? -1 : original;
	}
}
