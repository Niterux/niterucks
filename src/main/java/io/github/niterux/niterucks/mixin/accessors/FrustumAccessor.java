package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.client.render.Frustum;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Frustum.class)
public interface FrustumAccessor {
	@NotNull
	@Accessor("INSTANCE")
	static Frustum getInstanceNoCompute() {
		return new Frustum();
	}
}
