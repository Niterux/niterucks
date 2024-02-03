package io.github.niterux.niterucks.niterucksfeatures;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.bevofeatures.BetaEVO;
import io.github.niterux.niterucks.bevofeatures.PlayerNameStatus;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ChatUtils {
	private static final ArrayList<String> localChatHistory = new ArrayList<>();

	public static int makeIndexValid(int index) {
		Niterucks.LOGGER.debug("INDEX CHECK");
		if (index > localChatHistory.size())
			index = localChatHistory.size();
		if (index < 0)
			index = 0;
		return index;
	}

	public static String getLocalHistoryMessage(int index) {
		Niterucks.LOGGER.debug("HISTORY GET");
		if (index > localChatHistory.size())
			throw new IllegalArgumentException("INDEX GREATER THAN LOCAL CHAT HISTORY");
		if (index < 0)
			throw new IllegalArgumentException("INDEX IS LESS THAN 0");
		if (index == 0)
			return "";
		return localChatHistory.get(localChatHistory.size() - index);
	}

	public static void putLocalHistoryMessage(String message) {
		Niterucks.LOGGER.debug("HISTORY PUT");
		if (localChatHistory.size() > 100)
			localChatHistory.remove(0);
		localChatHistory.add(message);
	}

	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	public static void copyToClipboard(String messageText, int caretPos, int selectionPos) {
		int start = Math.min(messageText.length() - caretPos, messageText.length() - selectionPos);
		int end = Math.max(messageText.length() - caretPos, messageText.length() - selectionPos);
		String copyString = messageText.substring(start, end);
		StringSelection data = new StringSelection(copyString);
		clipboard.setContents(data, data);
	}

	public static String cutTextToClipboard(String messageText, int caretPos, int selectionPos) {
		copyToClipboard(messageText, caretPos, selectionPos);
		return cutText(messageText, caretPos, selectionPos);
	}

	public static String pasteFromClipboard(String messageText, int caretPos) {
		String result;
		Transferable contents = clipboard.getContents(null);
		boolean hasStringText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasStringText) {
			try {
				result = (String) contents.getTransferData(DataFlavor.stringFlavor);
				return messageText.substring(0, messageText.length() - caretPos) + result + messageText.substring(messageText.length() - caretPos);
			} catch (UnsupportedFlavorException | IOException ex) {
				ex.printStackTrace();
				return messageText;
			}
		}
		return messageText;
	}

	public static String cutText(String text, int caretPos, int selectionPos) {
		int start = Math.min(text.length() - caretPos, text.length() - selectionPos);
		int end = Math.max(text.length() - caretPos, text.length() - selectionPos);
		String returningText = text.substring(0, start) + text.substring(end);
		Niterucks.LOGGER.debug(returningText);
		return returningText;
	}

	public static String insertString(String text, String string, int position) {
		return text.substring(0, text.length() - position) + string + text.substring(text.length() - position);
	}

	public static String findMatchingPlayers(String message, int caretPos) {
		if (message.trim().isEmpty())
			return message;
		String[] words = message.substring(0, message.length() - caretPos).split("\\W");
		Collection<PlayerNameStatus> players = BetaEVO.playerList.values();
		if (players.isEmpty())
			return message;
		for (PlayerNameStatus player : players) {
			if (player.getName().startsWith(words[words.length - 1])) {
				return message.substring(0, (message.length() - caretPos) - words[words.length - 1].length()) + player.getName() + ' ' + message.substring(message.length() - caretPos);
			}
		}
		return message;
	}
}
