package io.github.niterux.niterucks.mixin.chatfixes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.BufferBuilder;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.TextRendererCharacterWidthsAccessor;
import io.github.niterux.niterucks.mixin.invokers.FillInvoker;
import io.github.niterux.niterucks.niterucksfeatures.ChatScreenGetMessageScroll;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.niterux.niterucks.niterucksfeatures.ChatUtils.*;

@Debug(export = true)
@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements ChatScreenGetMessageScroll {

	@Unique
	private int caretPos = 0; // inverted from the total length of the chat string

	@Unique
	private int selectionAnchor = -1;
	@Unique
	private int localChatHistorySelection = 0;
	@Shadow
	protected String lastChatMessage;
	@Shadow
	private int messageHistorySize;
	@Unique
	private int messageScroll = 0;

	@Override
	public int niterucks$GetMessageScroll() {
		int returnMessageScroll = messageScroll;
		messageScroll = 0;
		return returnMessageScroll;
	}

	@Unique
	private int calculateCaretRenderPos(String text, int caret) {
		if (caret > text.length())
			Niterucks.LOGGER.debug("CARET LARGER THAN LENGTH");
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

		BufferBuilder var10 = BufferBuilder.INSTANCE;
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		var10.start();
		var10.vertex(left, (double) this.height - 2, 0.0);
		var10.vertex(right, (double) this.height - 2, 0.0);
		var10.vertex(right, (double) this.height - 13, 0.0);
		var10.vertex(left, (double) this.height - 13, 0.0);
		var10.end();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glLogicOp(GL11.GL_SET);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}

	@Unique
	private int fastTextPosGet(String text, int xPos) {
		int newWidth;
		if (text == null) {
			return 0;
		} else {
			if (xPos > this.textRenderer.getWidth(text)) {
				return text.length();
			}
			int textWidth = 0;

			int currentCharacter;
			for (currentCharacter = 0; currentCharacter < text.length(); ++currentCharacter) {
				if (text.charAt(currentCharacter) == 167) {
					++currentCharacter;
				} else {
					int validCharacter = SharedConstants.VALID_CHAT_CHARACTERS.indexOf(text.charAt(currentCharacter));
					if (validCharacter >= 0) {
						newWidth = textWidth + ((TextRendererCharacterWidthsAccessor) this.textRenderer).getCharacterWidths()[validCharacter + 32];
						if (newWidth > xPos) {
							return currentCharacter;
						} else {
							textWidth = newWidth;
						}
					}
				}
			}

			return currentCharacter;
		}
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
		messageScroll = 0;
	}

	@Inject(method = "keyPressed(CI)V", at = @At("TAIL"))
	private void addNewKeys(char chr, int key, CallbackInfo ci) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (key == Keyboard.KEY_V) {
				if (selectionAnchor != -1) {
					lastChatMessage = cutText(lastChatMessage, caretPos, selectionAnchor);
					if (caretPos > selectionAnchor) moveCaret(caretPos - Math.abs(selectionAnchor - caretPos));
					selectionAnchor = -1;
				}
				lastChatMessage = pasteFromClipboard(lastChatMessage, caretPos);
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
				Niterucks.LOGGER.debug(lastChatMessage);
		}
		if (lastChatMessage.length() > 100)
			lastChatMessage = lastChatMessage.substring(0, 100);
		moveCaret(caretPos);
	}

	@WrapOperation(method = "keyPressed(CI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, ordinal = 1))
	private void typeAtCorrectPos(ChatScreen instance, String value, Operation<Void> original, char chr) {
		value = value.substring(0, value.length() - 1);
		if (selectionAnchor != -1) {
			Niterucks.LOGGER.debug(selectionAnchor + "hi");
			value = cutText(value, caretPos, selectionAnchor);
			moveCaret(Math.min(caretPos, selectionAnchor));
			selectionAnchor = -1;
		}
		String newString = insertString(value, String.valueOf(chr), caretPos);
		original.call(instance, newString);

	}

	@WrapOperation(method = "keyPressed(CI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void deleteAtCorrectPos(ChatScreen instance, String value, Operation<Void> original) {
		boolean cancelBackspace = false;
		if (selectionAnchor != -1) {
			Niterucks.LOGGER.debug(selectionAnchor + "hi");
			lastChatMessage = cutText(lastChatMessage, caretPos, selectionAnchor);
			moveCaret(Math.min(caretPos, selectionAnchor));
			selectionAnchor = -1;
			cancelBackspace = true;
		}
		if (caretPos != this.lastChatMessage.length() && !cancelBackspace) {
			String newString = this.lastChatMessage.substring(0, this.lastChatMessage.length() - 1 - caretPos) + this.lastChatMessage.substring(this.lastChatMessage.length() - caretPos);
			original.call(instance, newString);
		}
	}

	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/screen/ChatScreen;lastChatMessage:Ljava/lang/String;"))
	private String coloredChatTextPreview(String original, @Share("chatText") LocalRef<String> chatText) {
		int scroll = Mouse.getDWheel();
		if (scroll != 0) {
			messageScroll = (int) (Math.signum(scroll) * 1);
		}
		chatText.set(original);
		String modifiedString = original;
		for (int var12 = 0; modifiedString.length() - 1 > var12; var12++) {
			if (modifiedString.charAt(var12) == '&') {
				modifiedString = modifiedString.substring(0, var12) + "§" + modifiedString.charAt(var12 + 1) + modifiedString.substring(var12);
				var12 += 2;
			}
		}
		return modifiedString;
	}

	@Inject(method = "render(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;drawString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V", shift = At.Shift.AFTER))
	private void renderCaret(int mouseY, int tickDelta, float par3, CallbackInfo ci, @Share("chatText") LocalRef<String> chatText) {
		int caretRenderPos = calculateCaretRenderPos(chatText.get(), caretPos);
		renderSelectionOverlay(chatText.get(), caretRenderPos);
		if (caretPos > 0 && this.messageHistorySize / 6 % 2 == 0) {

			((FillInvoker) this).invokeFill(caretRenderPos, this.height - 13, caretRenderPos + 1, this.height - 2, 0xFFd0d0d0);
		}
	}

	@ModifyExpressionValue(method = "render(IIF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;messageHistorySize:I", opcode = Opcodes.GETFIELD))
	private int preventUnderscoreRendering(int original) {
		return (caretPos > 0) ? 7 : original;
	}

	@Inject(method = "mouseClicked(III)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ChatScreen;minecraft:Lnet/minecraft/client/Minecraft;", ordinal = 0))
	private void clickToMoveCaret(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
		if (mouseY > this.height - 14) {
			moveCaret(this.lastChatMessage.length() - fastTextPosGet(this.lastChatMessage, mouseX - this.textRenderer.getWidth("> ") - 4));
		}
	}
}
