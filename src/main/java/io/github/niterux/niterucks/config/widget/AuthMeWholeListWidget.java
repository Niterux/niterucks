package io.github.niterux.niterucks.config.widget;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.ElementListWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil;
import io.github.niterux.niterucks.config.option.AuthMeWholeListOption;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthMeWholeListWidget extends ClickableWidget {
	public AuthMeWholeListOption option;


	public AuthMeWholeListWidget(int x, int y, int width, int height, String message) {
		super(x, y, width, height, message);
	}
	public AuthMeWholeListWidget(int x, int y, int width, int height, AuthMeWholeListOption message) {
		super(x, y, width, height, "");
		option = message;
	}
}
