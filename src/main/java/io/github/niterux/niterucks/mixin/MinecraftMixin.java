package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Redirect(method = "init()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create()V", remap = false), require = 0)
	private void amdFix() throws LWJGLException {
		PixelFormat pixelformat = new PixelFormat();
		pixelformat = pixelformat.withDepthBits(24);
		Display.create(pixelformat);
	}

	@ModifyExpressionValue(method = "tick()V", at = @At(value = "INVOKE",
		target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false))
	private boolean noneKeyFix(boolean original) {
		if (Keyboard.getEventKey() == 0) {
			return false;
		}
		return original;
	}
}
