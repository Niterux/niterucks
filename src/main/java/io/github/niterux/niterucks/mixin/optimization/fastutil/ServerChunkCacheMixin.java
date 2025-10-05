package io.github.niterux.niterucks.mixin.optimization.fastutil;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.world.chunk.ServerChunkCache;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
	@Shadow
	private List<WorldChunk> chunks = new ReferenceArrayList<>();
}
