package io.github.niterux.niterucks.mixin.optimization.fastutil;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.world.RenderChunk;
import net.minecraft.client.render.world.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	public List<BlockEntity> globalBlockEntities = new ReferenceArrayList<>();
	@Shadow
	private List<RenderChunk> pendingChunks = new ReferenceArrayList<>(256);
	@Shadow
	private List<RenderChunk> renderList = new ReferenceArrayList<>(256);
}
