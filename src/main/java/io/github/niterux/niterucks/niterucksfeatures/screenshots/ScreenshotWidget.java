package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ScreenshotWidget extends ButtonWidget {
	private final int image;
	public ScreenshotWidget(int id, int x, int y, int width, int height, String message, int image) {
		super(id, x, y, width, height, message);
		this.image = image;
	}

	@Override
	public void render(Minecraft minecraft, int i, int j) {
		if (this.visible) {
			boolean hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			DrawUtil.outlineRect(this.x, this.y, width, height, hovered ? -1 : Colors.foreground().toInt());

			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.color3f(1, 1, 1);
			minecraft.textureManager.bind(image);
			DrawUtil.drawTexture(this.x+1, this.y+1, 0, 0, this.width-2, this.height-12, width-2, height-12);

			DrawUtil.drawScrollingText(message, x+1, y+height-11, width-2, 10, Colors.accent());
		}
	}
}
