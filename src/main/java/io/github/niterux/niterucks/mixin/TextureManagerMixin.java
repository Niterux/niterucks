package io.github.niterux.niterucks.mixin;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.render.texture.TextureManager;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
	@Inject(method = "reload()V", at = @At("TAIL"))
	private void reloadChunks(CallbackInfo ci){
		MinecraftInstanceAccessor.getMinecraft().worldRenderer.m_6748042();
		ByteBuffer[] icons = new ByteBuffer[2];
		try {
			/*icons[0] = MiscUtils.decodePng(ImageIO.read(Objects.requireNonNull(Niterucks.class.getResource("/assets/niterucks/icons/128x.png"))));
			icons[1] = MiscUtils.decodePng(ImageIO.read(Objects.requireNonNull(Niterucks.class.getResource("/assets/niterucks/icons/32x.png"))));
			icons[2] = MiscUtils.decodePng(ImageIO.read(Objects.requireNonNull(Niterucks.class.getResource("/assets/niterucks/icons/16x.png"))));*/
			icons[0] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/16x.png"));
			icons[1] = MiscUtils.readImageBuffer(Niterucks.class.getResourceAsStream("/assets/niterucks/icons/32x.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Display.setIcon(icons);
	}

}
