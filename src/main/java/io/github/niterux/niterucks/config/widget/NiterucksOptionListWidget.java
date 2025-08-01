package io.github.niterux.niterucks.config.widget;

import com.google.common.collect.ImmutableList;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.ResetButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.mixin.invokers.FillInvoker;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class NiterucksOptionListWidget extends VanillaButtonListWidget {
	final static int TOOLTIP_PADDING = 3;
	public NiterucksOptionListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
		setLeftPos(width / 4 + 10);
	}

	@Override
	protected void addCategories(ConfigManager manager, Collection<OptionCategory> categories) {

	}

	@Override
	protected Entry createOptionEntry(ClickableWidget widget, Option<?> option, @Nullable ClickableWidget other, @Nullable Option<?> otherOption) {
		return new NiterucksOptionEntry(widget, option);
	}

	@Override
	protected void renderDecorations(int mouseX, int mouseY) {
		if (!(this.getHoveredEntry() instanceof NiterucksOptionEntry niterucksOptionEntry)) {
			return;
		}
		var option = niterucksOptionEntry.option;
		String tooltip = I18n.translate(option.getTooltip());
		if (tooltip.equals(option.getName() + ".tooltip")) {
			return;
		}
		renderTooltip(tooltip, mouseX, mouseY);
	}

	private void renderTooltip(String tooltip, int mouseX, int mouseY) {
		TextRenderer textRenderer = MinecraftInstanceAccessor.getMinecraft().textRenderer;

		int tooltipWidth = 0, tooltipPositionX = mouseX + 9, tooltipPositionY;
		String[] tooltipLines = tooltip.split("<br>");
		tooltipPositionY = tooltipLines.length * -8 + mouseY - 7;
		for (String tooltipLine : tooltipLines)
			tooltipWidth = Math.max(textRenderer.getWidth(tooltipLine), tooltipWidth);
		tooltipWidth += TOOLTIP_PADDING * 2;
		tooltipPositionX = Math.min(width - tooltipWidth, tooltipPositionX);

		//noinspection SuspiciousNameCombination
		((FillInvoker) this).invokeFill(
			tooltipPositionX,
			tooltipPositionY,
			tooltipPositionX + tooltipWidth,
			tooltipLines.length * 8 + tooltipPositionY + (TOOLTIP_PADDING * 2),
			0xC0000000);
		for (int i = 0; i < tooltipLines.length; i++) {
			textRenderer.drawWithShadow(tooltipLines[i],
				tooltipPositionX + TOOLTIP_PADDING,
				i * 8 + tooltipPositionY + TOOLTIP_PADDING,
				0xFFFFFFFF);
		}
	}

	private class NiterucksOptionEntry extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.ButtonListWidget.Entry {
		private final Option<?> option;

		public NiterucksOptionEntry(ClickableWidget widget, Option<?> option) {
			super(ImmutableList.of(widget,
				new ResetButtonWidget<>(widget.getX() + widget.getWidth() - 40, 0, 40, widget.getHeight(), option)));
			widget.setWidth(widget.getWidth() - 42);
			this.option = option;
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			DrawUtil.drawScrollingText(I18n.translate(option.getName()), width / 4 + 10,
				y, width / 4 - 20, entryHeight, Colors.text());
		}
	}
}
