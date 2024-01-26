package io.github.niterux.niterucks.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.locale.LanguageManager;

public class NiterucksOptionsScreen extends Screen {
	private final Screen parent;
	protected String title = "Options";

	public NiterucksOptionsScreen(Screen screen) {
		this.parent = screen;
	}

	public void init() {
		LanguageManager var1 = LanguageManager.getInstance();
		this.title = var1.translate("options.title");
		this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, var1.translate("gui.done")));

	}

	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 200) {
				this.minecraft.openScreen(this.parent);
			}

		}
	}

	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
