package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ScreenshotWidget extends ButtonWidget {
	private final ScreenshotInfo image;
	private final int imageWidth;
	private int glId = -1;
	private int imageHeight, imageY;
	public ScreenshotWidget(int id, int x, int y, int width, int height, String message, ScreenshotInfo image) {
		super(id, x, y, width, height, message);
		this.image = image;
		imageWidth = width - 2;
	}

	public int getGlId() {
		return glId;
	}

	@Override
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.visible) {
			boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			DrawUtil.outlineRect(this.x, this.y, width, height, hovered ? -1 : Colors.foreground().toInt());

			GlStateManager.color3f(1, 1, 1);
			if (glId != -1) {
				GlStateManager.enableTexture();
				GlStateManager.enableBlend();
				minecraft.textureManager.bind(glId);
				DrawUtil.drawTexture(this.x + 1, imageY, 0, 0, imageWidth, imageHeight,
					imageWidth, imageHeight);
				GlStateManager.disableBlend();
			}
			DrawUtil.drawScrollingText(message, x + 1, y + height - 11, width - 2, 10, Colors.accent());
		}
	}

	public void clearBufferedImage() {
		image.clearBufferedImage();
	}

	public void onResolvedImage(int glId, double thumbnailAspectRatio) {
		this.glId = glId;
		imageHeight = (int) Math.min(imageWidth / thumbnailAspectRatio, height - 12);
		imageY = (y + 1) + (height - 2) / 2 - imageHeight / 2;
	}
}
