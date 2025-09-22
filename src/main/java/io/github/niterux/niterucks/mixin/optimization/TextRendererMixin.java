package io.github.niterux.niterucks.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.client.render.TextRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.nio.Buffer;
import java.nio.IntBuffer;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
	// 4 vertices per char with 4 variables each
	@Unique
	private final DoubleArrayList vertices = new DoubleArrayList(256 * 4 * 4);
	@Unique
	float[][] chatColors = new float[32][3];
	@Shadow
	private int boundPage;

	@Shadow
	private int[] characterWidths;

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;vertex(DDDDD)V"))
	private void captureVertices(BufferBuilder instance, double x, double y, double z, double u, double v, Operation<Void> original) {
		vertices.add(x);
		vertices.add(y);
		vertices.add(u);
		vertices.add(v);
	}

	@WrapOperation(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = {@At(value = "INVOKE", target = "Ljava/nio/IntBuffer;clear()Ljava/nio/Buffer;", remap = false), @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;flip()Ljava/nio/Buffer;", remap = false)})
	private Buffer noBufferFlip(IntBuffer instance, Operation<Buffer> original) {
		return null;
	}

	@WrapOperation(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;remaining()I", remap = false))
	private int noCallBuffer(IntBuffer instance, Operation<Buffer> original) {
		return 1;
	}

	@WrapOperation(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", remap = false))
	private void colorBufferInitially(float red, float green, float blue, float alpha, Operation<Void> original, @Share("alpha") LocalFloatRef alphaRef) {
		BufferBuilder.INSTANCE.start(GL11.GL_QUADS);
		alphaRef.set(alpha);
		BufferBuilder.INSTANCE.color(red, green, blue, alpha);
	}

	@ModifyArg(index = 0, method = "<init>(Lnet/minecraft/client/options/GameOptions;Ljava/lang/String;Lnet/minecraft/client/render/texture/TextureManager;)V",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V", ordinal = 1, remap = false))
	private int captureChatColorIndex(int index, @Share("index") LocalIntRef indexRef) {
		indexRef.set(index - 256 - this.boundPage);
		return index;
	}

	@ModifyArgs(method = "<init>(Lnet/minecraft/client/options/GameOptions;Ljava/lang/String;Lnet/minecraft/client/render/texture/TextureManager;)V",
		at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V", remap = false))
	private void captureChatColors(Args args, @Share("index") LocalIntRef indexRef) {
		chatColors[indexRef.get()][0] = args.get(0);
		chatColors[indexRef.get()][1] = args.get(1);
		chatColors[indexRef.get()][2] = args.get(2);
	}

	@WrapOperation(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;put(I)Ljava/nio/IntBuffer;", ordinal = 0))
	private IntBuffer modifyDrawColor(IntBuffer instance, int drawListIndex, Operation<IntBuffer> original, @Local(ordinal = 2, argsOnly = true) int color, @Share("alpha") LocalFloatRef alphaRef) {
		int colorIndex = drawListIndex - this.boundPage - 256;
		BufferBuilder.INSTANCE.color(chatColors[colorIndex][0], chatColors[colorIndex][1], chatColors[colorIndex][2], alphaRef.get());
		return instance;
	}

	@WrapOperation(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;put(I)Ljava/nio/IntBuffer;", ordinal = 1))
	private IntBuffer manuallyDrawChar(IntBuffer instance, int drawListIndex, Operation<IntBuffer> original, @Share("characterXOffset") LocalIntRef characterXOffset) {
		var bufferBuilder = BufferBuilder.INSTANCE;
		double[] charVertices = new double[16];
		int pageIndex = drawListIndex - boundPage;
		int xOffset = characterXOffset.get();
		vertices.getElements(pageIndex * 16, charVertices, 0, 16);
		bufferBuilder.vertex(charVertices[0] + xOffset, charVertices[1], 0, charVertices[2], charVertices[3]);
		bufferBuilder.vertex(charVertices[4] + xOffset, charVertices[5], 0, charVertices[6], charVertices[7]);
		bufferBuilder.vertex(charVertices[8] + xOffset, charVertices[9], 0, charVertices[10], charVertices[11]);
		bufferBuilder.vertex(charVertices[12] + xOffset, charVertices[13], 0, charVertices[14], charVertices[15]);
		characterXOffset.set(xOffset + characterWidths[pageIndex]);
		return instance;
	}

	@WrapOperation(method = "drawLayer(Ljava/lang/String;IIIZ)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glCallLists(Ljava/nio/IntBuffer;)V", ordinal = 2))
	private void sendBuffer(IntBuffer lists, Operation<Void> original) {
		BufferBuilder.INSTANCE.end();
	}
}
