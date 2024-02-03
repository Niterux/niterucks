package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface lastChatMessageAccessor {
	@Accessor("lastChatMessage")
	void setLastChatMessage(String lastChatMessage);
}
