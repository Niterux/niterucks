package io.github.niterux.niterucks;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.niterux.niterucks.screen.NiterucksOptionsScreen;

public class ModMenuConfig implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return NiterucksOptionsScreen::new;
	}
}
