package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.render.entity.WolfRenderer;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.niterux.niterucks.mixin.invokers.RenderNameTagInvoker;

import java.util.Objects;

@Mixin(WolfRenderer.class)
public class WolfRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/living/mob/passive/animal/tamable/WolfEntity;DDDFF)V", at = @At("TAIL"))
	private void renderOwnerName(CallbackInfo ci,@Local(ordinal = 0, argsOnly = true) WolfEntity wolfEntity,  @Local(ordinal = 0, argsOnly = true) double x, @Local(ordinal = 1, argsOnly = true) double y, @Local(ordinal = 2, argsOnly = true) double z){
		if(MinecraftInstanceAccessor.getMinecraft().isMultiplayer() && !Objects.equals(wolfEntity.m_9131874(), "")) {
			((RenderNameTagInvoker) this).renderNameTagCustom(wolfEntity, wolfEntity.m_9131874() + "'s Wolf", x, y - 1, z, 64);
		}
	}
}
