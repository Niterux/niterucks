package io.github.niterux.niterucks.mixin.accessors;

import dev.kdrag0n.colorkt.ucs.lch.Oklch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Oklch.class)
public interface HueAccessor {
	@Accessor(value = "hue", remap = false)
	void setHue(double hue);
}
