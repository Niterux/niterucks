package io.github.niterux.niterucks.mixin.invokers;

import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Dimension.class)
public interface initBrightnessTableInvoker {
	@Invoker("initBrightnessTable")
    void reinitBrightnessTable();
}
