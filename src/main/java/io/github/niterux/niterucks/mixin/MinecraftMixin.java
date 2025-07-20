package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.world.WorldRenderer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.chunkBordersEnabled;
import static io.github.niterux.niterucks.niterucksfeatures.MiscUtils.printDebugKeys;

@Mixin(value = Minecraft.class, priority = 1005)
public class MinecraftMixin {
	@Shadow
	public GameGui gui;
	@Shadow
	public WorldRenderer worldRenderer;
	@Shadow
	public TextureManager textureManager;

	//this will only do anything once issue #888 in fabric-loader is solved
	//You might also notice that this is strangely a redirect and not an inject, for some reason injects kept failing, no idea why
	@WrapOperation(method = "m_6868991(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/awt/Frame;setLayout(Ljava/awt/LayoutManager;)V"), require = 0)
	private static void addNiterucksIcon(Frame instance, LayoutManager layoutManager, Operation<Void> original) throws IOException {
		original.call(instance, new BorderLayout());
		instance.setIconImage(ImageIO.read(Objects.requireNonNull(Niterucks.class.getResource("/assets/niterucks/icon.png"))));
	}

	@WrapOperation(method = "init()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create()V", remap = false), require = 0)
	private void amdFix(Operation<Void> original) throws LWJGLException {
		PixelFormat pixelformat = new PixelFormat();
		pixelformat = pixelformat.withDepthBits(24);
		Display.create(pixelformat);
	}

	@ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE",
		target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false))
	private boolean noneKeyFix(boolean original) {
		if (Keyboard.getEventKey() <= 0) {
			return false;
		}
		return original;
	}

	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, remap = false), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleKeyboard()V", ordinal = 0)))
	private void addNewKeybinds(CallbackInfo ci) {
		if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_D:
					gui.clearChat();
					break;
				case Keyboard.KEY_Q:
					printDebugKeys(gui);
					break;
				case Keyboard.KEY_A:
					worldRenderer.m_6748042();
					break;
				case Keyboard.KEY_G:
					chunkBordersEnabled = !chunkBordersEnabled;
					break;
			}
		}

	}

	@ModifyVariable(method = "m_1075084(I)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private int swapMouseButtons(int button) {
		if (Niterucks.CONFIG.swapMouseButtons.get() && (button == 0 || button == 1)) {
			return (button == 0) ? 1 : 0;
		} else return button;
	}

	@ModifyArg(method = "tick()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z", remap = false), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;m_1075084(I)V", ordinal = 3)), index = 0)
	private int swapMiningMouseButton(int button) {
		return (Niterucks.CONFIG.swapMouseButtons.get()) ? 1 : button;
	}

	@Inject(method = "forceReload()V", at = @At("TAIL"))
	private void addTexturesReloading(CallbackInfo ci) {
		textureManager.reload();
	}
}
