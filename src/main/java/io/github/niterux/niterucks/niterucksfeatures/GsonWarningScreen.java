package io.github.niterux.niterucks.niterucksfeatures;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class GsonWarningScreen extends Screen {
	private int f_6624151 = 0;

	@Override
	public void tick() {
		++this.f_6624151;
	}

	@Override
	public void init() {
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
	}

	@Override
	protected void keyPressed(char chr, int key) {
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, "Out of memory!", this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.drawString(this.textRenderer, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
		this.drawString(this.textRenderer, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
		this.drawString(this.textRenderer, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
		this.drawString(this.textRenderer, "memory. If you are playing in a web browser, try", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
		this.drawString(this.textRenderer, "downloading the game and playing it offline.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 45, 10526880);
		this.drawString(this.textRenderer, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
		this.drawString(this.textRenderer, "Please restart the game.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
		super.render(mouseX, mouseY, tickDelta);
	}
}
