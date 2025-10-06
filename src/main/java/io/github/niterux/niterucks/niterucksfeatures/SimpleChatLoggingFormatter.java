package io.github.niterux.niterucks.niterucksfeatures;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleChatLoggingFormatter extends Formatter {
	private static final Matcher replacementMatcher = Pattern.compile("§.?").matcher("");

	@Override
	public String format(LogRecord record) {
		String message = record.getMessage();
		if (message == null)
			return null;
		return "Chat: " + replacementMatcher.reset(message).replaceAll("") + '\n';
	}
}
