package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import java.util.Comparator;

public class NameSort implements Comparator<String> {
	public static NameSort INSTANCE = new NameSort();

	@Override
	public int compare(String string1, String string2) {
		if (string1.equals(string2))
			return 0;
		char[] string1AsChars = new char[string1.length()];
		char[] string2AsChars = new char[string2.length()];
		string1.getChars(0, string1.length(), string1AsChars, 0);
		string2.getChars(0, string2.length(), string2AsChars, 0);
		int capitalizationDifference = 0;
		// Geyser Bedrock default config starts names with .
		int string1BeginsWithDot = string1AsChars[0] == '.' ? 1 : 0;
		int string2BeginsWithDot = string2AsChars[0] == '.' ? 1 : 0;
		int minimumStringLength = Math.min(string1AsChars.length - string1BeginsWithDot, string2AsChars.length - string2BeginsWithDot);

		for (int i = 0; i < minimumStringLength; i++) {
			char string1IndexedChar = string1AsChars[i + string1BeginsWithDot];
			char string2IndexedChar = string2AsChars[i + string2BeginsWithDot];
			if (string1IndexedChar == string2IndexedChar)
				continue;
			char string1IndexedCharLowerCase = Character.toLowerCase(string1IndexedChar);
			char string2IndexedCharLowerCase = Character.toLowerCase(string2IndexedChar);
			if (string1IndexedCharLowerCase == string2IndexedCharLowerCase && capitalizationDifference == 0) {
				capitalizationDifference = string1IndexedChar - string2IndexedChar;
				continue;
			}
			return string1IndexedCharLowerCase - string2IndexedCharLowerCase;
		}
		// capitals go first
		if (capitalizationDifference != 0)
			return capitalizationDifference;

		int longerString = string1AsChars.length - string2AsChars.length;
		// sort .a aa as .a aa
		if (longerString == 0)
			return -1 << string2BeginsWithDot;
		return longerString;
	}
}
