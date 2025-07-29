package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ScreenshotGalleryScreen extends Screen {

	private static final Path screenshotsDir = FabricLoader.getInstance().getGameDir().resolve("screenshots");
	private static final List<ScreenshotInfo> files = new ArrayList<>();
	private final Screen parent;
	private final ArrayList<ScreenshotWidget> screenshotWidgets = new ArrayList<>();
	private final ArrayList<Future<?>> thumbnailImageGetterFuturesReferences = new ArrayList<>();
	private int index = 0;
	private int count;
	private ButtonWidget prev, next;
	private boolean finishedFillingFiles = false;
	private boolean initialized = false;
	private ExecutorService thumbnailImageGetter;

	public ScreenshotGalleryScreen(Screen parent) {
		files.clear();
		setThreadPool();
		this.parent = parent;
		CompletableFuture.runAsync(() -> {
			try (Stream<Path> paths = Files.list(screenshotsDir)) {
				paths
					.filter(FastScreenshotUtils::isCompatiblePath).
					sorted(Comparator.reverseOrder())
					.forEachOrdered(p -> {
						ScreenshotInfo info = new ScreenshotInfo(p);
						files.add(info);
					});
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			finishedFillingFiles = true;
			if (initialized)
				refreshPage();
		});
	}

	@Override
	public void init() {
		int widgetWidth = 100;
		int widgetHeight = widgetWidth * 3 / 4;
		int padding = 5;
		count = (width / (widgetWidth + padding)) * ((height - 85) / (widgetHeight + padding));
		prev = new ButtonWidget(1, 2, 20, 20, 20, "<");
		next = new ButtonWidget(2, width - 22, 20, 20, 20, ">");
		buttons.add(new ButtonWidget(0, this.width / 2 + 4, this.height - 35, 150, 20, "Back"));
		buttons.add(new ButtonWidget(3, this.width / 2 - 154, this.height - 35, 150, 20, "Open screenshots folder"));
		buttons.add(prev);
		buttons.add(next);
		initialized = true;
		if (finishedFillingFiles)
			refreshPage();
	}

	private synchronized void refreshPage() {
		int fileCount = files.size();
		int widgetWidth = 100;
		int widgetHeight = widgetWidth * 3 / 4;
		int padding = 5;
		int beginX = width / 2 - ((width / (widgetWidth + padding)) / 2 * (widgetWidth + padding)) + padding / 2;
		int beginY = 50;
		int x = beginX;
		int y = beginY;
		thumbnailImageGetter.shutdownNow();
		setThreadPool();
		for (ScreenshotWidget screenshotWidget : screenshotWidgets) {
			screenshotWidget.clearBufferedImage();
			int glId = screenshotWidget.getGlId();
			if (glId > -1)
				GL11.glDeleteTextures(glId);
		}
		thumbnailImageGetterFuturesReferences.clear();
		buttons.removeAll(screenshotWidgets);
		screenshotWidgets.clear();

		int widgetCount = Math.min(count, fileCount - index);
		ScreenshotInfo[] applicableScreenshotInfoObjects = new ScreenshotInfo[widgetCount];
		System.arraycopy(files.toArray(), index, applicableScreenshotInfoObjects, 0, widgetCount);

		for (int i = 0; i < widgetCount; i++) {
			ScreenshotWidget screenshotWidget = new ScreenshotWidget(applicableScreenshotInfoObjects[i].getImagePath().hashCode(),
				x, y, widgetWidth, widgetHeight, applicableScreenshotInfoObjects[i].getImagePath().getFileName().toString(), applicableScreenshotInfoObjects[i]);
			buttons.add(screenshotWidget);

			screenshotWidgets.add(screenshotWidget);
			int finalI = i;
			thumbnailImageGetterFuturesReferences.add(thumbnailImageGetter.submit(() -> FastScreenshotUtils.getThumbnail(applicableScreenshotInfoObjects[finalI])));
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
		next.active = fileCount > count + index;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		for (int i = 0; i < thumbnailImageGetterFuturesReferences.size(); i++) {
			Future<?> future = thumbnailImageGetterFuturesReferences.get(i);
			if (future != null && future.isDone()) {
				try {
					BufferedImage image = (BufferedImage) future.get();
					int thumbGlId = GL11.glGenTextures();
					TextureUtil.putBufferedImageIntoGlId(thumbGlId, image);
					screenshotWidgets.get(i).onResolvedImage(thumbGlId, (double) image.getWidth() / image.getHeight(), image.getWidth(), image.getHeight());
					thumbnailImageGetterFuturesReferences.set(i, null);
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		}
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
		if (button.id == 3) {
			Sys.openURL("file://" + new File(String.valueOf(FabricLoader.getInstance().getGameDir()), "screenshots").getAbsolutePath());
		}
		for (ScreenshotInfo p : files) {
			if (p.getImagePath().hashCode() == button.id) {
				minecraft.openScreen(new ScreenshotViewerScreen(this, p));
				break;
			}
		}
	}

	private void setThreadPool() {
		thumbnailImageGetter = Executors.newFixedThreadPool(4);
	}
}
