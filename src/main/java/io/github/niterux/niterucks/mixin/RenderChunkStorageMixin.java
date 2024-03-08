package io.github.niterux.niterucks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.world.RenderChunkStorage;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//I know this mixin is terrible with compatibility, please contact if you know of a better way
@Mixin(RenderChunkStorage.class)
public class RenderChunkStorageMixin {
	@Shadow
	private int regionX;
	@Shadow
	private int regionY;
	@Shadow
	private int regionZ;
	@Unique
	private double[] cameraPositions = new double[3];

	@WrapOperation(method = "render()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", remap = false))
	private void fixMovementStutters(float x, float y, float z, Operation<Void> original) {
		GL11.glTranslated(this.regionX - this.cameraPositions[0], this.regionY - this.cameraPositions[1], this.regionZ - this.cameraPositions[2]);
	}

	@Inject(method = "setPositions(IIIDDD)V", at = @At("TAIL"))
	private void getDoubleCamera(int regionX, int regionY, int regionZ, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		cameraPositions[0] = cameraX;
		cameraPositions[1] = cameraY;
		cameraPositions[2] = cameraZ;
	}
}
