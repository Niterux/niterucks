package io.github.niterux.niterucks.mixin.borderlesswindow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@WrapOperation(method = "Lnet/minecraft/client/Minecraft;toggleFullscreen()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V"))
	private void disableFullscreen(boolean fullscreen, Operation<Void> original){

	}
}
