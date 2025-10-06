package io.github.niterux.niterucks.mixin.invokers;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface ForceReloadInvoker {
	@Invoker("forceReload")
	void invokeForceReload();
}
