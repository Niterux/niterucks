package io.github.niterux.niterucks.mixin.ui;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;
import java.time.Month;

import static io.github.niterux.niterucks.Niterucks.modVersion;

@Mixin(value = TitleScreen.class, priority = 2000)
public class TitleScreenMixin extends Screen {
	@Shadow
	private String splashText;

	@Inject(method = "<init>()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;splashText:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, ordinal = 1))
	private void splashInject(CallbackInfo info) {
		//Late VSync application
		Display.setVSyncEnabled(Niterucks.CONFIG.useVSync.get());
		//ensure the fire texture is initialized
		//minecraft.textureManager.tick();
		LocalDateTime currentDate = LocalDateTime.now();
		Month month = currentDate.getMonth();
		int day = currentDate.getDayOfMonth();
		if (month == Month.DECEMBER && day == 14) {
			splashText = "Happy birthday, Niterux!";
		}
		if (Math.random() < 0.083) {
			splashText = "Trans rights!";
		}
		if (month == Month.NOVEMBER && day >= 13 && day <= 20) {
			splashText = MiscUtils.textGradientGenerator("Trans lives matter!", new char[]{'b', 'd', 'f', 'd', 'b'}, this.textRenderer);
		}
	}

	@Inject(method = "render(IIF)V", at = @At("TAIL"))
	private void drawNiterucksVersionText(CallbackInfo ci) {
		this.drawString(this.textRenderer, "Niterucks Client " + modVersion, 2, this.height - 10, 0x5336A2);
/*		final float scale = 2.0f;
		int var9 = Block.FIRE.sprite;
		int var10 = ((var9 & 15) << 4) + 1;
		int var11 = var9 & 240;
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		minecraft.textureManager.bind(minecraft.textureManager.load("/terrain.png"));
		GL11.glPushMatrix();

		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(width/2.0f/scale - (102/scale) - 14, 100/scale, 0);
		drawTexture(0, 0, var10, var11, 14, 16);
		GL11.glTranslatef((204/scale) + 14, 0, 0);
		drawTexture(0, 0, var10, var11, 14, 16);
		GL11.glPopMatrix();*/

	}
/*	@Inject(method = "tick", at = @At("HEAD"))
	private void tickTextureManagerToAdvanceFireAnimation(CallbackInfo ci){
		minecraft.textureManager.tick();
	}*/
}
