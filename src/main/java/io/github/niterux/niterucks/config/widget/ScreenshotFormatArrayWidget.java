package io.github.niterux.niterucks.config.widget;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.StringArrayWidget;
import io.github.niterux.niterucks.config.option.ScreenshotFormatArrayOption;

public class ScreenshotFormatArrayWidget extends StringArrayWidget {
	private final ScreenshotFormatArrayOption option;

	public ScreenshotFormatArrayWidget(int x, int y, int width, int height, ScreenshotFormatArrayOption option) {
		super(x, y, width, height, option.returnBackingStringArrayOption());
		this.option = option;
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		this.active = option.isEnabled();
		super.drawWidget(mouseX, mouseY, delta);
	}
}
