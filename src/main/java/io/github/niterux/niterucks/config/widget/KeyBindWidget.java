package io.github.niterux.niterucks.config.widget;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.niterux.niterucks.config.option.KeyBindOption;
import org.lwjgl.input.Keyboard;

public class KeyBindWidget extends VanillaButtonWidget {

	private final KeyBindOption option;

	public KeyBindWidget(int x, int y, int width, int height, KeyBindOption option) {
		super(x, y, width, height,
			Keyboard.getKeyName(option.get().keyCode),
			widget -> {
			});
		this.option = option;
	}

	@Override
	public String getMessage() {
		if (isFocused()) {
			return "> " + super.getMessage() + " <";
		}
		return super.getMessage();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.setFocused(true);
		super.onClick(mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.isFocused() && keyCode != 0) {
			if (keyCode == Keyboard.KEY_ESCAPE) {
				option.get().keyCode = 0;
			} else {
				option.get().keyCode = keyCode;
			}
			setFocused(false);
			updateMessage();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void updateMessage() {
		setMessage(Keyboard.getKeyName(option.get().keyCode));
	}
}
