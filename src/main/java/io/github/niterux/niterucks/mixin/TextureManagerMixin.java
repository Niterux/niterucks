package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.SignBlockEntityInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
	@Inject(method = "reload()V", at = @At("TAIL"))
	private void reloadChunks(CallbackInfo ci) {
		Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
		if (minecraft.worldRenderer != null)
			minecraft.worldRenderer.m_6748042();
		// Avoid drawlists from the previous font from being used.
		var iterator = Niterucks.SIGN_DRAWLIST_OBJECT_CACHE_LIST.iterator();
		while (iterator.hasNext()) {
			((SignBlockEntityInterface) iterator.next()).niterucks$releaseList();
			iterator.remove();
		}
	}
}
