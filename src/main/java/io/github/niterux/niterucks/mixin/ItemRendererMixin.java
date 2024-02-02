package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@ModifyArg(method = "renderItemInfo(Lnet/minecraft/client/render/TextRenderer;Lnet/minecraft/client/render/texture/TextureManager;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"), index = 2)
	private int moveOverstackedItemText(int y, @Local(ordinal = 0, argsOnly = true) ItemStack currItem) {
		if (currItem.getMaxSize() < currItem.size && currItem.isDamaged()) {
			return y - 4;
		}
		return y;
	}
}
