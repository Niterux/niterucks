package io.github.niterux.niterucks.niterucksfeatures;

import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleChatLoggingFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		String message = record.getMessage();
		if (message == null)
			return null;
		message = message.replaceAll("§.", "");
		return MessageFormat.format("Chat: {0}\n", message);
	}
}
