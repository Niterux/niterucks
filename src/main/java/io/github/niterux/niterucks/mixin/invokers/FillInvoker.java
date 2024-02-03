package io.github.niterux.niterucks.mixin.invokers;

import net.minecraft.client.gui.GuiElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiElement.class)
public interface FillInvoker {
	@Invoker("fill")
	void invokeFill(int y1, int x2, int y2, int color, int i);
}
