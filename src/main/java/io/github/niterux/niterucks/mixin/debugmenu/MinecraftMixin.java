package io.github.niterux.niterucks.mixin.debugmenu;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.niterucksfeatures.debugmenu.DebugHotKeyRegistry;
import io.github.niterux.niterucks.niterucksfeatures.debugmenu.KeyCombo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.render.texture.TextureManager;
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
	public GameGui gui;
	@Shadow
	public TextureManager textureManager;
	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, remap = false), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleKeyboard()V", ordinal = 0)))
	private void addNewKeybinds(CallbackInfo ci) {
		if (!Keyboard.isKeyDown(Keyboard.KEY_F3))
			return;
		for (KeyCombo keyCombo : DebugHotKeyRegistry.HotKeyRegistry) {
			if (keyCombo.getComboKey() != Keyboard.getEventKey())
				continue;
			keyCombo.onPress((Minecraft) (Object) this);
			String message = keyCombo.getMessage();
			if (message != null)
				gui.addChatMessage("§e[Debug]:§f " + message);
			return;
		}
	}

	@Inject(method = "forceReload()V", at = @At("TAIL"))
	private void addTexturesReloading(CallbackInfo ci) {
		textureManager.reload();
	}

	@WrapOperation(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;forceReload()V"))
	private void removeDebugHotKeyBuiltIn(Minecraft instance, Operation<Void> original) {
		// Remove builtin hotkey for reloading assets.
	}

}
