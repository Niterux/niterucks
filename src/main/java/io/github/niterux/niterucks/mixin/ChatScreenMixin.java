package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screen.ChatScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;"))
	private String coloredChatTextPreview(String original) {
		String modifiedString = original;
		for (int var12 = 0; modifiedString.length() - 1 > var12; var12++) {
			if (modifiedString.charAt(var12) == '&') {
				modifiedString = modifiedString.substring(0, var12) + "§" + modifiedString.charAt(var12 + 1) + modifiedString.substring(var12);
				var12 += 2;
			}
		}
		return modifiedString;
	}
}
