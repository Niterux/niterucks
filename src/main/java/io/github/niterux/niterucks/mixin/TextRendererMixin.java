package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.render.TextRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.nio.IntBuffer;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
	@Shadow
	private int boundPage;

	@Unique
	float[][] chatColors = new float[32][3];

	@ModifyArg(index = 0, method = "<init>(Lnet/minecraft/client/options/GameOptions;Ljava/lang/String;Lnet/minecraft/client/render/texture/TextureManager;)V",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V", ordinal = 1, remap = false))
	private int captureChatColorIndex(int index, @Share("index") LocalIntRef indexRef){
		indexRef.set(index - 256 - this.boundPage);
		return index;
	}
	@ModifyArgs(method = "<init>(Lnet/minecraft/client/options/GameOptions;Ljava/lang/String;Lnet/minecraft/client/render/texture/TextureManager;)V",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V", remap = false))
	private void captureChatColors(Args args, @Share("index") LocalIntRef indexRef){
		chatColors[indexRef.get()][0] = args.get(0);
		chatColors[indexRef.get()][1] = args.get(1);
		chatColors[indexRef.get()][2] = args.get(2);
	}
	@Redirect(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;put(I)Ljava/nio/IntBuffer;", ordinal = 0))
	private IntBuffer modifyDrawColor(IntBuffer instance, int i, @Local(ordinal = 2, argsOnly = true) int color){
		int colorIndex = i - this.boundPage - 256;
		float alpha = (float)(color >> 24 & 0xFF) / 255.0F;
		if (alpha == 0.0F) {
			alpha = 1.0F;
		}
		GL11.glColor4f(chatColors[colorIndex][0],chatColors[colorIndex][1],chatColors[colorIndex][2], alpha);
		return instance;
	}
	@Redirect(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;put(I)Ljava/nio/IntBuffer;", ordinal = 1))
	private IntBuffer manuallyDrawChar(IntBuffer instance, int i){
		GL11.glCallList(i);
		return instance;
	}
}
