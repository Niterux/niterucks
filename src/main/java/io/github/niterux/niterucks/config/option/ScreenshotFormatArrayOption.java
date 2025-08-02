package io.github.niterux.niterucks.config.option;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.OptionBase;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import io.github.niterux.niterucks.api.screenshots.ScreenshotFormatRegistry;

import java.util.ArrayList;

public class ScreenshotFormatArrayOption extends OptionBase<String> {
	private final ArrayList<String> values;
	private boolean enabled = true;
	/*
	 HACK: The superclass of ScreenshotFormatArrayWidget requires it talks to a
	 StringArrayOption, we create our own every time a format is added or this class
	 is instantiated. When we read out the value we use the StringArrayOption since
	 that's what the widget talks to.
	 */
	private StringArrayOption backingStringArrayOptionHack;

	public ScreenshotFormatArrayOption(String name, ArrayList<String> values, String defaultValue) {
		super(name, defaultValue);
		this.values = values;
		createBackingStringArrayOption();
	}

	@Override
	public String getWidgetIdentifier() {
		return "screenshotformat[]";
	}

	@Override
	public String get() {
		String potentiallyInvalidFormat = backingStringArrayOptionHack.get();
		if (!ScreenshotFormatRegistry.getRegisteredScreenshotReaderWriters().containsKey(potentiallyInvalidFormat)) {
			setDefault();
			values.remove(potentiallyInvalidFormat);
			createBackingStringArrayOption();
		}
		return super.get();
	}

	@Override
	public void set(String value) {
		if (values.contains(value)) {
			super.set(value);
			backingStringArrayOptionHack.set(value);
		} else {
			values.add(value);
			createBackingStringArrayOption();
			set(value);
		}
	}

	@Override
	public String toSerializedValue() {
		return backingStringArrayOptionHack.get();
	}

	@Override
	public void fromSerializedValue(String value) {
		set(value);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void addFormat(String format) {
		values.add(format);
		createBackingStringArrayOption();
	}

	public StringArrayOption returnBackingStringArrayOption() {
		return backingStringArrayOptionHack;
	}

	private void createBackingStringArrayOption() {
		backingStringArrayOptionHack = new StringArrayOption(getName(), values.toArray(new String[0]), value);
	}
}
