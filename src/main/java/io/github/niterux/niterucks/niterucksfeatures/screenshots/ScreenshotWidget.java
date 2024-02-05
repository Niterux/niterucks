package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ScreenshotWidget extends ButtonWidget {
	private final ScreenshotGalleryScreen.ScreenshotInfo image;
	private final int imageWidth, imageHeight, imageY;

	public ScreenshotWidget(int id, int x, int y, int width, int height, String message, ScreenshotGalleryScreen.ScreenshotInfo image) {
		super(id, x, y, width, height, message);
		this.image = image;

		imageWidth = width - 2;
		imageHeight = Math.min((int) ((imageWidth / (float) image.getWidth()) * image.getHeight()), height - 12);
		imageY = (y + 1) + (height - 2) / 2 - imageHeight / 2;
	}

	@Override
	public void render(Minecraft minecraft, int i, int j) {
		if (this.visible) {
			boolean hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			DrawUtil.outlineRect(this.x, this.y, width, height, hovered ? -1 : Colors.foreground().toInt());

			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.color3f(1, 1, 1);
			minecraft.textureManager.bind(image.getThumbGlId());
			DrawUtil.drawTexture(this.x + 1, imageY, 0, 0, imageWidth, imageHeight,
				imageWidth, imageHeight);

			DrawUtil.drawScrollingText(message, x + 1, y + height - 11, width - 2, 10, Colors.accent());
		}
	}
}
