package io.github.niterux.niterucks.mixin.playerlist;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.MemoryTracker;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.NameSort;
import io.github.niterux.niterucks.niterucksfeatures.playerlist.PlayerListControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

import static io.github.niterux.niterucks.niterucksfeatures.playerlist.PlayerListUtil.getMaxPlayers;
import static io.github.niterux.niterucks.niterucksfeatures.playerlist.PlayerListUtil.getPlayerList;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {
	@Unique
	private final static int HORIZONTAL_SPACING = 120;
	@Unique
	private final static int PLAYER_COUNT_Y_OFFSET = 14;
	@Unique
	private final static int VERTICAL_SPACING = 10;
	@Unique
	private final static int VERTICAL_MARGINAL_AREA = 6;
	@Unique
	private static String[] cachedPlayerList;
	@Unique
	private static int cachedMaxPlayers;
	@Unique
	private static final int assignedDrawList = MemoryTracker.getLists(1);
	@Shadow
	private Minecraft minecraft;

	@Inject(method = "render(FZII)V", at = @At(value = "TAIL"))
	private void renderTabMenu(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 0) TextRenderer textRenderer, @Local(ordinal = 2) int width, @Local(ordinal = 3) int height) {
		if (!PlayerListControls.playerListKeyDown || !minecraft.isMultiplayer())
			return;
		String[] players = getPlayerList();
		if (players == null)
			return;
		int maxPlayerCount = getMaxPlayers();
		if (Arrays.equals(players, cachedPlayerList) && cachedMaxPlayers == maxPlayerCount) {
			GL11.glCallList(assignedDrawList);
			return;
		}
		cachedMaxPlayers = maxPlayerCount;
		cachedPlayerList = Arrays.copyOf(players, players.length);
		Arrays.sort(players, NameSort.INSTANCE);
		int playerListDrawY = Math.max(height / (VERTICAL_MARGINAL_AREA * 2), PLAYER_COUNT_Y_OFFSET);
		int playerListMaxHeight = height * (VERTICAL_MARGINAL_AREA - 2) / VERTICAL_MARGINAL_AREA;
		int maxPlayersInAColumn = playerListMaxHeight / VERTICAL_SPACING;

		int playerListWidth = (int) (Math.ceil((double) players.length / maxPlayersInAColumn) * HORIZONTAL_SPACING);
		int playerListHeight = Math.min(players.length, maxPlayersInAColumn) * VERTICAL_SPACING + 1;
		int playerListDrawX = width / 2 - playerListWidth / 2;

		String playerCountFormatted;
		if (maxPlayerCount > 0) {
			playerCountFormatted = "Players: " + players.length + '/' + maxPlayerCount;
		} else
			playerCountFormatted = "Players: " + players.length;
		GL11.glNewList(assignedDrawList, GL11.GL_COMPILE_AND_EXECUTE);
		this.drawCenteredString(textRenderer, playerCountFormatted, width / 2, playerListDrawY - PLAYER_COUNT_Y_OFFSET, 0xFFFFFFFF);
		//noinspection SuspiciousNameCombination
		fill(playerListDrawX, playerListDrawY, playerListDrawX + playerListWidth, playerListDrawY + playerListHeight, 0x80000000);

		for (int i = 0; i < players.length; i++) {
			int nameDrawX = playerListDrawX + i / maxPlayersInAColumn * HORIZONTAL_SPACING;
			int nameDrawY = playerListDrawY + i % maxPlayersInAColumn * VERTICAL_SPACING + 1;
			this.drawCenteredString(textRenderer,
				MiscUtils.replaceAllAmpersandsWithColorCharacter(players[i]),
				nameDrawX + (HORIZONTAL_SPACING / 2),
				nameDrawY,
				0xFFFFFF);
		}
		GL11.glEndList();
	}
}
