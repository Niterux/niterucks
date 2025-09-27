package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.RainbowManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.world.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private Minecraft minecraft;

	@ModifyArgs(method = "renderBlockOutline(Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/world/HitResult;ILnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", remap = false))
	private void rainbowOutline(Args args) {
		if (!Niterucks.CONFIG.rainbowBlockOutline.get())
			return;
		double[] rgb = RainbowManager.getRawRgb();
		args.set(0, (float) rgb[0]);
		args.set(1, (float) rgb[1]);
		args.set(2, (float) rgb[2]);
		args.set(3, 1.0F);
	}

	@ModifyArg(method = "renderBlockOutline(Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/world/HitResult;ILnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glLineWidth(F)V", remap = false))
	private float fixWidth(float width) {
		return width * minecraft.height / 480f;
	}
}
