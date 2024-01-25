package io.github.niterux.niterucks.mixin.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftInstanceAccessor {
	@Accessor("INSTANCE")
	static Minecraft getMinecraft() {throw new AssertionError();}
}
