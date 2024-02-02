package io.github.niterux.niterucks.niterucksfeatures;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class ColorCheatSheetScreen extends Screen {
	private static final char[] colors = "0123456789abcdef".toCharArray();
	private static final BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
	private final Screen parent;

	public ColorCheatSheetScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		//noinspection unchecked
		this.buttons.add(new ButtonWidget(6, this.width / 2 - 49, this.height / 4 + 100, 98, 20, "Back"));
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		this.minecraft.openScreen(this.parent);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();

		//Draw grey background
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		bufferBuilder.start();
		bufferBuilder.color(0x636363);
		bufferBuilder.vertex((double) width / 2 - 85, (double) this.height / 4 - 10, 0.0); // top left
		bufferBuilder.vertex((double) width / 2 - 85, (double) this.height / 4 + 90, 0.0); // bottom left
		bufferBuilder.vertex((double) width / 2 + 85, (double) this.height / 4 + 90, 0.0); // bottom right
		bufferBuilder.vertex((double) width / 2 + 85, (double) this.height / 4 - 10, 0.0); //top right
		bufferBuilder.end();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		this.drawCenteredString(this.textRenderer, "Text color cheat sheet", this.width / 2, this.height / 4 - 40, 0xFFFFFF);
		for (int i = 0; i < colors.length; i++)
			this.drawCenteredString(this.textRenderer, "§" + colors[i] + '&' + colors[i], this.width / 2 + ((i >> 3 & 1) * 140) - 70, this.height / 4 + (i & 7) * 10, 0xFFFFFF);
		super.render(mouseX, mouseY, tickDelta);
	}
}
