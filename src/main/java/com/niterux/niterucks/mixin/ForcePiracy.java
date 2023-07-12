//force the anti piracy message to appear for lulz
package com.niterux.niterucks.mixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public class ForcePiracy {
	@Shadow
	public static long f_4942690 = 1L;
}
