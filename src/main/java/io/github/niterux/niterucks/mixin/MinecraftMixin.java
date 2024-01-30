package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.GsonWarningScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

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

	@WrapOperation(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
	private void displayGsonWarning(Minecraft instance, Screen screen, Operation<Void> original) {
		if (Objects.equals(Niterucks.gsonVersion, "2.2.2")) {
			original.call(instance, new GsonWarningScreen());
		} else {
			original.call(instance, screen);
		}
	}

	//this will only do anything once issue #888 in fabric-loader is solved
	//You might also notice that this is strangely a redirect and not an inject, for some reason injects kept failing, no idea why
	@WrapOperation(method = "m_6868991(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/awt/Frame;setLayout(Ljava/awt/LayoutManager;)V"))
	private static void addNiterucksIcon(Frame instance, LayoutManager layoutManager, Operation<Void> original) throws IOException {
		original.call(instance, new BorderLayout());
		instance.setIconImage(ImageIO.read(Objects.requireNonNull(Niterucks.class.getResource("/assets/niterucks/icon.png"))));
	}
}
