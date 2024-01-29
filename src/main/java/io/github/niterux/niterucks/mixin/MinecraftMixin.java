package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.GsonWarningScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	public GameOptions options;
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
	@WrapOperation(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
	private void displayGsonWarning(Minecraft instance, Screen screen, Operation<Void> original){
		if(Objects.equals(Niterucks.gsonVersion, "2.2.2")){
			original.call(instance, new GsonWarningScreen());
		} else {
			original.call(instance, screen);
		}
	}
}
