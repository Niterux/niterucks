package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Unique
	boolean handFov = false;

	@Shadow
	private float getFov(float f) {
		return 10F;
	}

	@Shadow
	private double zoom;
	@Shadow
	private double zoomX;
	@Shadow
	private double zoomY;
	@Shadow
	private Minecraft minecraft;
	@Shadow
	private float viewDistance;

	@ModifyConstant(method = "getFov(F)F", constant = @Constant(floatValue = 70.0F, ordinal = 0))
	private float changeFov(float seventy) {
		return this.handFov ? seventy : Niterucks.CONFIG.FOV.get();
	}

	@Inject(method = "setupCamera(FI)V", at = @At(value = "HEAD"))
	private void injectHandFovMode(CallbackInfo info) {
		this.handFov = false;
	}

	@Inject(method = "renderItemInHand(FI)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", remap = false))
	private void changePerspective(float tickDelta, int anaglyphFilter, CallbackInfo ci) {
		this.handFov = true;
		if (zoom != 1.0) {
			GL11.glTranslatef((float) zoomX, (float) (-zoomY), 0.0F);
			GL11.glScaled(zoom, zoom, 1.0);
			GLU.gluPerspective(getFov(tickDelta), (float) minecraft.width / (float) minecraft.height, 0.05F, viewDistance * 2.0F);
		} else {
			GLU.gluPerspective(getFov(tickDelta), (float) minecraft.width / (float) minecraft.height, 0.05F, viewDistance * 2.0F);
		}
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
	}

	@Inject(method = "renderItemInHand(FI)V", at = @At("HEAD"))
	private void injectGlMode(CallbackInfo info) {
		GL11.glMatrixMode(5889);
	}

}
