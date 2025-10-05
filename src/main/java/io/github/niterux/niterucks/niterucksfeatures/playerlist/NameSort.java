package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import java.util.Comparator;

public class NameSort implements Comparator<String> {
	public static NameSort INSTANCE = new NameSort();

	/*
	pizza, Pizza = 32
	.sauc, sauce = 1
	pepperoni, pineapple = -4
	new String("cheese"), new String("cheese") = 0
	cheese, cheese = 0
	Fully sorted would be: cheese, cheese, cheese, cheese, pepperoni, pineapple, Pizza, pizza, sauce, .sauc
	 */
	@Override
	public int compare(String string1, String string2) {
		//noinspection StringEquality
		if (string1 == string2)
			return 0;
		// Geyser Bedrock default config starts names with .
		final int string1DotOffset = string1.charAt(0) == '.' ? 1 : 0;
		final int string2DotOffset = string2.charAt(0) == '.' ? 1 : 0;
		int minimumStringLength = Math.min(string1.length() - string1DotOffset, string2.length() - string2DotOffset);
		int capitalizationDifference = 0;

		for (int i = 0; i < minimumStringLength; i++) {
			char string1IndexedChar = string1.charAt(i + string1DotOffset);
			char string2IndexedChar = string2.charAt(i + string2DotOffset);
			if (string1IndexedChar == string2IndexedChar)
				continue;
			char string1IndexedCharLowerCase = Character.toLowerCase(string1IndexedChar);
			char string2IndexedCharLowerCase = Character.toLowerCase(string2IndexedChar);
			if (string1IndexedCharLowerCase != string2IndexedCharLowerCase)
				return string1IndexedCharLowerCase - string2IndexedCharLowerCase;
			if (capitalizationDifference == 0)
				capitalizationDifference = string1IndexedChar - string2IndexedChar;
		}
		// capitals go first
		if (capitalizationDifference != 0)
			return capitalizationDifference;

		int longerString = string1.length() - string2.length();
		if (longerString != 0)
			return longerString;
		// sort .a aa as aa .a / return 0 for equal strings
		return string1DotOffset - string2DotOffset;
	}
}
