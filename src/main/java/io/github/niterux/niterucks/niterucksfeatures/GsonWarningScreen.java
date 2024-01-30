package io.github.niterux.niterucks.niterucksfeatures;

import net.minecraft.client.gui.screen.Screen;

import static com.terraformersmc.modmenu.util.ScreenUtil.openLink;

public class GsonWarningScreen extends Screen {
	@Override
	protected void keyPressed(char chr, int key) {
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (mouseX < this.textRenderer.getWidth("https://ornithemc.net/") + this.width / 2 - 150 &&
				mouseX > this.width / 2 - 150 &&
				mouseY < this.height / 4 + 68 &&
				mouseY > this.height / 4 + 58) {
				openLink(this, "https://ornithemc.net/", "niterucks");
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, "Gson out of date!", this.width / 2, this.height / 4 - 40, 0xFFFFFF);
		this.drawString(this.textRenderer, "The Gson library on your Minecraft instance is out of date", this.width / 2 - 150, this.height / 4, 0xA0A0A0);
		this.drawString(this.textRenderer, "It is likely that you are using an older installation of Ornithe.", this.width / 2 - 150, this.height / 4 + 18, 0xA0A0A0);
		this.drawString(this.textRenderer, "Please download the Ornithe installer from", this.width / 2 - 150, this.height / 4 + 46, 0xA0A0A0);
		this.drawString(this.textRenderer, "https://ornithemc.net/", this.width / 2 - 150, this.height / 4 + 58, 0x0000EE);
		this.drawString(this.textRenderer, "and make a new instance using it.", this.width / 2 - 150, this.height / 4 + 70, 0xA0A0A0);
		super.render(mouseX, mouseY, tickDelta);
	}
}
