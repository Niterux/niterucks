package io.github.niterux.niterucks.mixin.debugmenu;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.niterux.niterucks.Niterucks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.Niterucks.MOD_VERSION;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {
	@Unique
	private static final String[] directions = {
		"South (Towards Positive Z)",
		"West (Towards Negative X)",
		"North (Towards Negative Z)",
		"East (Towards Positive X)"};
	@Shadow
	private Minecraft minecraft;

	@ModifyExpressionValue(method = "render",
		at = @At(value = "CONSTANT", args = "stringValue=Minecraft Beta 1.7.3 ("))
	private String replaceGameName(String original) {
		return String.format("Niterucks Client %s (", MOD_VERSION);
	}

	@ModifyArg(
		method = "render", at =
	@At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V",
		ordinal = 0
	),
		slice =
		@Slice(
			from =
			@At(
				value = "INVOKE",
				ordinal = 0,
				target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
				remap = false
			)
		),
		index = 3
	)
	private int colorChangeVersion(int color) {
		return 0x9e87fe;
	}

	@ModifyArg(
		method = "render",
		at =
		@At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"
		),
		slice =
		@Slice(
			from =
			@At(
				value = "INVOKE",
				ordinal = 2,
				target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"
			)
		),
		index = 4
	)
	private int colorChangeCoords(int color) {
		return 0x6D86F2;
	}

	@ModifyArg(
		method = "render",
		at =
		@At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"
		),
		slice =
		@Slice(
			from =
			@At(
				value = "INVOKE",
				ordinal = 1,
				target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"
			),
			to =
			@At(
				value = "INVOKE",
				ordinal = 4,
				target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V"
			)
		),
		index = 3
	)
	private int colorChangeDebugData(int color) {
		return 0xAAAAAA;
	}

	@ModifyArg(
		method = "render",
		at =
		@At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"),
		slice =
		@Slice(
			from =
			@At(
				value = "INVOKE",
				ordinal = 0,
				target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"
			),
			to =
			@At(
				value = "INVOKE",
				ordinal = 1,
				target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"
			)
		),
		index = 4
	)
	private int colorChangeMemory(int color) {
		return 0xd5312f;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
	private void addNewInfoText(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 0) TextRenderer textRenderer, @Local(ordinal = 2) int width) {
		// Precise enough? 🤞
		double eyeOffset = Niterucks.CONFIG.useFeetCoordinates.get() ? Math.nextUp(Double.sum(minecraft.player.y, -Double.sum(minecraft.player.eyeHeight, -minecraft.player.eyeHeightSneakOffset))) : minecraft.player.y;

		if (Niterucks.CONFIG.showSeed.get())
			this.drawString(textRenderer, "Seed: " + minecraft.world.getSeed(), 2, 104, 0xd96e02);

		this.drawString(textRenderer, "For help: press F3 + Q", 2, 96, 0xd96e02);
		String biomeString = "Biome: " + minecraft.world.getBiomeSource().getBiome(MathHelper.floor(minecraft.player.x), MathHelper.floor(minecraft.player.z)).name;
		this.drawString(textRenderer, biomeString, width - textRenderer.getWidth(biomeString) - 2, 22, 0x46a848);
		String lightString = "Light: " + minecraft.world.getRawBrightness(MathHelper.floor(minecraft.player.x), MathHelper.floor(eyeOffset), MathHelper.floor(minecraft.player.z));
		this.drawString(textRenderer, lightString, width - textRenderer.getWidth(lightString) - 2, 32, 0x46a848);

		if (minecraft.crosshairTarget != null && minecraft.crosshairTarget.type != HitResult.Type.ENTITY) {
			int targetBlockID = minecraft.world.getBlock(minecraft.crosshairTarget.x, minecraft.crosshairTarget.y, minecraft.crosshairTarget.z);
			int targetBlockMetadata = minecraft.world.getBlockMetadata(minecraft.crosshairTarget.x, minecraft.crosshairTarget.y, minecraft.crosshairTarget.z);
			String blockString = "Block: " + targetBlockID + ":" + targetBlockMetadata;
			this.drawString(textRenderer, blockString, width - textRenderer.getWidth(blockString) - 2, 42, 0x46a848);
		}
		ItemStack heldItem = minecraft.player.inventory.getMainHandStack();
		if (heldItem == null)
			return;
		String itemString = "Item: " + heldItem.itemId + ":" + heldItem.getDamage();
		this.drawString(textRenderer, itemString, width - textRenderer.getWidth(itemString) - 2, 52, 0x46a848);
	}

	@ModifyArg(
		method = "render",
		at =
		@At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GameGui;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V",
			ordinal = 5
		),
		slice =
		@Slice(
			from =
			@At(
				value = "INVOKE",
				ordinal = 0,
				target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
				remap = false
			)
		),
		index = 1
	)
	private String goodFaceDirection(String text) {
		//todo, make the code less cursed
		int facingDir = (int) this.minecraft.player.yaw;
		int index;
		if (facingDir < -45) {
			index = ((facingDir - 45) / 90) % 4;
			return "Facing: " + directions[(index + 4) % 4];
		} else {
			index = ((facingDir + 45) / 90) % 4;
			return "Facing: " + directions[index];
		}
	}

	@ModifyExpressionValue(
		method = "render",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;x:D",
			opcode = Opcodes.GETFIELD
		)
	)
	private double roundCoordsX(double x) {
		return truncate(x);
	}

	@Unique
	private double truncate(double coord) {
		return (double) Math.round(coord * 1000) / 1000;
	}

	@ModifyExpressionValue(
		method = "render",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;y:D",
			opcode = Opcodes.GETFIELD
		)
	)
	private double roundCoordsY(double y) {
		y = Niterucks.CONFIG.useFeetCoordinates.get() ? y - (minecraft.player.eyeHeight - minecraft.player.eyeHeightSneakOffset) : y;
		return truncate(y);
	}

	@ModifyExpressionValue(
		method = "render",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/entity/living/player/InputPlayerEntity;z:D",
			opcode = Opcodes.GETFIELD
		)
	)
	private double roundCoordsZ(double z) {
		return truncate(z);
	}
}
