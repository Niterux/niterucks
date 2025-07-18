package io.github.niterux.niterucks.config.option;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionBase;
import io.github.niterux.niterucks.config.optionstorage.AuthMeWholeListOptionStorage;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.minecraft.client.options.KeyBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthMeWholeListOption extends OptionBase<AuthMeWholeListOptionStorage> {

	public AuthMeWholeListOption(String name) {
		super(name, new AuthMeWholeListOptionStorage(name));
	}

	@Override
	public String toSerializedValue() {
		return String.valueOf(get().IPUsernamePassword);
	}

	@Override
	public void fromSerializedValue(String s) {
		this.get().IPUsernamePassword = new ArrayList<>();
	}

	@Override
	public String getWidgetIdentifier() {
		return "authMeList";
	}
}
