package io.github.niterux.niterucks.mixin.optimization.signs;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.SignBlockEntityInterface;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	public List<BlockEntity> globalBlockEntities = new ReferenceArrayList<>();

	@Inject(method = "renderEntities", at = @At("TAIL"))
	private void removeOldSignDrawLists(Vec3d pos, Culler culler, float tickDelta, CallbackInfo ci) {
		var iterator = Niterucks.SIGN_DRAWLIST_OBJECT_CACHE_LIST.iterator();
		while (iterator.hasNext()) {
			var signInterface = (SignBlockEntityInterface) iterator.next();
			if (signInterface.niterucks$getRenderCheck()) {
				signInterface.niterucks$setRenderCheck(false);
				continue;
			}
			signInterface.niterucks$releaseList();
			iterator.remove();
		}
	}
}
