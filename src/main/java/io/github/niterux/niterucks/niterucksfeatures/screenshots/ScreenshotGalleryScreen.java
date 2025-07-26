package io.github.niterux.niterucks.niterucksfeatures.screenshots;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.Sys;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ScreenshotGalleryScreen extends Screen {

	private static final Path screenshotsDir = FabricLoader.getInstance().getGameDir().resolve("screenshots");
	private static final List<ScreenshotInfo> files = new ArrayList<>();
	private final Screen parent;
	private int index = 0;
	private int count;
	private ButtonWidget prev, next;
	private boolean finishedFillingFiles = false;
	private boolean initialized = false;


	public ScreenshotGalleryScreen(Screen parent) {
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
		if (index != 0 && (fileCount - index) < count) {
			index = Math.max(fileCount - count, 0);
		}

		int widgetWidth = 100;
		int widgetHeight = widgetWidth * 3 / 4;
		int padding = 5;
		int beginX = width / 2 - ((width / (widgetWidth + padding)) / 2 * (widgetWidth + padding)) + padding / 2;
		int beginY = 50;
		int x = beginX;
		int y = beginY;

		for(ButtonWidget button : buttons) {
			if (button instanceof ScreenshotWidget screenshotWidget) {
				screenshotWidget.clearBufferedImage();
			}
		}
		buttons.removeIf(b -> b instanceof ScreenshotWidget);

		for (int i = 0; i < Math.min(count, files.size() - index); i++) {
			ScreenshotInfo info = files.get(i + index);
			buttons.add(new ScreenshotWidget(info.getImagePath().hashCode(),
				x, y, widgetWidth, widgetHeight, info.getImagePath().getFileName().toString(), info));
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


}
