package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ClearChat implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.clearChatButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		minecraft.gui.clearChat();
	}

	@Override
	public @NotNull String actionDescription() {
		return "Clear chat";
	}
}
