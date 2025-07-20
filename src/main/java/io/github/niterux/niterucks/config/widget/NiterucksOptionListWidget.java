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
import net.minecraft.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class NiterucksOptionListWidget extends VanillaButtonListWidget {
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
