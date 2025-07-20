package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.logging.Logger;

@Mixin(targets = {"net/java/games/input/DefaultControllerEnvironment"})
public class FixUnknownWindowsVersionMixin {
	@WrapOperation(method = "getControllers()[Lnet/java/games/input/Controller;", remap = false, at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V", remap = false))
	private void suppressWindowsWarning(Logger instance, String msg, Operation<Void> original) {

	}

	@WrapOperation(method = "getControllers()[Lnet/java/games/input/Controller;", remap = false, at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", remap = false, ordinal = 1))
	private boolean useOSXPluginsOnMacOS(String instance, Object o, Operation<Boolean> original) {
		return instance.toLowerCase().contains("mac");
	}
}
