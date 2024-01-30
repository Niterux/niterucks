package io.github.niterux.niterucks.mixin.invokers;

import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Dimension.class)
public interface InitBrightnessTableInvoker {
	@Invoker("initBrightnessTable")
	void reinitBrightnessTable();
}
