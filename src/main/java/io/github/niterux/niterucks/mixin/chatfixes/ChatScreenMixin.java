package io.github.niterux.niterucks.mixin.chatfixes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.mixin.invokers.FillInvoker;
import io.github.niterux.niterucks.niterucksfeatures.MiscUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.ChatUtils.*;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
	@Shadow
	protected String lastChatMessage;
	// counts from the position of the end of the string
	@Unique
	private int caretPos = 0;
	@Unique
	private int selectionAnchor = -1;
	@Unique
	private int localChatHistorySelection = 0;
	@Shadow
	private int messageHistorySize;

	@Unique
	private int calculateCaretRenderPos(String text, int caret) {
		String caretTextBefore = text.substring(0, text.length() - caret);
		return this.textRenderer.getWidth(caretTextBefore) + this.textRenderer.getWidth("> ") + 4;
	}

	@Unique
	private void renderSelectionOverlay(String text, int caretRenderPos) {
		if (selectionAnchor == -1) {
			return;
		}
		int selectionRenderPos = calculateCaretRenderPos(text, selectionAnchor);
		int left = Math.min(caretRenderPos, selectionRenderPos);
		int right = Math.max(caretRenderPos, selectionRenderPos);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		BufferBuilder bufferBuilder = BufferBuilder.INSTANCE;
		GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
		bufferBuilder.start();
		bufferBuilder.vertex(left, this.height - 2, 0.0);
		bufferBuilder.vertex(right, this.height - 2, 0.0);
		bufferBuilder.vertex(right, this.height - 13, 0.0);
		bufferBuilder.vertex(left, this.height - 13, 0.0);
		bufferBuilder.end();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glLogicOp(GL11.GL_SET);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}

	@Unique
	private void moveCaret(int position) {
		if (position == caretPos) return;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && selectionAnchor == -1) {
			selectionAnchor = caretPos;
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			selectionAnchor = -1;
		}
		caretPos = position;
		if (caretPos < 0) caretPos = 0;
		if (caretPos > this.lastChatMessage.length()) caretPos = this.lastChatMessage.length();
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void fixFields(CallbackInfo ci) {
		caretPos = 0;
		selectionAnchor = -1;
		localChatHistorySelection = 0;
	}

	@Inject(method = "keyPressed(CI)V", at = @At("TAIL"))
	private void addNewKeys(char chr, int key, CallbackInfo ci) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (key == Keyboard.KEY_V) {
				try {
					if (selectionAnchor != -1) {
						lastChatMessage = cutText(lastChatMessage, caretPos, selectionAnchor);
						if (caretPos > selectionAnchor) moveCaret(caretPos - Math.abs(selectionAnchor - caretPos));
						selectionAnchor = -1;
					}
					lastChatMessage = pasteFromClipboard(lastChatMessage, caretPos);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (key == Keyboard.KEY_A) {
				if (!lastChatMessage.isEmpty()) {
					moveCaret(lastChatMessage.length());
					selectionAnchor = 0;
				}
			}

			if (selectionAnchor != -1 && selectionAnchor != caretPos) {
				switch (key) {
					case Keyboard.KEY_C:
						copyToClipboard(lastChatMessage, caretPos, selectionAnchor);
						break;
					case Keyboard.KEY_X:
						lastChatMessage = cutTextToClipboard(lastChatMessage, caretPos, selectionAnchor);
						if (caretPos > selectionAnchor) moveCaret(caretPos - Math.abs(selectionAnchor - caretPos));
						selectionAnchor = -1;
						break;
				}
			}
		}
		switch (key) {
			case Keyboard.KEY_LEFT:
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					moveCaret(findNextWord(lastChatMessage, caretPos, direction.LEFT));
				} else {
					moveCaret(caretPos + 1);
				}
				break;
			case Keyboard.KEY_RIGHT:
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					moveCaret(findNextWord(lastChatMessage, caretPos, direction.RIGHT));
				} else {
					moveCaret(caretPos - 1);
				}
				break;
			case Keyboard.KEY_UP:
				localChatHistorySelection += 2;
			case Keyboard.KEY_DOWN:
				localChatHistorySelection--;
				localChatHistorySelection = makeIndexValid(localChatHistorySelection);
				lastChatMessage = getLocalHistoryMessage(localChatHistorySelection);
				moveCaret(0);
				selectionAnchor = -1;
				break;
			case Keyboard.KEY_TAB:
				lastChatMessage = findMatchingPlayers(lastChatMessage, caretPos);
		}
		if (lastChatMessage.length() > 100) lastChatMessage = lastChatMessage.substring(0, 100);
		moveCaret(caretPos);
		if (selectionAnchor == caretPos) selectionAnchor = -1;
	}

	@WrapOperation(method = "keyPressed(CI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, ordinal = 1))
	private void typeAtCorrectPos(ChatScreen instance, String value, Operation<Void> original, char chr) {
		value = value.substring(0, value.length() - 1);
		if (selectionAnchor != -1) {
			value = cutText(value, caretPos, selectionAnchor);
			moveCaret(Math.min(caretPos, selectionAnchor));
			selectionAnchor = -1;
		}
		String newString = MiscUtils.insertText(value, chr, value.length() - caretPos);
		original.call(instance, newString);
	}

	@WrapOperation(method = "keyPressed(CI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void deleteAtCorrectPos(ChatScreen instance, String value, Operation<Void> original) {
		if (selectionAnchor != -1) {
			lastChatMessage = cutText(lastChatMessage, caretPos, selectionAnchor);
			moveCaret(Math.min(caretPos, selectionAnchor));
			selectionAnchor = -1;
		} else if (caretPos != this.lastChatMessage.length()) {
			String newString = this.lastChatMessage.substring(0, this.lastChatMessage.length() - 1 - caretPos) + this.lastChatMessage.substring(this.lastChatMessage.length() - caretPos);
			original.call(instance, newString);
		}
	}

	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;"))
	private String coloredChatTextPreview(String original, @Share("chatText") LocalRef<String> chatText) {
		chatText.set(original);
		String modifiedString = original;
		for (int i = 0; modifiedString.length() - 1 > i; i++) {
			if (modifiedString.charAt(i) == '&') {
				modifiedString = modifiedString.substring(0, i) + "§" + modifiedString.charAt(i + 1) + modifiedString.substring(i);
				i += 2;
			}
		}
		return modifiedString;
	}

	@Inject(method = "render(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V", shift = At.Shift.AFTER))
	private void renderCaret(int mouseY, int tickDelta, float par3, CallbackInfo ci, @Share("chatText") LocalRef<String> chatText) {
		int caretRenderPos = calculateCaretRenderPos(chatText.get(), caretPos);
		if (caretPos > 0 && this.messageHistorySize / 6 % 2 == 0) {
			((FillInvoker) this).invokeFill(caretRenderPos, this.height - 13, caretRenderPos + 1, this.height - 2, 0xFFd0d0d0);
		}
		renderSelectionOverlay(chatText.get(), caretRenderPos);
	}

	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;messageHistorySize:I", opcode = Opcodes.GETFIELD))
	private int preventUnderscoreRendering(int original) {
		return (caretPos > 0) ? 7 : original;
	}

	@Inject(method = "mouseClicked(III)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;minecraft:Lnet/minecraft/client/Minecraft;", ordinal = 0))
	private void clickToMoveCaret(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
		if (mouseY > this.height - 14) {
			moveCaret(this.lastChatMessage.length() - MiscUtils.fastTextPosGet(this.lastChatMessage, mouseX - this.textRenderer.getWidth("> ") - 4, this.textRenderer));
		}
	}
}
