package io.github.niterux.niterucks.mixin.entity;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.client.render.model.entity.SkeletonModel;
import net.minecraft.client.render.model.entity.ZombieModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieModel.class)
public class ZombieModelMixin extends HumanoidModel {
	@Inject(method = "setAngles(FFFFFF)V", at = @At("TAIL"))
	private void fixSkeletonHands(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, CallbackInfo ci) {
		//noinspection ConstantValue
		if (Niterucks.CONFIG.newSkeleton.get() && (ZombieModel) (Object) this instanceof SkeletonModel) {
			this.rightArm.rotationZ = 0.0F;
			this.leftArm.rotationZ = 0.0F;
			this.rightArm.rotationY = this.head.rotationY - 0.1F;
			this.leftArm.rotationY = this.head.rotationY + 0.5F;
			this.rightArm.rotationX = -1.570796F + this.head.rotationX;
			this.leftArm.rotationX = -1.570796F + this.head.rotationX;
		}
	}
}
