package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import io.github.niterux.niterucks.Niterucks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ScreenshotGalleryScreen extends Screen {

	private static final Path screenshotsDir = FabricLoader.getInstance().getGameDir().resolve("screenshots");

	private final Screen parent;

	private static final List<ScreenshotInfo> files = new ArrayList<>();
	private int index, count;
	private ButtonWidget prev, next;


	public ScreenshotGalleryScreen(Screen parent) {
		this.parent = parent;
	}

	@Override
	public void init() {
		int widgetWidth = 100;
		int widgetHeight = widgetWidth * 3 / 4;
		int padding = 5;
		count = (width / (widgetWidth + padding)) * ((height - 85) / (widgetHeight + padding));


		CompletableFuture.runAsync(() -> {
			try (Stream<Path> paths = Files.list(screenshotsDir)) {
				paths.sorted(Comparator.reverseOrder()).forEachOrdered(p -> {
					if (files.stream().map(ScreenshotInfo::getFile).noneMatch(s -> {
						try {
							return Files.isSameFile(s, p);
						} catch (IOException e) {
							return false;
						}
					})) {
						try {
							PngMetadata.validate(p);
							ScreenshotInfo info = new ScreenshotInfo(p);
							files.add(info);
						} catch (IOException e) {
							Niterucks.LOGGER.error("Failed to validate image: " + p + ", skipping!");
						}

					}
				});
				refreshPage();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		buttons.add(new ButtonWidget(0, width / 2 - 75, height - 35, 150, 20, "Back"));
		prev = new ButtonWidget(1, 2, 20, 20, 20, "<");
		buttons.add(prev);
		next = new ButtonWidget(2, width - 22, 20, 20, 20, ">");
		buttons.add(next);
	}

	private void refreshPage() {

		int fileCount = files.size();
		if (index != 0 && (fileCount-index) < count){
			index = Math.max(fileCount-count, 0);
		}

		int widgetWidth = 100;
		int widgetHeight = widgetWidth * 3 / 4;
		int padding = 5;
		int beginX = width / 2 - ((width / (widgetWidth + padding)) / 2 * (widgetWidth + padding)) + padding / 2;
		int beginY = 50;
		int x = beginX;
		int y = beginY;

		//System.out.println("Images per page: "+count);
		//System.out.println("Showing screenshots "+index+"-"+ Math.min((index)+count-1, files.size()-1));

		buttons.removeIf(b -> b instanceof ScreenshotWidget);

		for (int i = 0; i < Math.min(count, files.size() - index); i++) {
			ScreenshotInfo info = files.get(i + index);
			buttons.add(new ScreenshotWidget(info.getFile().hashCode(),
				x, y, widgetWidth, widgetHeight, info.getFile().getFileName().toString(), info));
			x += widgetWidth + padding;
			if (width - x <= beginX + widgetWidth) {
				y += widgetHeight + padding;
				x = beginX;
			}
			if (height - y <= widgetHeight + padding + 35) {
				break;
			}
		}

		prev.active = index > 0;
		next.active = files.size() - index > count;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		renderBackground();
		drawCenteredString(minecraft.textRenderer, "Screenshots", width / 2, 20, -1);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0) {
			files.forEach(ScreenshotInfo::release);
			files.clear();
			buttons.clear();
			System.gc();
			minecraft.openScreen(parent);
			return;
		}
		if (button.id == 1) {
			index = Math.max(0, index - count);
			refreshPage();
			return;
		}
		if (button.id == 2) {
			index = Math.min(count + index, files.size() - 1);
			refreshPage();
			return;
		}
		for (ScreenshotInfo p : files) {
			if (p.getFile().hashCode() == button.id) {
				minecraft.openScreen(new ScreenshotViewerScreen(this, p));
				break;
			}
		}
	}

	public static class ScreenshotInfo {
		private PngMetadata metadata;
		private int glId = -1;
		private int thumbGlId = -1;
		private final Path file;

		public ScreenshotInfo(Path file) {
			this.file = file;
		}

		private BufferedImage getImage() {
			try {
				BufferedImage image = TextureUtil.readImage(Files.newInputStream(getFile()));
				if (image == null) {
					BufferedImage error = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Graphics2D graphics = error.createGraphics();
					graphics.drawString("Error", 1, 15);
					graphics.dispose();
					return error;
				}
				return image;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private BufferedImage generateThumb(){

			Path cache = getThumbFile();
			if (Files.exists(cache)){
				try {
					return ImageIO.read(Files.newInputStream(cache));
				} catch (IOException e) {
					Niterucks.LOGGER.warn("Failed to read cached thumbnail file, regenerating!");
				}
			}

			BufferedImage image = getImage();
			int thumbWidth = Math.min(128, image.getWidth());
			int thumbHeight = (int) Math.min(128, (thumbWidth/(float)image.getWidth())*image.getHeight());
			Image i = image.getScaledInstance(thumbWidth, thumbHeight, BufferedImage.SCALE_SMOOTH);
			BufferedImage scaled = new BufferedImage(thumbWidth, thumbHeight, image.getType());
			Graphics2D graphics = scaled.createGraphics();
			graphics.drawImage(i, 0, 0, null);
			graphics.dispose();

			try {
				ImageIO.write(scaled, "png", Files.newOutputStream(cache));
			} catch (IOException e) {
				Niterucks.LOGGER.error("Failed to write thumbnail cache for "+getFile()+"!");
				e.printStackTrace();
			}

			return scaled;
		}

		private void readMetadata() {
			if (metadata == null) {
				try (InputStream in = Files.newInputStream(getFile())) {
					metadata = PngMetadata.read(in);
				} catch (IOException e) {
					Niterucks.LOGGER.error(e.getMessage());
				}
			}
		}

		public int getWidth() {
			readMetadata();
			return metadata.width();
		}

		public int getHeight() {
			readMetadata();
			return metadata.height();
		}

		public int getGlId() {
			if (glId == -1) {
				glId = TextureUtil.genTextures();
				TextureUtil.uploadTexture(glId, getImage());
			}
			return glId;
		}

		public int getThumbGlId(){
			if (thumbGlId == -1){
				thumbGlId = TextureUtil.genTextures();
				TextureUtil.uploadTexture(thumbGlId, generateThumb());
			}
			return thumbGlId;
		}

		public Path getFile() {
			return file;
		}

		public void release() {
			if (glId != -1) {
				TextureUtil.deleteTextures(glId);
				glId = -1;
			}
		}

		private Path getThumbFile(){
			try {
				String hash = Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("MD5")
					.digest(Files.readAllBytes(getFile())));
				return createThumbnailDir().resolve(hash);
			} catch (NoSuchAlgorithmException | IOException e) {
				throw new RuntimeException(e);
			}
		}

		private Path createThumbnailDir(){
			Path dir = FabricLoader.getInstance().getGameDir()
				.resolve(".cache")
				.resolve("niterucks")
				.resolve("thumbnails");
			try {
				Files.createDirectories(dir);
				return dir;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
