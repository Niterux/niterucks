package io.github.niterux.niterucks.niterucksfeatures;

import net.minecraft.client.gui.GameGui;

public class MiscUtils {
	public static void printDebugKeys(GameGui gui) {
		gui.addChatMessage("§e[Debug]:§f Key bindings:");
		gui.addChatMessage("F3 + A = Reload chunks");
		gui.addChatMessage("F3 + D = Clear chat");
		gui.addChatMessage("F3 + G = Show chunk boundaries");
		gui.addChatMessage("F3 + Q = Show this list");
		gui.addChatMessage("F3 + S = Reload texture pack");
	}
}
