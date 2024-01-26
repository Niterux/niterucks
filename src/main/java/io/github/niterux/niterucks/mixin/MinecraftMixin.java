package io.github.niterux.niterucks.mixin;

import net.minecraft.client.Minecraft;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.niterux.niterucks.Niterucks.keyboardPressed;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Redirect(method = "init()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create()V", remap = false))
	private void test() throws LWJGLException {
		PixelFormat pixelformat = new PixelFormat();
		pixelformat = pixelformat.withDepthBits(24);
		Display.create(pixelformat);
	}

	@Redirect(method = "tick()V", at = @At(value = "INVOKE",
		target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false))
	public boolean noneKeyFix() {
		return keyboardPressed();
	}
}
