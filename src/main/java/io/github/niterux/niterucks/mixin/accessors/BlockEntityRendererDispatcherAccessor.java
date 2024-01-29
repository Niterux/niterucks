package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererDispatcherAccessor {
	@Accessor("dispatcher")
	BlockEntityRenderDispatcher getRenderDispatcher();
}
