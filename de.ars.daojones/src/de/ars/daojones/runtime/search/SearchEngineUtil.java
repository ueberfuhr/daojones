package de.ars.daojones.runtime.search;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A utility class providing helper methods for using or developing custom
 * search engines.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class SearchEngineUtil {

	private static <T> Iterable<T> toIterable(final Enumeration<T> enumeration) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					public boolean hasNext() {
						return enumeration.hasMoreElements();
					}

					public T next() {
						return enumeration.nextElement();
					}

					public void remove() {
						throw new UnsupportedOperationException(
								"The method \"remove\" of wrapping iterator for an enumeration is not supported.");
					}
				};
			}
		};
	}

	
	/**
	 * Parses a search text and splits it into multiple search texts.
	 * @param text the search text
	 * @param separator the separator
	 * @param openingBracket the opening bracket
	 * @param closingBracket the closingBracket
	 * @return the list of search texts
	 */
	public static List<String> parseText(final String text,
			final String separator, final String openingBracket,
			final String closingBracket) {
		/*
		 * OPENING BRACKET: separator before -> entry starts with, mode is open
		 * examples: - "das ist ein test" - das "ist ein test" - das
		 * " ist ein test" - das " ist ein " test - das ( ist ein ) test (von
		 * mir)
		 */
		final List<String> entries = new ArrayList<String>();
		if (openingBracket.equals(closingBracket)) {
			boolean isInBracket = text.indexOf(openingBracket) == 0;
			for (Object s1 : toIterable(new StringTokenizer(text,
					openingBracket))) {
				if (isInBracket) {
					entries.add((String) s1);
				} else {
					for (Object s2 : toIterable(new StringTokenizer(
							(String) s1, separator))) {
						entries.add((String) s2);
					}
				}
				isInBracket = !isInBracket;
			}
		} else {
			for (Object s1 : toIterable(new StringTokenizer(text,
					openingBracket))) {
				boolean isInBracket = ((String) s1).indexOf(closingBracket) != 0;
				for (Object s2 : toIterable(new StringTokenizer((String) s1,
						closingBracket))) {
					if (isInBracket) {
						entries.add((String) s2);
					} else {
						for (Object s3 : toIterable(new StringTokenizer(
								(String) s2, separator))) {
							entries.add((String) s3);
						}
					}
					isInBracket = false;
				}
			}
		}
		return entries;
	}
	
}
