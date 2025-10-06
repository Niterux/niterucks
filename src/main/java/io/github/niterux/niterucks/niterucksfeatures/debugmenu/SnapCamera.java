package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SnapCamera implements KeyCombo {
	@Override
	public int getComboKey() {
		return Niterucks.CONFIG.snapCameraButton.get().keyCode;
	}

	@Override
	public void onPress(Minecraft minecraft) {
		Entity camera = minecraft.camera;
		camera.yaw = Math.round(camera.yaw / 45) * 45;
		camera.pitch = Math.round(camera.pitch / 45) * 45;
	}

	@Override
	public @NotNull String actionDescription() {
		return "Snap the camera to the nearest 45 degree angle";
	}
}
