package io.github.niterux.niterucks.mixin.optimization;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.LightUpdate;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;

@Mixin(World.class)
public class WorldMixin {
	@Shadow
	public List<Entity> entities = new ReferenceArrayList<>();
	@Shadow
	public List<BlockEntity> blockEntities = new ReferenceArrayList<>();
	@Shadow
	public List<PlayerEntity> players = new ReferenceArrayList<>();
	@Shadow
	public List<Entity> globalEntities = new ReferenceArrayList<>();
	@Shadow
	private List<LightUpdate> removedBlockEntities = new ReferenceArrayList<>();
	@Shadow
	private List<Entity> entitiesToRemove = new ReferenceArrayList<>();
	@Shadow
	private Set<ScheduledTick> scheduledTicks = new ObjectOpenHashSet<>();
	@Shadow
	private List<BlockEntity> pendingBlockEntities = new ReferenceArrayList<>();
	@Shadow
	private Set<ChunkPos> tickingChunks = new ObjectOpenHashSet<>();
	@Shadow
	private List<Entity> entitiesInArea = new ReferenceArrayList<>();
}
