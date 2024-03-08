package io.github.niterux.niterucks.mixin.bevofeatures.playerlist;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.bevofeatures.BetaEVO;
import io.github.niterux.niterucks.bevofeatures.BetaEVOPlayerListHelper;
import io.github.niterux.niterucks.bevofeatures.PlayerNameStatus;
import io.github.niterux.niterucks.mixin.invokers.FillInvoker;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {
	@Inject(method = "render(FZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;getSleepTimer()I", ordinal = 0))
	private void renderTabMenu(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci, @Local(ordinal = 0) TextRenderer textRenderer, @Local(ordinal = 2) int width, @Local(ordinal = 3) int height) {
		if (BetaEVOPlayerListHelper.playerListKeyDown && BetaEVOPlayerListHelper.playerListPacketRecieved) {
			PlayerNameStatus[] players = BetaEVO.playerList.values().toArray(new PlayerNameStatus[0]);
			int maxPlayersInAColumn = 15;
			int horizontalSpacing = 150;
			int playerListWidth = (players.length / maxPlayersInAColumn + 1) * horizontalSpacing;
			int playerListHeight = Math.min(players.length, maxPlayersInAColumn) * 11 + 1;
			int playerListDrawX = width / 2 - playerListWidth / 2;
			int PlayerListDrawY = height / 6;
			//noinspection SuspiciousNameCombination
			((FillInvoker) this).invokeFill(playerListDrawX, PlayerListDrawY, playerListDrawX + playerListWidth, PlayerListDrawY + playerListHeight, 0x80000000);

			for (int i = 0; i < BetaEVO.playerList.size(); i++) {
				int prefixWidth = textRenderer.getWidth(players[i].getPrefix());
				int nameDrawX = playerListDrawX + i / maxPlayersInAColumn * horizontalSpacing;
				int nameDrawY = PlayerListDrawY + i % maxPlayersInAColumn * 11 + 1;
				this.drawString(textRenderer,
					MiscUtils.colorify(players[i].getPrefix()),
					nameDrawX,
					nameDrawY,
					0xFFFFFF);
				this.drawString(textRenderer,
					MiscUtils.colorify(players[i].getNickname()),
					nameDrawX + prefixWidth,
					nameDrawY,
					players[i].getColor());
			}
		}
	}
}
