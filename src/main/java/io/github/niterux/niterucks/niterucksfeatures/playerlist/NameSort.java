package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import java.util.Comparator;
import java.util.regex.Pattern;

public class NameSort implements Comparator<String> {
	private static final Pattern firstDotPattern = Pattern.compile("^\\.");
	@Override
	public int compare(String o1, String o2) {
		o1 = o1.toLowerCase();
		o2 = o2.toLowerCase();
		String o1NoDot = firstDotPattern.matcher(o1).replaceFirst("");
		String o2NoDot = firstDotPattern.matcher(o2).replaceFirst("");
		if (o1.equals(o2NoDot))
			return -1;
		if (o2.equals(o1NoDot))
			return 1;
		if (o1.equals(o2))
			return 0;
		return o1NoDot.compareTo(o2NoDot);
	}

	public static NameSort INSTANCE = new NameSort();
}
