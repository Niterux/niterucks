package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flying;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Redirect(method = "Lnet/minecraft/client/render/GameRenderer;setupCamera(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;viewBobbing:Z", opcode = Opcodes.GETFIELD))
	private boolean removeFlyBobbing(GameOptions instance) {
		return !flying && instance.viewBobbing;
	}

	@Redirect(method = "Lnet/minecraft/client/render/GameRenderer;renderItemInHand(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;viewBobbing:Z", opcode = Opcodes.GETFIELD))
	private boolean removeFlyBobbingHand(GameOptions instance) {
		return !flying && instance.viewBobbing;
	}
}
