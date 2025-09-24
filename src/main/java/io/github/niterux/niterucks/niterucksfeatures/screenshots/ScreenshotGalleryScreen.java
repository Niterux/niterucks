package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.invokers.FillInvoker;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ScreenshotGalleryScreen extends Screen {
	private static final Path screenshotsDir = FabricLoader.getInstance().getGameDir().resolve("screenshots");
	private static final ReferenceArrayList<ScreenshotInfo> files = new ReferenceArrayList<>();
	private static final ThreadPoolExecutor thumbnailImageGetter = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

	private final Screen parent;
	private final ReferenceArrayList<ScreenshotWidget> screenshotWidgets = new ReferenceArrayList<>();
	private final ReferenceArrayList<Future<?>> thumbnailImageGetterFuturesReferences = new ReferenceArrayList<>();
	private int index = 0;
	private int count;
	private ButtonWidget prev, next;
	private boolean finishedFillingFiles = false;
	private boolean initialized = false;

	public ScreenshotGalleryScreen(Screen parent) {
		files.clear();
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
				Niterucks.LOGGER.error("Failed to get a list of files in the screenshot directory!", e);
				throw new RuntimeException(e);
			}
			finishedFillingFiles = true;
			if (initialized)
				refreshPage();
		});
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
		cleanUpScreenshotGalleryPage();
		thumbnailImageGetterFuturesReferences.clear();
		buttons.removeAll(screenshotWidgets);
		screenshotWidgets.clear();

		int widgetCount = Math.min(count, fileCount - index);
		ScreenshotInfo[] applicableScreenshotInfoObjects = new ScreenshotInfo[widgetCount];
		//noinspection SuspiciousSystemArraycopy
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

	private void cleanUpScreenshotGalleryPage() {
		thumbnailImageGetter.getQueue().clear();
		for (ScreenshotWidget screenshotWidget : screenshotWidgets) {
			screenshotWidget.clearBufferedImage();
			int glId = screenshotWidget.getGlId();
			if (glId > -1)
				GL11.glDeleteTextures(glId);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		for (int i = 0; i < thumbnailImageGetterFuturesReferences.size(); i++) {
			Future<?> future = thumbnailImageGetterFuturesReferences.get(i);
			if (future == null || !future.isDone())
				continue;
			try {
				BufferedImage image = (BufferedImage) future.get();
				int thumbGlId = TextureUtil.putBufferedImageIntoGlId(image);
				screenshotWidgets.get(i).onResolvedImage(thumbGlId, (double) image.getWidth() / image.getHeight(), image.getWidth(), image.getHeight());
				thumbnailImageGetterFuturesReferences.set(i, null);
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		renderBackground();
		drawCenteredString(minecraft.textRenderer, "Screenshots", width / 2, 20, -1);
		super.render(mouseX, mouseY, tickDelta);
		for (ScreenshotWidget screenshotWidget : screenshotWidgets) {
			if (screenshotWidget.isHovered())
				MiscUtils.renderTooltip(screenshotWidget.message, mouseX, mouseY, width, 3, (FillInvoker) this);
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 0:
				cleanUpScreenshotGalleryPage();
				minecraft.openScreen(parent);
				return;
			case 1:
				index = Math.max(0, index - count);
				refreshPage();
				return;
			case 2:
				index = Math.min(count + index, files.size() - 1);
				refreshPage();
				return;
			case 3:
				Sys.openURL("file://" + new File(String.valueOf(FabricLoader.getInstance().getGameDir()), "screenshots").getAbsolutePath());
				return;
			default:
				for (ScreenshotInfo p : files) {
					if (p.getImagePath().hashCode() != button.id)
						continue;
					cleanUpScreenshotGalleryPage();
					minecraft.openScreen(new ScreenshotViewerScreen(this, p));
					return;
				}
		}
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

	@Override
	public void handleKeyboard() {
		if (Keyboard.getEventKey() == 1) {
			cleanUpScreenshotGalleryPage();
			this.minecraft.openScreen(null);
			this.minecraft.closeScreen();
			return;
		}
		super.handleKeyboard();
	}


}
