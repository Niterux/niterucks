package io.github.niterux.niterucks.mixin.optimization.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.screenshots.FastScreenshotUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "tryTakeScreenshot()V", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ScreenshotUtils;saveScreenshot(Ljava/io/File;II)Ljava/lang/String;"))
	private void synchronizeAndThreadScreenshots(CallbackInfo ci) {
		if (!Niterucks.CONFIG.enableScreenshotEnhancements.get())
			return;
		FastScreenshotUtils.takeScreenshot();
		ci.cancel();
	}
}
