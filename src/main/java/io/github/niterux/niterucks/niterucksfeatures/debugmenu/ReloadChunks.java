package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ReloadChunks implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.reloadChunksButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		minecraft.worldRenderer.m_6748042();
	}

	@Override
	public @NotNull String actionDescription() {
		return "Reload chunks";
	}
}
