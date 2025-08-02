package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Locale;

import static io.github.niterux.niterucks.niterucksfeatures.screenshots.TextureUtil.getPowerOf2Ratio;

public class ScreenshotViewerScreen extends Screen {
	private final Screen parent;
	private final ScreenshotInfo image;
	private final int glId;
	private final float imageVMax, imageUMax;
	private int imageDisplayWidth, imageDisplayHeight;
	private int x, y;

	public ScreenshotViewerScreen(Screen parent, ScreenshotInfo image) {
		this.parent = parent;
		this.image = image;
		this.glId = image.getGlId();
		imageVMax = getPowerOf2Ratio(image.getWidth());
		imageUMax = getPowerOf2Ratio(image.getHeight());
	}

	@Override
	public void init() {
		imageDisplayHeight = Math.min(image.getHeight(), height - 80);
		imageDisplayWidth = Math.min(width - 105, imageDisplayHeight * image.getWidth() / image.getHeight());
		imageDisplayHeight = Math.min(imageDisplayWidth * image.getHeight() / image.getWidth(), imageDisplayHeight);
		x = width / 2 - imageDisplayWidth / 2;
		y = height / 2 - imageDisplayHeight / 2;
		buttons.add(new ButtonWidget(0, width / 2 - 75, height - 35, 150, 20, "Back"));
		buttons.add(new ButtonWidget(1, x + imageDisplayWidth + 2, y, 50, 20, "Copy"));
		if (Desktop.isDesktopSupported())
			buttons.add(new ButtonWidget(2, x + imageDisplayWidth + 2, y + 24, 50, 20, "Open"));
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		renderBackground();

		minecraft.textureManager.bind(glId);

		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		TextureUtil.renderTexturePortion(x, y, imageDisplayWidth + x, y + imageDisplayHeight, 0, 0, imageVMax, imageUMax);

		drawCenteredString(minecraft.textRenderer, image.getImagePath().getFileName().toString(), width / 2, 20, -1);

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 0:
				image.release();
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

	@Override
	public void handleKeyboard() {
		if (Keyboard.getEventKey() == 1) {
			image.release();
			this.minecraft.openScreen(null);
			this.minecraft.closeScreen();
			return;
		}
		super.handleKeyboard();
	}

	private void copyImageToClipboard() {
		if (System.getenv().getOrDefault("DESKTOP_SESSION", "").toLowerCase(Locale.ROOT)
			.contains("wayland")) {
			try {
				ProcessBuilder builder = new ProcessBuilder("bash", "-c", "wl-copy -t image/png < " + image.getImagePath());
				Process p = builder.start();
				p.waitFor();
			} catch (IOException | InterruptedException e) {
				Niterucks.LOGGER.error("Failed to copy screenshot", e);
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
				public Object getTransferData(DataFlavor flavor) {
					return image.getImage();
				}
			}, null);
		} catch (IllegalStateException ignored) {
		}
	}

	private void openImage() {
		if (!Desktop.isDesktopSupported())
			throw new IllegalStateException("openImage called when the awt desktop isn't supported!");
		try {
			Desktop.getDesktop().open(image.getImagePath().toFile());
		} catch (IOException | IllegalArgumentException e) {
			Niterucks.LOGGER.error("FAILED TO OPEN FILE, HARASS YOUR LOCAL PROGRAMMER!:");
			Niterucks.LOGGER.error("An error occurred: ", e);
		} catch (UnsupportedOperationException e) {
			Niterucks.LOGGER.warn("Opening files isn't supported on your OS!");
		}
	}
}
