package io.github.niterux.niterucks.mixin;

import net.minecraft.client.crash.CrashPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CrashPanel.class)
public class CrashPanelMixin {
	@ModifyConstant(method = "<init>(Lnet/minecraft/client/crash/CrashSummary;)V", constant = @Constant(intValue = 3028036))
	private int modifyCrashBGColor(int constant) {
		return 0x633534;
	}

	@ModifyConstant(method = "<init>(Lnet/minecraft/client/crash/CrashSummary;)V", constant =
	@Constant(stringValue = "If you wish to report this, please copy this entire text and email it to support@mojang.com.\n"))
	private String correctReport(String constant) {
		return "Report this by uploading your entire log at https://github.com/Niterux/niterucks/issues\n";
	}
}
