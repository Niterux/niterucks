package io.github.niterux.niterucks.mixin.ui;

import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import io.github.niterux.niterucks.niterucksfeatures.screenshots.ScreenshotGalleryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;
import java.time.Month;

import static io.github.niterux.niterucks.Niterucks.MOD_VERSION;

@Mixin(value = TitleScreen.class, priority = 2000)
public class TitleScreenMixin extends Screen {
	@Unique
	private static final int SCREENSHOT_ID = 12421;
	@Shadow
	private String splashText;

	@Inject(method = "<init>()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;splashText:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, ordinal = 1))
	private void splashInject(CallbackInfo info) {
		LocalDateTime currentDate = LocalDateTime.now();
		Month month = currentDate.getMonth();
		int day = currentDate.getDayOfMonth();
		if (month == Month.DECEMBER && day == 14)
			splashText = "Happy birthday, Niterux!";
		if (Math.random() < 0.083)
			splashText = "Trans rights!";
		if (month == Month.NOVEMBER && day >= 13 && day <= 20)
			splashText = MiscUtils.textGradientGenerator("Trans lives matter!", new char[]{'b', 'd', 'f', 'd', 'b'}, MinecraftInstanceAccessor.getMinecraft().textRenderer);
	}

	@Inject(method = "render(IIF)V", at = @At("TAIL"))
	private void drawNiterucksVersionText(CallbackInfo ci) {
		this.drawString(this.textRenderer, "Niterucks Client " + MOD_VERSION, 2, this.height - 10, 0x5336A2);
	}

	@Inject(method = "init()V", at = @At("TAIL"))
	private void addScreenshotButton(CallbackInfo ci) {
		buttons.add(new ButtonWidget(SCREENSHOT_ID, width - 100, height - 40, 80, 20, "Screenshots"));
	}

	@Inject(method = "buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("TAIL"))
	private void handleButtons(ButtonWidget button, CallbackInfo ci) {
		switch (button.id) {
			case SCREENSHOT_ID:
				minecraft.openScreen(new ScreenshotGalleryScreen(this));
		}

	}
}
