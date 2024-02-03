package io.github.niterux.niterucks.mixin.chatfixes;

import net.minecraft.client.entity.living.player.LocalPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.ChatUtils.putLocalHistoryMessage;

@Mixin(LocalPlayerEntity.class)
public class LocalPlayerEntityMixin {
	@Inject(method = "sendChat(Ljava/lang/String;)V", at = @At("HEAD"))
	private void pushNewLocalHistoryMessage(String message, CallbackInfo ci) {
		putLocalHistoryMessage(message);
	}
}
