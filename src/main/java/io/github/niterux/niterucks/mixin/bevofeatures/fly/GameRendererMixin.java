package io.github.niterux.niterucks.mixin.bevofeatures.fly;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.flyingTouchedGround;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyExpressionValue(method = "setupCamera(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;viewBobbing:Z", opcode = Opcodes.GETFIELD))
	private boolean removeFlyBobbing(boolean original) {
		return flyingTouchedGround && original;
	}

	@ModifyExpressionValue(method = "renderItemInHand(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;viewBobbing:Z", opcode = Opcodes.GETFIELD))
	private boolean removeFlyBobbingHand(boolean original) {
		return flyingTouchedGround && original;
	}
}
