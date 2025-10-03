package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import java.util.Comparator;

public class NameSort implements Comparator<String> {
	public static NameSort INSTANCE = new NameSort();

	@Override
	public int compare(String string1, String string2) {
		// Only time 0 should be returned
		if (string1.equals(string2))
			return 0;
		// Geyser Bedrock default config starts names with .
		int string1BeginsWithDot = string1.charAt(0) == '.' ? 1 : 0;
		int string2BeginsWithDot = string2.charAt(0) == '.' ? 1 : 0;
		int minimumStringLength = Math.min(string1.length() - string1BeginsWithDot, string2.length() - string2BeginsWithDot);
		int capitalizationDifference = 0;

		for (int i = 0; i < minimumStringLength; i++) {
			char string1IndexedChar = string1.charAt(i + string1BeginsWithDot);
			char string2IndexedChar = string2.charAt(i + string2BeginsWithDot);
			if (string1IndexedChar == string2IndexedChar)
				continue;
			char string1IndexedCharLowerCase = Character.toLowerCase(string1IndexedChar);
			char string2IndexedCharLowerCase = Character.toLowerCase(string2IndexedChar);
			if (string1IndexedCharLowerCase == string2IndexedCharLowerCase) {
				if (capitalizationDifference == 0)
					capitalizationDifference = string1IndexedChar - string2IndexedChar;
				continue;
			}
			return string1IndexedCharLowerCase - string2IndexedCharLowerCase;
		}
		// capitals go first
		if (capitalizationDifference != 0)
			return capitalizationDifference;

		int longerString = string1.length() - string2.length();
		// sort .a aa as .a aa
		if (longerString == 0)
			return -1 >>> string2BeginsWithDot;
		return longerString;
	}
}
