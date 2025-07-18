package io.github.niterux.niterucks.mixin;

import net.minecraft.block.TorchBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {
	@Inject(method = "onAdded", at = @At("HEAD"), cancellable = true)
	private void fixClientSideBrokenTorchPlacement(World world, int x, int y, int z, CallbackInfo ci) {
		int torchMetadata = world.getBlockMetadata(x, y, z);
		if (torchMetadata < 6 && torchMetadata > 0)
			ci.cancel();
	}
}
