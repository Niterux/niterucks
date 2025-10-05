package io.github.niterux.niterucks.mixin.optimization.signs;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.SignBlockEntityInterface;
import net.minecraft.client.render.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
	@Inject(method = "reload()V", at = @At("TAIL"))
	private void releaseDrawLists(CallbackInfo ci) {
		var iterator = Niterucks.SIGN_DRAWLIST_OBJECT_CACHE_LIST.iterator();
		while (iterator.hasNext()) {
			((SignBlockEntityInterface) iterator.next()).niterucks$releaseList();
			iterator.remove();
		}
	}
}
