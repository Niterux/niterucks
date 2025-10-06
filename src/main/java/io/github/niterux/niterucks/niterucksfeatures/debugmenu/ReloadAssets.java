package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.invokers.ForceReloadInvoker;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ReloadAssets implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.reloadAssetsButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		((ForceReloadInvoker) minecraft).invokeForceReload();
	}

	@Override
	public @NotNull String actionDescription() {
		return "Reload all assets";
	}
}
