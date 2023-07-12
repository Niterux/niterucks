package com.niterux.niterucks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameGui.class)
public class HudModify {
	String[] directions = {"South (Towards positive Z)", "West (Towards Negative X)", "North (Towards Negative Z)", "East (Towards Positive X)"};
	@Shadow
	private Minecraft minecraft;
	@ModifyArg(
		method = "render",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V",
			ordinal = 5),
	index = 1
	)
	private String goodFaceDirection(String par2){
		int facingDir = (int)this.minecraft.player.yaw;
		int i;
		if(facingDir+45<0){
			i =  ((facingDir - 45) / 90) % 4;
			return "Facing: " + directions[(i + 4) % 4];
		}else{
			i = ((facingDir + 45) / 90) % 4;
			return "Facing: " + directions[i];
		}


	}
}
