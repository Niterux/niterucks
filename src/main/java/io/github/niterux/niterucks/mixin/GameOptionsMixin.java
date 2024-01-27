package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Debug(export = true)
@Mixin(value = GameOptions.class, priority = 9999)
public class GameOptionsMixin {
	@Shadow
	public int guiScale;
	@Shadow
	protected Minecraft minecraft;

	@ModifyArg(
		method = "translateValue(Lnet/minecraft/client/options/GameOptions$Option;)Ljava/lang/String;",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/client/options/GameOptions;GUI_SCALE_SETTINGS:[Ljava/lang/String;",
				opcode = Opcodes.GETSTATIC,
				shift = At.Shift.BEFORE
			)),
		at = @At(value = "INVOKE", target = "Lnet/minecraft/locale/LanguageManager;translate(Ljava/lang/String;)Ljava/lang/String;", ordinal = 0)
	)
	private String fixGuiScaleTranslate(String key) {
		if (guiScale == 0)
			return "options.guiScale.auto";
		return String.valueOf(guiScale) + 'x';
	}

	@WrapOperation(method = "setValue(Lnet/minecraft/client/options/GameOptions$Option;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;guiScale:I", ordinal = 0, opcode = Opcodes.PUTFIELD))
	private void fixMaxGuiScale(GameOptions instance, int value, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) int increment) {
		original.call(instance, (instance.guiScale + increment) % (Math.min(minecraft.width / 320, minecraft.height / 240)));
	}

	@ModifyExpressionValue(method = "translateValue(Lnet/minecraft/client/options/GameOptions$Option;)Ljava/lang/String;", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;guiScale:I"))
	private int avoidGuiScaleCrash(int original) {
		return 0;
	}
}
