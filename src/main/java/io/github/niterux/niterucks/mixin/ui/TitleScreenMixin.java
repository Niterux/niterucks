package io.github.niterux.niterucks.mixin.ui;

import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Calendar;
import java.util.Date;

import static io.github.niterux.niterucks.Niterucks.modVersion;

@Mixin(value = TitleScreen.class, priority = 2000)
public class TitleScreenMixin extends Screen {
	@Shadow
	private String splashText;

	@Inject(method = "<init>()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;splashText:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, ordinal = 1))
	private void splashInject(CallbackInfo info) {
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());
		int month = currentDate.get(Calendar.MONTH);
		int day = currentDate.get(Calendar.DAY_OF_MONTH);
		if (month == Calendar.DECEMBER && day == 14) {
			splashText = "Happy birthday, Niterux!";
		}
		if (Math.random() < 0.083) {
			splashText = "Trans rights!";
		}
		if (month == Calendar.NOVEMBER && day >= 13 && day <= 20) {
			splashText = MiscUtils.textGradientGenerator("Trans lives matter!", new char[]{'b', 'd', 'f', 'd', 'b'}, this.textRenderer);
		}
		/*
		I changed this from 100% of the time in november to 8% of the times you start the game
		because I felt seeing this every day of november might get repetitive, the chance that this
		message appears throughout the year is the same since november is 8% of the year
		if (month == Calendar.NOVEMBER) {
			splashText = "Trans Rights!";
		}*/
	}

	@Inject(method = "render(IIF)V", at = @At("TAIL"))
	private void vdf(CallbackInfo ci) {
		this.drawString(this.textRenderer, "Niterucks Client " + modVersion, 2, this.height - 10, 0x5336A2);
	}
}
