package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;

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
		imageHeight = Math.min(image.getHeight(), height - 80);
		imageWidth = Math.min(width - 105, (imageHeight / image.getHeight()) * image.getWidth());
		imageHeight = Math.min((imageWidth / image.getWidth()) * image.getHeight(), imageHeight);
		x = (int) (width / 2 - imageWidth / 2);
		y = (int) (height / 2 - imageHeight / 2);
		buttons.add(new ButtonWidget(0, width / 2 - 75, height - 35, 150, 20, "Back"));
		buttons.add(new ButtonWidget(1, (int) (x + imageWidth + 2), y, 50, 20, "Copy"));
		if(Desktop.isDesktopSupported())
			buttons.add(new ButtonWidget(2, (int) (x + imageWidth + 2), y + 24, 50, 20, "Open"));
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		renderBackground();

		minecraft.textureManager.bind(glId);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		DrawUtil.drawTexture(x, y, 0, 0, (int) imageWidth, (int) imageHeight, imageWidth, imageHeight);

		drawCenteredString(minecraft.textRenderer, image.getImagePath().getFileName().toString(), width / 2, 20, -1);

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 0:
				minecraft.openScreen(parent);
				break;
			case 1:
				copyImageToClipboard();
				break;
			case 2:
				openImage();
				break;
		}
	}

	private void copyImageToClipboard() {
		if (System.getenv().getOrDefault("DESKTOP_SESSION", "").toLowerCase(Locale.ROOT)
			.contains("wayland")) {
			try {
				ProcessBuilder builder = new ProcessBuilder("bash", "-c", "wl-copy -t image/png < " + image.getImagePath());
				Process p = builder.start();
				p.waitFor();
			} catch (IOException | InterruptedException e) {
				Niterucks.LOGGER.error(e.getMessage());
			}
			return;
		}

		try {
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
				public Object getTransferData(DataFlavor flavor) throws IOException {
					try (InputStream in = Files.newInputStream(image.getImagePath())) {
						return ImageIO.read(in);
					}
				}
			}, null);
		} catch(IllegalStateException ignored) {}
	}

	private void openImage() {
		if(!Desktop.isDesktopSupported())
			throw new IllegalStateException("openImage called when the awt desktop isn't supported!");
		try {
			Desktop.getDesktop().open(image.getImagePath().toFile());
		} catch (IOException | IllegalArgumentException  e) {
			Niterucks.LOGGER.error("FAILED TO OPEN FILE, HARASS YOUR LOCAL PROGRAMMER!:");
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			Niterucks.LOGGER.warn("Opening files isn't supported on your OS!");
		}
	}
}
