package io.github.niterux.niterucks.mixin.frontthirdperson;

import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.frontThirdPersonCamera;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyArg(method = "transformCamera(F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", remap = false, ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugCamera:Z", ordinal = 2, opcode = Opcodes.GETFIELD)), index = 0)
	private float changeCameraRotationX(float angle) {
		return MiscUtils.fixSpritePitch(angle);
	}

	@ModifyArg(method = "transformCamera(F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", remap = false, ordinal = 1), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugCamera:Z", ordinal = 2, opcode = Opcodes.GETFIELD)), index = 0)
	private float changeCameraRotationY(float angle) {
		return MiscUtils.fixSpriteYaw(angle);
	}

	@ModifyVariable(method = "transformCamera(F)V", ordinal = 4, at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;pitch:F", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTrace(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/world/HitResult;", shift = At.Shift.AFTER)))
	private double invertCameraPosX(double X) {
		return frontThirdPersonCamera ? -X : X;
	}

	@ModifyVariable(method = "transformCamera(F)V", ordinal = 5, at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;pitch:F", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTrace(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/world/HitResult;", shift = At.Shift.AFTER)))
	private double invertCameraPosY(double Y) {
		return frontThirdPersonCamera ? -Y : Y;
	}

	@ModifyVariable(method = "transformCamera(F)V", ordinal = 6, at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0, shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;pitch:F", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTrace(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/world/HitResult;", shift = At.Shift.AFTER)))
	private double invertCameraPosZ(double Z) {
		return frontThirdPersonCamera ? -Z : Z;
	}
}
