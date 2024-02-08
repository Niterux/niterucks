package io.github.niterux.niterucks.config.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import net.minecraft.resource.language.I18n;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class NiterucksCategoryListWidget extends VanillaButtonListWidget {
	private OptionCategory current;
	public NiterucksCategoryListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
		centerListVertically = true;
	}

	@Override
	public void addEntry(OptionCategory first, @Nullable OptionCategory second) {
		addEntry(createCategoryEntry(createWidget(10, first), first,
			second == null ? null : createWidget(width / 2 + WIDGET_ROW_RIGHT, second), second));
	}

	@Override
	protected void addOptions(ConfigManager manager, Collection<Option<?>> options) {

	}

	@Override
	public void addEntries(ConfigManager manager, OptionCategory category) {
		this.current = category;
		Collection<OptionCategory> categories = new ArrayList<>();
		addCategories(categories, manager.getRoot());
		addCategories(manager, categories);
	}

	private void addCategories(Collection<OptionCategory> collection, OptionCategory current){
		collection.add(current);
		current.getSubCategories().forEach(c -> addCategories(collection, c));
	}

	@Override
	protected void addCategories(ConfigManager manager, Collection<OptionCategory> categories) {
		List<OptionCategory> list = categories.stream()
			.filter(c -> !manager.getSuppressedNames().contains(c.getName())).collect(Collectors.toList());
		list.forEach(l -> addEntry(l, null));
	}

	protected ClickableWidget createWidget(int x, WidgetIdentifieable id) {
		ClickableWidget widget = ConfigStyles.createWidget(x, 0, width-20, itemHeight - 5, id);
		if (widget.getMessage().equals(I18n.translate(current.getName()))){
			widget.active = false;
		}
		return widget;
	}

	@Override
	protected void renderBackground() {
		GL11.glDisable(2896);
		GL11.glDisable(2912);
		BufferBuilder var16 = BufferBuilder.INSTANCE;
		GL11.glBindTexture(3553, client.textureManager.load("/gui/background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var17 = 32.0F;
		var16.start();
		var16.color(2105376);
		var16.vertex(left, bottom, 0.0, (left / var17), ((float)(bottom + (int) this.getScrollAmount()) / var17));
		var16.vertex(this.right, this.bottom, 0.0, ((float)this.right / var17), ((float)(this.bottom + (int) this.getScrollAmount()) / var17));
		var16.vertex(this.right, this.top, 0.0, ((float)this.right / var17), ((float)(this.top + (int) this.getScrollAmount()) / var17));
		var16.vertex(this.left, this.top, 0.0, ((float)this.left / var17), ((float)(this.top + (int) this.getScrollAmount()) / var17));
		var16.end();
	}
}
