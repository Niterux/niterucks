package io.github.niterux.niterucks.config.option;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionCategoryImpl;

public class AuthMeCategory extends OptionCategoryImpl {

	public AuthMeCategory() {
		super("authme");
	}

	@Override
	public String getWidgetIdentifier() {
		return "authme_category";
	}
}
