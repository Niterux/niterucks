package io.github.niterux.niterucks.config.widget;

import com.google.common.collect.Lists;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ClickableWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.ButtonListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.MathUtil;
import io.github.niterux.niterucks.config.optionstorage.AuthMeIPUsernamePassword;
import io.github.niterux.niterucks.config.optionstorage.AuthMeWholeListOptionStorage;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;

import java.util.Collection;

public class AuthMeListWidget extends ButtonListWidget {
	private static double scroll;

	public AuthMeListWidget(ConfigManager manager, OptionCategory category, int screenWidth, int screenHeight, int top, int bottom, int entryHeight) {
		super(manager, category, screenWidth, screenHeight, top, bottom, entryHeight);
		setRenderBackground(false);
		setRenderHeader(false, headerHeight);
		setRenderHorizontalShadows(false);
		centerListVertically = true;
		setScrollAmount(scroll);
		setLeftPos(width / 4 + 10);
	}

	@Override
	public void addEntries(ConfigManager manager, OptionCategory category) {
		setLeftPos(width / 4 + 10);
		AuthMeWholeListOptionStorage.getInstance().entries.forEach(e -> addEntry(Entry.get(getRowLeft(), e)));
		addEntry(new ActionsEntry(getRowLeft()));
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		scroll = MathUtil.clamp(this.getScrollAmount() - amountY * (double) this.itemHeight / 2.0, 0, getMaxScroll());
		return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		children().forEach(e -> e.children().forEach(el -> el.setFocused(false)));
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public static class Entry extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.ButtonListWidget.Entry {

		private Entry(Collection<ClickableWidget> widgets) {
			super(widgets);
		}

		public static Entry get(int left) {
			AuthMeIPUsernamePassword newEntry = new AuthMeIPUsernamePassword();
			AuthMeWholeListOptionStorage.getInstance().entries.add(newEntry);
			return get(left, newEntry);
		}

		public static Entry get(int left, AuthMeIPUsernamePassword data) {
			left += 1;
			var textRenderer = MinecraftInstanceAccessor.getMinecraft().textRenderer;
			TextFieldWidget ip = new TextFieldWidget(textRenderer, left, 0, 75, 20, "");
			ip.setHint("IP Address");
			TextFieldWidget username = new TextFieldWidget(textRenderer, left + 79, 0, 75, 20, "");
			username.setHint("Username");
			TextFieldWidget password = new CensoredTextFieldWidget(textRenderer, left + 79 + 79, 0, 75, 20, "");
			password.setHint("Password");
			ip.setText(data.getIpAddress());
			username.setText(data.getUsername());
			password.setText(data.getPassword());
			ip.setChangedListener(data::setIpAddress);
			username.setChangedListener(data::setUsername);
			password.setChangedListener(data::setPassword);
			return new Entry(Lists.newArrayList(ip, username, password));
		}
	}

	public class ActionsEntry extends Entry {

		public ActionsEntry(int left) {
			super(Lists.newArrayList(new VanillaButtonWidget(left, 0, 20, 20, "+", btn -> {
				AuthMeListWidget.this.children().add(AuthMeListWidget.this.getEntryCount() - 1, Entry.get(left));
				AuthMeListWidget.this.setScrollAmount(AuthMeListWidget.this.getMaxScroll());
			}), new VanillaButtonWidget(left + 24, 0, 20, 20, "-", btn -> {
				if (AuthMeListWidget.this.getEntryCount() <= 1)
					return;
				AuthMeListWidget.this.remove(AuthMeListWidget.this.getEntryCount() - 2);
				AuthMeListWidget.this.setScrollAmount(AuthMeListWidget.this.getMaxScroll());
			})));
		}
	}
}
