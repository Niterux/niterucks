package io.github.niterux.niterucks.niterucksfeatures;

import io.github.niterux.niterucks.Niterucks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

import static io.github.niterux.niterucks.niterucksfeatures.playerlist.PlayerListUtil.getPlayerList;

public class ChatUtils {
	private static final ObjectArrayList<String> localChatHistory = new ObjectArrayList<>(100);
	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	public static int makeIndexValid(int index) {
		if (index > localChatHistory.size())
			index = localChatHistory.size();
		if (index < 0)
			index = 0;
		return index;
	}

	public static String getLocalHistoryMessage(int index) {
		if (index > localChatHistory.size())
			throw new IllegalArgumentException("INDEX GREATER THAN LOCAL CHAT HISTORY");
		if (index < 0)
			throw new IllegalArgumentException("INDEX IS LESS THAN 0");
		if (index == 0)
			return "";
		return localChatHistory.get(localChatHistory.size() - index);
	}

	public static void putLocalHistoryMessage(String message) {
		if (localChatHistory.size() > 100)
			localChatHistory.remove(0);
		localChatHistory.add(message);
	}

	public static String cutTextToClipboard(String messageText, int caretPos, int selectionPos) {
		copyToClipboard(messageText, caretPos, selectionPos);
		return cutText(messageText, caretPos, selectionPos);
	}

	public static void copyToClipboard(String messageText, int caretPos, int selectionPos) {
		int start = Math.min(messageText.length() - caretPos, messageText.length() - selectionPos);
		int end = Math.max(messageText.length() - caretPos, messageText.length() - selectionPos);
		String copyString = messageText.substring(start, end);
		StringSelection data = new StringSelection(copyString);
		clipboard.setContents(data, data);
	}

	public static String cutText(String text, int caretPos, int selectionPos) {
		int start = Math.min(text.length() - caretPos, text.length() - selectionPos);
		int end = Math.max(text.length() - caretPos, text.length() - selectionPos);
		return text.substring(0, start) + text.substring(end);
	}

	public static String pasteFromClipboard(String messageText, int caretPos) {
		try {
			Transferable contents = clipboard.getContents(null);
			boolean hasStringText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (!hasStringText)
				return messageText;
			String result = (String) contents.getTransferData(DataFlavor.stringFlavor);
			return messageText.substring(0, messageText.length() - caretPos) + result + messageText.substring(messageText.length() - caretPos);
		} catch (UnsupportedFlavorException | IOException ex) {
			Niterucks.LOGGER.error("An error occurred: ", ex);
			return messageText;
		}
	}

	public static String findMatchingPlayers(String message, int caretPos) {
		if (message.trim().isEmpty())
			return message;
		int nonWordChars = 0;
		while (("" + message.charAt(message.length() - caretPos - nonWordChars - 1)).matches("\\W")) {
			nonWordChars++;
			if (nonWordChars == message.length())
				return message;
		}

		String[] words = message.substring(0, message.length() - caretPos).split("\\W");
		String word = words[words.length - 1].toLowerCase();
		String[] players = getPlayerList();
		if (players == null)
			return message;
		for (String player : players) {
			if (player.toLowerCase().startsWith(word))
				return message.substring(0, (message.length() - caretPos) - word.length() - nonWordChars) + player + ' ' + message.substring(message.length() - caretPos);
		}
		return message;
	}

	public static int findNextWord(String text, int caretPos, direction direction) {
		int cursorPosition = caretPos;
		int textLength = text.length();
		boolean isRight = (direction == ChatUtils.direction.RIGHT);

		if (isRight) {
			while (cursorPosition > 0 && text.charAt(textLength - cursorPosition) == ' ')
				--cursorPosition;
			while (cursorPosition > 0 && text.charAt(textLength - cursorPosition) != ' ')
				--cursorPosition;
			return cursorPosition;
		}
		while (cursorPosition < textLength && text.charAt(textLength - cursorPosition - 1) == ' ')
			++cursorPosition;
		while (cursorPosition < textLength && text.charAt(textLength - cursorPosition - 1) != ' ')
			++cursorPosition;
		return cursorPosition;
	}

	public enum direction {
		LEFT,
		RIGHT
	}
}
