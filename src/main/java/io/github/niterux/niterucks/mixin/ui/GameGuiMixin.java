package io.github.niterux.niterucks.mixin.ui;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.ItemCoords;
import io.github.niterux.niterucks.niterucksfeatures.SimpleChatLoggingFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {
	@Unique
	private static Logger chatLogger = Logger.getLogger("io.github.niterux.niterucks.mixin.ui.GameGuiMixin");
	@Shadow
	private static ItemRenderer ITEM_RENDERER;

	static {
		chatLogger.setUseParentHandlers(false);
		ConsoleHandler simpleChatLoggingHandler = new ConsoleHandler();
		simpleChatLoggingHandler.setFormatter(new SimpleChatLoggingFormatter());
		chatLogger.addHandler(simpleChatLoggingHandler);
	}

	@Unique
	int hotbarHeight;
	@Shadow
	private Minecraft minecraft;

	//hotbar screen safety
	@Inject(method = "render", at = @At("HEAD"))
	private void getHotbarHeight(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		hotbarHeight = Niterucks.CONFIG.hotbarScreenSafety.get();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;m_1513977()I", ordinal = 0))
	private void moveHealthMatrixUp(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		GL11.glTranslated(0.0, -hotbarHeight, 0.0);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", remap = false, ordinal = 1))
	private void moveHealthMatrixBackDown(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		GL11.glTranslated(0.0, hotbarHeight, 0.0);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V", remap = false, ordinal = 0))
	private void moveHotbarMatrixUp(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		GL11.glTranslated(0.0, -hotbarHeight, 0.0);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V", remap = false, ordinal = 1))
	private void moveHotbarMatrixBackDown(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		GL11.glTranslated(0.0, hotbarHeight, 0.0);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", remap = false, ordinal = 0, shift = At.Shift.AFTER))
	private void moveItemsMatrixUp(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		GL11.glTranslated(0.0, -hotbarHeight, -50.0); //50 to item rendering over chat
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;turnOff()V", ordinal = 0))
	private void moveItemsMatrixBackDown(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
		GL11.glTranslated(0.0, hotbarHeight, 50.0);
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GameGui;drawTexture(IIIIII)V", ordinal = 1), index = 5)
	private int fixSelectedSlotRendering(int height) {
		return 24;
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
			shift = At.Shift.AFTER,
			remap = false
		)
	)
	private void addDurability(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci, @Local(ordinal = 0) TextRenderer textRenderer, @Local(ordinal = 3) int height, @Local(ordinal = 2) int width) {
		ItemStack heldItem = minecraft.player.inventory.getMainHandStack();
		if (heldItem == null)
			return;
		if (heldItem.getMaxDamage() > 0) {
			String damageText = String.valueOf(heldItem.getMaxDamage() - heldItem.getDamage());
			this.drawCenteredString(textRenderer, damageText, width / 2 - ItemCoords.displayX + ItemCoords.textOffsetX, height - ItemCoords.displayY + ItemCoords.damageOffsetY, 0x55FF55);
		}
		if (heldItem.size > 1 || heldItem.getMaxSize() > 1) { //account for overstacked items
			this.drawCenteredString(textRenderer, String.valueOf(heldItem.size), width / 2 - ItemCoords.displayX + ItemCoords.textOffsetX, height - ItemCoords.displayY + ItemCoords.stackOffsetY, 0x55FF55);
		}
		ITEM_RENDERER.renderGuiItemWithEnchantmentGlint(this.minecraft.textRenderer, this.minecraft.textureManager, heldItem, width / 2 - ItemCoords.displayX + ItemCoords.itemIconOffsetX, height - ItemCoords.displayY + ItemCoords.itemIconOffsetY);
	}

	//print chat message to console
	@Inject(method = "addChatMessage(Ljava/lang/String;)V", at = @At("HEAD"))
	private void printChatMessage(String text, CallbackInfo ci) {
		chatLogger.info(text);
	}
}
