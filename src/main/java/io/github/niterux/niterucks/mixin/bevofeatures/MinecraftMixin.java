package io.github.niterux.niterucks.mixin.bevofeatures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.niterux.niterucks.bevofeatures.BetaEVOFlyHelper.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Redirect(method = "Lnet/minecraft/client/Minecraft;tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(I)V"))
	private void redirectScrolling(PlayerInventory instance, int i){
		if(flying && flyingControls[3]) {
			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}
		flySpeed += i;
		if (flySpeed > 16)
			flySpeed = 16;
		else if (flySpeed < 0)
			flySpeed = 0;
		} else{
			instance.scrollInHotbar(i);
		}
	}
}
