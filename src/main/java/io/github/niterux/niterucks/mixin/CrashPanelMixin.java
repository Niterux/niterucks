package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.crash.CrashPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Mixin(CrashPanel.class)
public class CrashPanelMixin extends Panel {
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/client/crash/CrashSummary;)V", at = @At(value = "CONSTANT", args = "intValue=3028036"))
	private int modifyCrashBGColor(int constant) {
		return 0x633534;
	}

	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/client/crash/CrashSummary;)V", at =
	@At(value = "CONSTANT", args = "stringValue=If you wish to report this, please copy this entire text and email it to support@mojang.com.\n"))
	private String correctReport(String constant) {
		return "Report this by uploading your entire log at https://github.com/Niterux/niterucks/issues\n";
	}
}
