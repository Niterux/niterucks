package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Locale;

import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;

public class ScreenshotViewerScreen extends Screen {
	private final Screen parent;
	private final ScreenshotGalleryScreen.ScreenshotInfo image;
	private final int glId;
	private float imageWidth, imageHeight;
	private int x, y;

	public ScreenshotViewerScreen(Screen parent, ScreenshotGalleryScreen.ScreenshotInfo image) {
		this.parent = parent;
		this.image = image;
		this.glId = image.getGlId();
	}

	@Override
	public void init() {
		imageHeight = Math.min(image.getImage().getHeight(), height-80);
		imageWidth = (imageHeight/image.getImage().getHeight()) * image.getImage().getWidth();
		x = (int) (width/2-imageWidth/2);
		y = (int) (height/2-imageHeight/2);
		buttons.add(new ButtonWidget(1, (int) (x+imageWidth+2), y, 50, 20, "Copy"));
		buttons.add(new ButtonWidget(0, width/2-75, height-35, 150, 20, "Back"));
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		renderBackground();

		minecraft.textureManager.bind(glId);

		DrawUtil.drawTexture(x,	y,0, 0, (int) imageWidth, (int) imageHeight, imageWidth, imageHeight);

		drawCenteredString(minecraft.textRenderer, image.getFile().getFileName().toString(), width/2, 20, -1);

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0){
			minecraft.openScreen(parent);
		} else if (button.id == 1){

			if (System.getenv().getOrDefault("DESKTOP_SESSION", "").toLowerCase(Locale.ROOT)
				.contains("wayland")){
				try {
					ProcessBuilder builder = new ProcessBuilder("bash", "-c", "wl-copy -t image/png < "+image.getFile());
					Process p = builder.start();
					p.waitFor();
				} catch (IOException | InterruptedException ignored) {
					Niterucks.LOGGER.error(ignored.getMessage());
				}
				return;
			}

			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[]{DataFlavor.imageFlavor};
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return DataFlavor.imageFlavor.equals(flavor);
				}

				@NotNull
				@Override
				public Object getTransferData(DataFlavor flavor) {
					return image.getImage();
				}
			}, null);
		}
	}
}
