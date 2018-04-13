
package generator.common.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class CommonUtils
{
	private static int NUMBER_OF_THREADS = 1;

	public static List<ThreadPair> getThreadsForGivenExampleNumber(int numberOfExamples)
	{
		List<ThreadPair> threadList = new ArrayList<ThreadPair>();
		int base = numberOfExamples / NUMBER_OF_THREADS;
		int numberOfIncrementalAdjusts = numberOfExamples % NUMBER_OF_THREADS;

		for (int i = 0; i < NUMBER_OF_THREADS; i++)
		{
			if (numberOfIncrementalAdjusts != 0)
			{
				ThreadPair pair = new ThreadPair(i, base + 1);
				numberOfIncrementalAdjusts--;
				threadList.add(pair);
				continue;
			}
			ThreadPair pair = new ThreadPair(i, base);
			threadList.add(pair);
		}

		return threadList;
	}

	public static void setNumberOfThreads(int numberOfThreads)
	{
		NUMBER_OF_THREADS = numberOfThreads;
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 * 
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max, Random rand)
	{

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	/**
	 * Removes first matching element from the given list.
	 * 
	 * @param list
	 * @param element
	 */
	public static void removeElementFromList(List<String> list, String element)
	{
		Iterator<String> it = list.iterator();
		while (it.hasNext())
		{
			if (it.next().equals(element))
			{
				it.remove();
				break;
			}
		}
	}

	public static final Pattern PATTERN_UNDERSCORE_LEFT_RIGHT_COMPOSITE_BRACKET = Pattern.compile("_{.*}",
		Pattern.LITERAL);
	public static final Pattern PATTERN_RIGHT_COMPOSITE_BRACKET = Pattern.compile("}", Pattern.LITERAL);
	public static final Pattern PATTERN_LEFT_SHARP_BRACKET = Pattern.compile("[", Pattern.LITERAL);
	public static final Pattern PATTERN_RIGHT_SHARP_BRACKET = Pattern.compile("]", Pattern.LITERAL);

	public static final Pattern GRAMMAR_INPUT_FORM_PATTERN = Pattern
		.compile("(((\\s*[A-Z]\\s*->(\\s*([A-Z]{2}|[a-z0-9])\\s*)(\\|\\s*([A-Z]{2}|[a-z0-9])\\s*)*)|(---\\s*))[\r\n]*)*");
	// for now validates words without diacritic (no local language alphabet chars) -> can be extended - validator
	// don't have optimal performance then... Could be solved via focuslost listener
	// words separated by new lines
	// initial nonterminal is the first found!
	public static final Pattern WORD_INPUT_FORM_PATTERN = Pattern.compile("([a-z]+[\r\n]?)*");

	public static List<String> generateAlphabetList(int alphCount, int alphabetType)
	{
		List<String> listToReturn = new ArrayList<>();
		switch (alphabetType)
		{
			case 0:
				for (int i = 0; i < alphCount; i++)
				{
					listToReturn.add(String.valueOf((char) ('a' + i)));
				}
				break;
			case 1:
				for (int i = 0; i < alphCount; i++)
				{
					listToReturn.add(String.valueOf((char) ('A' + i)));
				}
				break;
			case 2:
				for (int i = 0; i < alphCount; i++)
				{
					listToReturn.add(String.valueOf(i + 1));
				}
				break;
			case 3:
				for (int i = 0; i < alphCount; i++)
				{
					listToReturn.add(String.valueOf((char) ('z' - i)));
				}
				break;
			case 4:
				for (int i = 0; i < alphCount; i++)
				{
					listToReturn.add(String.valueOf((char) ('Z' - i)));
				}
				break;
		}

		return listToReturn;

	}
}
