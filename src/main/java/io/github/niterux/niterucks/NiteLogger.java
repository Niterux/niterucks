package io.github.niterux.niterucks;

import java.util.Calendar;

public class NiteLogger {
	private final String name;
	public int level = 3;
	Calendar time;

	public NiteLogger(String name) {
		this.name = name;
	}

	public NiteLogger(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public void info(String text) {
		if (level >= 3)
			printMessage(text, "info");
	}

	public void warn(String text) {
		if (level >= 2)
			printMessage(text, "warn", "\u001B[33m");
	}

	public void error(String text) {
		if (level >= 1)
			printMessage(text, "error", "\u001B[31m");
	}

	public void debug(String text) {
		if (level >= 4)
			printMessage(text, "debug", "\u001B[35m");
	}

	private void printMessage(String text, String levelName) {
		System.out.println(getTimestamp() + '(' + this.name + ")(" + levelName + "): " + text);
	}

	private void printMessage(String text, String levelName, String color) {
		System.out.println(color + getTimestamp() + '(' + this.name + ")(" + levelName + "): " + text + "\u001B[0m");
	}

	private String getTimestamp() {
		time = Calendar.getInstance(); // this is probably inefficient, but I couldn't find a better way to do this
		return "[" + longate(time.get(Calendar.HOUR)) + ':' + longate(time.get(Calendar.MINUTE)) + ':' + longate(time.get(Calendar.SECOND)) + ']';
	}

	private String longate(int Time) {
		return String.valueOf((Time < 10) ? "0" + Time : Time);
	}
}
