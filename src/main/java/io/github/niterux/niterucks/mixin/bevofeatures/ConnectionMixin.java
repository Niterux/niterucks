package io.github.niterux.niterucks.mixin.bevofeatures;

import io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
	@Inject(method = "disconnect(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At("HEAD"))
	private void revokeFly(String args, Object[] par2, CallbackInfo ci){
		BetaEVOFlyHelper.resetFly();
	}
}
