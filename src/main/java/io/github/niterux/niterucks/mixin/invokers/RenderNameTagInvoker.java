package io.github.niterux.niterucks.mixin.invokers;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface RenderNameTagInvoker {
	@Invoker("renderNameTag")
	void renderNameTagCustom(LivingEntity entity, String name, double dx, double dy, double dz, int range);
}
