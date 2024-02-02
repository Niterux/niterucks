package io.github.niterux.niterucks.mixin.bevofeatures;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.bevofeatures.BetaEVO;
import io.github.niterux.niterucks.bevofeatures.PlayerNameStatus;
import io.github.niterux.niterucks.niterucksfeatures.RainbowManager;
import net.minecraft.client.entity.living.player.RemotePlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@ModifyArg(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;Ljava/lang/String;DDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)V", ordinal = 0), index = 3)
	private int makeUsernameColoredBehindWalls(int original, @Local(ordinal = 0, argsOnly = true) LivingEntity entity) {
		if (entity instanceof RemotePlayerEntity) {
			String playerName = ((RemotePlayerEntity) entity).name;
			if (BetaEVO.playerList.containsKey(playerName)) {
				PlayerNameStatus playerNameStatus = BetaEVO.playerList.get(playerName);
				int alpha = original & 0xFF000000;
				return ((playerNameStatus.getIsRainbow()) ? RainbowManager.getColor() : playerNameStatus.getColor()) + alpha;
			}
		}
		return original;
	}

	@ModifyArg(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;Ljava/lang/String;DDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)V", ordinal = 1), index = 3)
	private int makeUsernameColored(int original, @Local(ordinal = 0, argsOnly = true) LivingEntity entity) {
		if (entity instanceof RemotePlayerEntity) {
			String playerName = ((RemotePlayerEntity) entity).name;
			if (BetaEVO.playerList.containsKey(playerName)) {
				PlayerNameStatus playerNameStatus = BetaEVO.playerList.get(playerName);
				return (playerNameStatus.getIsRainbow()) ? RainbowManager.getColor() : playerNameStatus.getColor();
			}
		}
		return original;
	}
}
