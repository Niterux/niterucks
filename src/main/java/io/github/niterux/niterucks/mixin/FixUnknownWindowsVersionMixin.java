package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.logging.Logger;

//This mixin won't work on Quilt because Quilt silently fails mixins into libraries
@Mixin(targets = {"net/java/games/input/DefaultControllerEnvironment"})
public class FixUnknownWindowsVersionMixin {
	@WrapOperation(method = "getControllers()[Lnet/java/games/input/Controller;", remap = false, at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;warning(Ljava/lang/String;)V", remap = false))
	private void suppressWindowsWarning(Logger instance, String msg, Operation<Void> original) {

	}

	@WrapOperation(method = "getControllers()[Lnet/java/games/input/Controller;", remap = false, at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", remap = false, ordinal = 1))
	private boolean fixMacOs(String instance, Object o, Operation<Boolean> original) {
		// ????, I realized this code only checks for OSX (probably because it's so old) so I feel it will help loading the plugins on modern macOS too instead of defaulting
		return instance.toLowerCase().contains("mac");
	}
}
