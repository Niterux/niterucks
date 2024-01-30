package io.github.niterux.niterucks.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	@Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
	private void setViewDistanceScaling(World x, double y, double z, double stack, ItemStack par5, CallbackInfo ci) {
		this.viewDistanceScaling = 2.0;
	}

	public ItemEntityMixin(World world) {
		super(world);
	}
}
