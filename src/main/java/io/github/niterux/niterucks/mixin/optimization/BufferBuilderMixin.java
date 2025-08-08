package io.github.niterux.niterucks.mixin.optimization;

import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.Niterucks;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {
	@Shadow
	private boolean f_4060651;

	@Inject(method = "<init>", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;f_4060651:Z", opcode = Opcodes.GETFIELD))
	private void log(int size, CallbackInfo ci) {
		f_4060651 = Niterucks.CONFIG.enableVBO.get();
		if (f_4060651)
			Niterucks.LOGGER.info("Enabled VBOs!");
	}
}
