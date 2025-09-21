package io.github.niterux.niterucks.mixin.ui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryMenuScreen.class)
public class InventoryMenuScreenMixin {
	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "INVOKE", target = "Ljava/lang/String;trim()Ljava/lang/String;"))
	private String addItemID(String original, @Local(ordinal = 0) InventorySlot slot) {
		if (!Niterucks.CONFIG.showItemIDs.get())
			return original;
		ItemStack stack = slot.getStack();
		return original + " §7" + stack.itemId + ':' + stack.getMetadata();
	}
}
