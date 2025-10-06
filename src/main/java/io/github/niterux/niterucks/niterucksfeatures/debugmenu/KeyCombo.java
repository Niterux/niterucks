package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KeyCombo {
	int getComboKey();

	void onPress(Minecraft minecraft);

	@Nullable
	default String getMessage() {
		return null;
	}

	@NotNull
	String actionDescription();
}
