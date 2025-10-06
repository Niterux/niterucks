package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public class ShowHelp implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.showHelpButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		minecraft.gui.addChatMessage("§e[Debug]:§f Key bindings:");
		for (KeyCombo keyCombo : DebugHotKeyRegistry.HotKeyRegistry)
			minecraft.gui.addChatMessage("F3 + " + Keyboard.getKeyName(keyCombo.getComboKey()).toUpperCase() + " = " + keyCombo.actionDescription());
	}

	@Override
	public @NotNull String actionDescription() {
		return "Show this list";
	}
}
