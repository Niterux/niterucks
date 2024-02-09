package io.github.niterux.niterucks.config.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.options.WidgetIdentifieable;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.MathUtil;
import net.minecraft.resource.Identifier;
import net.minecraft.resource.language.I18n;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class NiterucksCategoryListWidget extends VanillaButtonListWidget {
	private static final Identifier BACKGROUND_TEXTURE = new Identifier("/gui/background.png");
	private OptionCategory current;
	private static double scroll;

	public NiterucksCategoryListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
		centerListVertically = true;
		setScrollAmount(scroll);
	}

	@Override
	public void addEntry(OptionCategory first, @Nullable OptionCategory second) {
		addEntry(createCategoryEntry(createWidget(10, first), first,
			second == null ? null : createWidget(width / 2 + WIDGET_ROW_RIGHT, second), second));
	}

	@Override
	public void addEntries(ConfigManager manager, OptionCategory category) {
		this.current = category;
		Collection<OptionCategory> categories = new ArrayList<>();
		addCategories(categories, manager.getRoot());
		addCategories(manager, categories);
	}

	private void addCategories(Collection<OptionCategory> collection, OptionCategory current) {
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
		ClickableWidget widget = ConfigStyles.createWidget(x, 0, width - 20, itemHeight - 5, id);
		if (widget.getMessage().equals(I18n.translate(current.getName()))) {
			widget.active = false;
		}
		return widget;
	}

	@Override
	protected void renderBackground() {
		if (client.world == null) {
			GlStateManager.disableLighting();
			GL11.glDisable(GL11.GL_FOG);
			BufferBuilder builder = BufferBuilder.INSTANCE;
			DrawUtil.bindTexture(BACKGROUND_TEXTURE);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			float textureWidth = 32.0F;
			builder.start();
			builder.color(0x202020);
			builder.vertex(this.left, this.bottom, 0.0, (this.left / textureWidth), ((float) (this.bottom + (int) this.getScrollAmount()) / textureWidth));
			builder.vertex(this.right, this.bottom, 0.0, ((float) this.right / textureWidth), ((float) (this.bottom + (int) this.getScrollAmount()) / textureWidth));
			builder.vertex(this.right, this.top, 0.0, ((float) this.right / textureWidth), ((float) (this.top + (int) this.getScrollAmount()) / textureWidth));
			builder.vertex(this.left, this.top, 0.0, ((float) this.left / textureWidth), ((float) (this.top + (int) this.getScrollAmount()) / textureWidth));
			builder.end();
		}
	}

	@Override
	protected int getScrollbarPositionX() {
		return width - 6;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		scroll = MathUtil.clamp(this.getScrollAmount() - amountY * (double) this.itemHeight / 2.0, 0, getMaxScroll());
		return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
	}
}
