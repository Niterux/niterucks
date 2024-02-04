package io.github.niterux.niterucks.mixin.chatfixes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.niterux.niterucks.niterucksfeatures.ChatScreenGetMessageScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;

@Mixin(GameGui.class)
public class GameGuiMixin {
	@Shadow
	private List chatMessages;
	@Shadow
	private Minecraft minecraft;
	@Unique
	private int messageScrollTotal = 0;

	@ModifyExpressionValue(method = "addChatMessage(Ljava/lang/String;)V", at = @At(value = "CONSTANT", args = "intValue=50"))
	private int modifyMaxChatMessages(int original) {
		messageScrollTotal++;
		return 1000;
	}

	@ModifyExpressionValue(method = "Lnet/minecraft/client/gui/GameGui;render(FZII)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screen/Screen;")))
	private int onlyRender50(int original) {
		return Math.min(original, 50);
	}

	@ModifyArg(method = "Lnet/minecraft/client/gui/GameGui;render(FZII)V", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 2), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screen/Screen;")))
	private int getHigherMessages(int index) {
		if (this.minecraft.screen instanceof ChatScreen && chatMessages.size() > 20) {
			int scroll = Mouse.getDWheel();
			if (scroll != 0) {
				messageScrollTotal += (int) (Math.signum(scroll) * 1);
			}
			if (messageScrollTotal < 0)
				messageScrollTotal = 0;
			if (messageScrollTotal > chatMessages.size() - 20)
				messageScrollTotal = chatMessages.size() - 20;
			return index + messageScrollTotal;
		} else {
			messageScrollTotal = 0;
		}
		return index;
	}

}
