package io.github.niterux.niterucks.mixin;

import net.minecraft.block.TntBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntBlock.class)
public class TntBlockMixin {
	@Inject(method = "onExploded(Lnet/minecraft/world/World;III)V", at = @At("HEAD"), cancellable = true)
	private void preventGhostEntitiesInMP(World world, int x, int y, int z, CallbackInfo ci) {
		if (world.isMultiplayer)
			ci.cancel();
	}
}
