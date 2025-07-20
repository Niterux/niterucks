package io.github.niterux.niterucks.config.widget;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import net.minecraft.client.render.TextRenderer;

public class CensoredTextFieldWidget extends TextFieldWidget {

	public CensoredTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, String text) {
		super(textRenderer, x, y, width, height, text);
	}

	@Override
	public void drawWidget(int mouseX, int mouseY, float delta) {
		String text = this.text;
		this.text = "*".repeat(text.length());
		super.drawWidget(mouseX, mouseY, delta);
		this.text = text;
	}
}
