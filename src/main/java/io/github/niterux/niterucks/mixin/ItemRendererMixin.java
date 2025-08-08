package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin extends EntityRenderer<ItemEntity> {
	@ModifyArg(method = "renderItemInfo(Lnet/minecraft/client/render/TextRenderer;Lnet/minecraft/client/render/texture/TextureManager;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"), index = 2)
	private int moveOverstackedItemText(int y, @Local(ordinal = 0, argsOnly = true) ItemStack currItem) {
		if (currItem.getMaxSize() < currItem.size && currItem.isDamaged()) {
			return y - 4;
		}
		return y;
	}

	@Inject(method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;start()V"))
	private void rotateItemSpritePitch(ItemEntity itemEntity, double d, double e, double f4, float g5, float h6, CallbackInfo ci) {
		if(Niterucks.CONFIG.pitchBillboarding.get())
			GL11.glRotatef(MiscUtils.fixSpritePitch(this.dispatcher.cameraPitch), -1.0F, 0.0F, 0.0F);
	}
}
