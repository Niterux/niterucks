package io.github.niterux.niterucks.mixin.entity;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.invokers.RenderNameTagInvoker;
import net.minecraft.client.render.entity.WolfRenderer;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(WolfRenderer.class)
public class WolfRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/living/mob/passive/animal/tamable/WolfEntity;DDDFF)V", at = @At("TAIL"))
	private void renderOwnerName(WolfEntity wolfEntity, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
		if (wolfEntity.world.isMultiplayer && Niterucks.CONFIG.wolfNameTags.get() && !Objects.equals(wolfEntity.m_9131874(), "")) {
			((RenderNameTagInvoker) this).renderNameTagCustom(wolfEntity, wolfEntity.m_9131874() + "'s Dog", x, y - 1, z, 64);
		}
	}
}
