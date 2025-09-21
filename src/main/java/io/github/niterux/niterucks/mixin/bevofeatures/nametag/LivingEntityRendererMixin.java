package io.github.niterux.niterucks.mixin.bevofeatures.nametag;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.bevofeatures.BetaEVO;
import io.github.niterux.niterucks.bevofeatures.PlayerNameStatus;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.entity.living.player.RemotePlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@ModifyArgs(method = "renderNameTag(Lnet/minecraft/entity/living/LivingEntity;Ljava/lang/String;DDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TextRenderer;draw(Ljava/lang/String;III)V"))
	private void makeUsernameColoredBehindWalls(Args args, @Local(ordinal = 0, argsOnly = true) LivingEntity entity) {
		if (!(entity instanceof RemotePlayerEntity))
			return;

		String playerName = ((RemotePlayerEntity) entity).name;
		if (!BetaEVO.playerList.containsKey(playerName))
			return;

		PlayerNameStatus playerNameStatus = BetaEVO.playerList.get(playerName);
		int alpha = (int) args.get(3) & 0xFF000000;
		args.set(3, playerNameStatus.getColor() + alpha);
		if (playerNameStatus.getNickname() != null && playerNameStatus.getPrefix() != null) {
			args.set(0, MiscUtils.replaceAllAmpersandsWithColorCharacter(playerNameStatus.getNickname()) + ' ' + MiscUtils.replaceAllAmpersandsWithColorCharacter(playerNameStatus.getPrefix()));
		}
	}
}
