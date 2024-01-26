package io.github.niterux.niterucks.config.option;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionBase;
import net.minecraft.client.options.KeyBinding;

public class KeyBindOption extends OptionBase<KeyBinding> {

	public KeyBindOption(String name, int defaultKey) {
		super(name, new KeyBinding(name, defaultKey));
	}

	public KeyBindOption(String name, KeyBinding defaultValue) {
		super(name, defaultValue);
	}

	public KeyBindOption(String name, String tooltip, KeyBinding defaultValue) {
		super(name, tooltip, defaultValue);
	}

	public KeyBindOption(String name, KeyBinding defaultValue, ChangeListener<KeyBinding> changeListener) {
		super(name, defaultValue, changeListener);
	}

	public KeyBindOption(String name, String tooltip, KeyBinding defaultValue, ChangeListener<KeyBinding> changeListener) {
		super(name, tooltip, defaultValue, changeListener);
	}

	@Override
	public String toSerializedValue() {
		return String.valueOf(get().keyCode);
	}

	@Override
	public void fromSerializedValue(String s) {
		this.get().keyCode = Integer.parseInt(s);
	}

	@Override
	public String getWidgetIdentifier() {
		return "keybinding";
	}
}
