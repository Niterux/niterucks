package io.github.niterux.niterucks.mixin.debugmenu.hitbox;

import io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft minecraft;

	@Inject(method = "renderWorld(FJ)V", at = @At("HEAD"))
	private void arrowHitBoxDecision(float tickDelta, long renderTimeLimit, CallbackInfo ci) {
		ItemStack mainHandStack = minecraft.player.inventory.getMainHandStack();
		if (mainHandStack == null) {
			GameFeaturesStates.selfIsHoldingBow = false;
			return;
		}
		GameFeaturesStates.selfIsHoldingBow = minecraft.player.inventory.getMainHandStack().itemId == Item.BOW.id;
	}
}
