package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.GameFeaturesStates.zoomAmount;
import static io.github.niterux.niterucks.niterucksfeatures.KeyStateManager.niterucksControls;

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

	@ModifyExpressionValue(method = "getFov(F)F", at = @At(value = "CONSTANT", args = "floatValue=70.0F", ordinal = 0))
	private float changeFov(float seventy, @Share("seventy")LocalFloatRef fovRef) {
		fovRef.set(seventy);
		return this.handFov ? seventy : Niterucks.CONFIG.FOV.get();
	}

	@ModifyExpressionValue(method = "getFov(F)F", at = @At(value = "CONSTANT", args = "floatValue=60.0F", ordinal = 0))
	private float changeWaterFov(float sixty, @Share("seventy")LocalFloatRef fovRef) {
		return this.handFov ? sixty : Niterucks.CONFIG.FOV.get() * sixty / fovRef.get();
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

	@ModifyExpressionValue(method = "render(F)V", at = @At(value = "CONSTANT", args = "floatValue=0.6F", ordinal = 0))
	private float addZoomFunctionality(float constant) {
		zoom = niterucksControls[0] ? Math.pow((double) zoomAmount / 4, 2) : 1.0;
		return niterucksControls[0] ? (float) (constant / Math.pow((double) zoomAmount / 4, 2)) : constant;
	}
}
