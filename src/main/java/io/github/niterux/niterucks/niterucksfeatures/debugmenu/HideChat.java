package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.chatHidden;

public class HideChat implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.hideChatButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		chatHidden = !chatHidden;
	}

	@Override
	public String getMessage() {
		return chatHidden ? "You will no longer see new chat messages." : "You can see new messages again.";
	}

	@Override
	public @NotNull String actionDescription() {
		return "Hide server chat messages";
	}
}
