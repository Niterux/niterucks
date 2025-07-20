package io.github.niterux.niterucks.config.widget;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.CategoryWidget;
import io.github.niterux.niterucks.config.option.AuthMeCategory;
import io.github.niterux.niterucks.config.screen.NiterucksAuthMeScreen;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class AuthMeCategoryWidget extends CategoryWidget {
	private final AuthMeCategory category;

	public AuthMeCategoryWidget(int x, int y, int width, int height, AuthMeCategory category) {
		super(x, y, width, height, category);
		this.category = category;
	}

	@Override
	public void onPress() {
		Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
		if (minecraft.screen != null) {
			minecraft.openScreen(new NiterucksAuthMeScreen(minecraft.screen, this.category));
		}
	}
}
