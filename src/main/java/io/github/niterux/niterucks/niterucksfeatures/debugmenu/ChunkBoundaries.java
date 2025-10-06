package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.chunkBordersEnabled;

public class ChunkBoundaries implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.chunkBoundariesButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		chunkBordersEnabled = !chunkBordersEnabled;
	}

	@Override
	public String getMessage() {
		return chunkBordersEnabled ? "Chunk borders: shown" : "Chunk borders: hidden";
	}

	@Override
	public @NotNull String actionDescription() {
		return "Show chunk boundaries";
	}
}
