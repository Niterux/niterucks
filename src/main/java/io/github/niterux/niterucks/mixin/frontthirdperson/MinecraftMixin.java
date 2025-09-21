package io.github.niterux.niterucks.mixin.frontthirdperson;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.frontThirdPersonCamera;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@ModifyExpressionValue(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z", opcode = Opcodes.GETFIELD))
	private boolean addFrontFacingCamera(boolean original) {
		if (!original)
			return false;
		frontThirdPersonCamera = !frontThirdPersonCamera;
		return !frontThirdPersonCamera;
	}

	@Inject(method = "run()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void resetFrontThirdPerson(CallbackInfo ci) {
		frontThirdPersonCamera = false;
	}
}
