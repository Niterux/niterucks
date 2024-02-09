package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import net.minecraft.client.render.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
	@Inject(method = "reload()V", at = @At("TAIL"))
	private void reloadChunks(CallbackInfo ci) {
		MinecraftInstanceAccessor.getMinecraft().worldRenderer.m_6748042();
	}
}
