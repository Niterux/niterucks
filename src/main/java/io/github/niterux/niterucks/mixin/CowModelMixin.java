package io.github.niterux.niterucks.mixin;


import net.minecraft.client.render.model.entity.CowModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

// I can't believe fixing the horns on the cow model is something someone actually wanted
@Mixin(CowModel.class)
public class CowModelMixin {
	@ModifyArgs(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;setPivot(FFF)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;setPivot(FFF)V", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;setPivot(FFF)V", ordinal = 2)))
	private void fixCowPivot(Args args) {
		args.set(1, 4.0F);
		args.set(2, -8.0F);
	}

	@ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;addBox(FFFIIIF)V", ordinal = 1), index = 0)
	private float fixCowHornPos1(float x) {
		return -5.0F;
	}

	@ModifyArg(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;addBox(FFFIIIF)V", ordinal = 2), index = 0)
	private float fixCowHornPos2(float x) {
		return 4.0F;
	}
}
