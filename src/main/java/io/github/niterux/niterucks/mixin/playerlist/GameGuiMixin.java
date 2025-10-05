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

import static io.github.niterux.niterucks.niterucksfeatures.playerlist.PlayerListUtil.*;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {
	@Unique
	private final static int MINIMUM_COLUMN_WIDTH = 40;
	@Unique
	private final static int NAME_WIDTH_MARGIN = 10;
	@Unique
	private final static int HORIZONTAL_BG_MARGIN = 5;
	@Unique
	private final static int PLAYER_COUNT_Y_OFFSET = 14;
	@Unique
	private final static int VERTICAL_SPACING = 10;
	@Unique
	private final static int VERTICAL_MARGINAL_AREA = 6;
	@Unique
	private static final int assignedDrawList = MemoryTracker.getLists(1);
	@Unique
	private static int cachedMaxPlayers;
	@Shadow
	private Minecraft minecraft;
	@Unique
	private static int lastKnownGUIScale;

	@Inject(method = "render(FZII)V", at = @At(value = "TAIL"))
	private void renderTabMenu(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 0) TextRenderer textRenderer, @Local(ordinal = 2) int width, @Local(ordinal = 3) int height) {
		if (!PlayerListControls.playerListKeyDown || !minecraft.isMultiplayer())
			return;
		String[] players = getPlayerList();
		if (players == null)
			return;
		int maxPlayerCount = getMaxPlayers();
		if (lastKnownGUIScale == minecraft.options.guiScale && cachedMaxPlayers == maxPlayerCount && updateCacheReturnsFalseIfInvalidated(players)) {
			GL11.glCallList(assignedDrawList);
			return;
		}
		players = Arrays.copyOf(players, players.length);
		cachedMaxPlayers = maxPlayerCount;
		lastKnownGUIScale = minecraft.options.guiScale;
		for (int i = 0; i < players.length; i++)
			players[i] = MiscUtils.replaceAllAmpersandsWithColorCharacter(players[i]);
		Arrays.sort(players, NameSort.INSTANCE);
		int playerListDrawY = Math.max(height / (VERTICAL_MARGINAL_AREA << 1), PLAYER_COUNT_Y_OFFSET);
		int playerListMaxHeight = height * (VERTICAL_MARGINAL_AREA - 2) / VERTICAL_MARGINAL_AREA;
		int maxPlayersInAColumn = playerListMaxHeight / VERTICAL_SPACING;
		int columns = (int) Math.ceil((double) players.length / maxPlayersInAColumn);
		int playerListHeight = Math.min(players.length, maxPlayersInAColumn) * VERTICAL_SPACING + 1;
		int[] playerNameWidthStorage = new int[players.length];
		int[] playerNameDrawXPosStorage = new int[players.length];
		int playerListWidth = 0;
		for (int i = 0; i < columns; i++) {
			int index = i * maxPlayersInAColumn;
			int end = Math.min(players.length, index + maxPlayersInAColumn);
			int maximum = MINIMUM_COLUMN_WIDTH;
			for (int j = index; j < end; j++) {
				int nameWidth = textRenderer.getWidth(players[j]);
				playerNameWidthStorage[j] = nameWidth;
				maximum = Math.max(nameWidth + NAME_WIDTH_MARGIN, maximum);
			}
			for (int j = index; j < end; j++)
				playerNameDrawXPosStorage[j] = playerListWidth + ((maximum - playerNameWidthStorage[j]) >> 1);
			playerListWidth += maximum;
		}
		int playerListDrawX = (width - playerListWidth) >> 1;
		String playerCountFormatted;
		if (maxPlayerCount > 0) {
			playerCountFormatted = "Players: " + players.length + '/' + maxPlayerCount;
		} else
			playerCountFormatted = "Players: " + players.length;
		GL11.glNewList(assignedDrawList, GL11.GL_COMPILE_AND_EXECUTE);
		GL11.glPushMatrix();
		GL11.glTranslated(playerListDrawX, playerListDrawY, 0);
		this.drawCenteredString(textRenderer, playerCountFormatted, playerListWidth >> 1, -PLAYER_COUNT_Y_OFFSET, 0xFFFFFFFF);
		fill(-HORIZONTAL_BG_MARGIN, 0, playerListWidth + HORIZONTAL_BG_MARGIN, playerListHeight, 0x80000000);

		for (int i = 0; i < players.length; i++) {
			int nameDrawY = i % maxPlayersInAColumn * VERTICAL_SPACING + 1;
			this.drawString(textRenderer,
				players[i],
				playerNameDrawXPosStorage[i],
				nameDrawY,
				0xFFFFFF);
		}
		GL11.glPopMatrix();
		GL11.glEndList();
	}
}
