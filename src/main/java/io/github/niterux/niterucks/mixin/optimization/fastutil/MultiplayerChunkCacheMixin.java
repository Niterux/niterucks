package io.github.niterux.niterucks.mixin.optimization.fastutil;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.world.chunk.MultiplayerChunkCache;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(MultiplayerChunkCache.class)
public class MultiplayerChunkCacheMixin {
	@Shadow
	private Map<ChunkPos, WorldChunk> chunksByPos = new Object2ReferenceOpenHashMap<>();
	@Shadow
	private List<WorldChunk> chunks = new ReferenceArrayList<>();

}
