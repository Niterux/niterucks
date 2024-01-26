package io.github.niterux.niterucks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.niterux.niterucks.Niterucks;

public class NiterucksModmenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return Niterucks::getConfigScreen;
	}
}
