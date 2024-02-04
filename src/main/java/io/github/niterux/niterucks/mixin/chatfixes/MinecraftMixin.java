package io.github.niterux.niterucks.mixin.chatfixes;

import io.github.niterux.niterucks.mixin.accessors.lastChatMessageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	public boolean isMultiplayer() {
		return true;
	}

	@Shadow
	public void openScreen(Screen screen) {
	}

	@Inject(method = "tick()V", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/Minecraft;isMultiplayer()Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleKeyboard()V", ordinal = 0)))
	private void openSlashChat(CallbackInfo ci) {
		if (this.isMultiplayer() && Keyboard.getEventKey() == Keyboard.KEY_SLASH) {
			Screen chatWithSlash = new ChatScreen();
			this.openScreen(chatWithSlash);
			((lastChatMessageAccessor) chatWithSlash).setLastChatMessage("/");
		}
	}
}
