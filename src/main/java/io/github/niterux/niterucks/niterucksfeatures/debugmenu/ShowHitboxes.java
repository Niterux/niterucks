package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.hitboxEnabled;

public class ShowHitboxes implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.showHitboxesButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		hitboxEnabled = !hitboxEnabled;
	}

	@Override
	public String getMessage() {
		return hitboxEnabled ? "Hitboxes: shown" : "Hitboxes: hidden";
	}

	@Override
	public @NotNull String actionDescription() {
		return "Show hitboxes";
	}
}
