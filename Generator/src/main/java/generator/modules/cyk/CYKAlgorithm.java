
package generator.modules.cyk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Implementation of algorithm for deterministic syntactic analysis.
 * 
 * @author JUH
 */
public class CYKAlgorithm
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static CYKTable executeAlgorithm(String word, Grammar g)
	{
		char[] wordChars = word.toCharArray();
		String[] wordStringChars = new String[wordChars.length];
		int wordLength = wordChars.length;

		// first list sets beginning index, second internal sets subword length
		List<List<Set<String>>> cykTable = new ArrayList<List<Set<String>>>();

		SortedMap<String, Set<String>> grammarRules = g.getRules();
		Set<String> terminals = g.getTerminals();

		for (int i = 0; i < wordLength; i++)
		{
			cykTable.add(i, new ArrayList<Set<String>>());
			for (int j = 0; j < wordLength - i; j++)
			{
				cykTable.get(i).add(j, new HashSet<String>());
			}
		}

		for (int i = 0; i < wordLength; i++)
		{
			wordStringChars[i] = String.valueOf(wordChars[i]);
		}

		// first initial run
		for (int i = 0; i < wordLength; i++)
		{
			for (String nonTerminal : grammarRules.keySet())
			{
				for (String rightHandRule : grammarRules.get(nonTerminal))
				{
					if (terminals.contains(rightHandRule) && wordStringChars[i].equals(rightHandRule))
					{
						cykTable.get(i).get(0).add(nonTerminal);
					}
				}
			}
		}

		// algorithm body
		// j = subwordlength
		// i = beginning index
		// indexing is switched 1 down against algorithm from slides
		for (int subwordLength = 1; subwordLength < wordLength; subwordLength++)
		{
			for (int beginningIndex = 0; beginningIndex < wordLength - subwordLength; beginningIndex++)// rmv +1
			{
				for (int k = 0; k < subwordLength; k++)
				{
					for (String nonTerminal : grammarRules.keySet())
					{
						for (String rightHandRule : grammarRules.get(nonTerminal))
						{
							if (rightHandRule.length() != 2)
							{
								continue;
							}
							if (cykTable.get(beginningIndex).get(k).contains(String.valueOf(rightHandRule.charAt(0)))
								&& cykTable.get(beginningIndex + k + 1).get(subwordLength - (k + 1))
									.contains(String.valueOf(rightHandRule.charAt(1))))
							{
								cykTable.get(beginningIndex).get(subwordLength).add(nonTerminal);
							}

						}
					}
				}
			}
		}

		int emptySets = 0;
		Set<Set<String>> differentSets = new HashSet<Set<String>>();

		for (int i = 0; i < wordLength; i++)
		{
			for (int j = 0; j < wordLength - i; j++)
			{
				Set<String> cell = cykTable.get(i).get(j);
				if (cell.size() == 0)
					emptySets++;
				if (!differentSets.contains(cell))
					differentSets.add(cell);
			}
		}
		boolean generatable = false;
		if (cykTable.get(0).get(wordLength - 1).contains(g.getInitialNonTerminal()))
		{
			generatable = true;
		}

		return new CYKTable(cykTable, differentSets.size(), emptySets, generatable);
	}

	public static String printCYKTablePlain(List<List<Set<String>>> cykTable, String[] wordChars, Grammar g)
	{
		int longestString = 0;
		StringBuilder sb = new StringBuilder();
		for (List<Set<String>> list : cykTable)
		{
			for (Set<String> set : list)
			{
				if (set.size() > longestString)
				{
					longestString = set.size();
				}
			}
		}

		// adjustlongeststring with commas, brackets and spaces
		longestString = longestString + (longestString - 1) + 4;

		int cykTableSize = cykTable.size();

		for (int subwordLength = cykTableSize - 1; subwordLength >= 0; subwordLength--)
		{
			sb.append(calculateLine(cykTableSize - subwordLength, longestString));
			sb.append(LINE_SEPARATOR);
			sb.append("|");
			for (int beginningIndex = 0; beginningIndex < cykTableSize - subwordLength; beginningIndex++)
			{
				Set<String> cellSet = cykTable.get(beginningIndex).get(subwordLength);
				if (cellSet.isEmpty())
				{
					sb.append(adjustToLength("{ }", longestString));
				}
				else
				{
					sb.append(adjustToLength(setToString(cellSet), longestString));
				}
				sb.append("|");
			}
			sb.append(LINE_SEPARATOR);
		}
		sb.append(calculateLine(cykTableSize, longestString));
		sb.append(LINE_SEPARATOR);
		for (int i = 0; i < cykTableSize; i++)
		{
			sb.append(" " + adjustToLength(wordChars[i], longestString));
		}
		String finalString = sb.toString();
		// FormalLanguagesExampleGenerator.getCoreInstance().getTextAreas().get("plainCZ")
		// .setText(g + LINE_SEPARATOR + finalString);
		return finalString;
	}

	public static String printCYKTableLaTeX(List<List<Set<String>>> cykTable, String[] wordChars, Grammar g)
	{
		StringBuilder sb = new StringBuilder();

		// adjustlongeststring with commas, brackets and spaces

		int cykTableSize = cykTable.size();

		// TODO
		// \setlength{\tabcolsep}{0pt}
		// \renewcommand\arraystretch{1.3}
		// \begin{tabular}{ccccc}
		// &~~~1~~~~\\[0mm]\cline{2-2}
		// 4~~\vline&\hfill\{A,B,S\}\hfill\vline&~~~2~~~~\\[0mm]\cline{2-3}
		// 3~~\vline&\hfill\{A\}\hfill\vline&\hfill\vline&~~~3~~~~\\[0mm]\cline{2-4}
		// 2~~\vline&\hfill\vline&\hfill\vline&\hfill\vline&~~~4~~~~\\[0mm]\cline{2-5}
		// 1~~\vline&\hfill\vline&\hfill\vline&\hfill\vline&\hfill\vline\\[0mm]\cline{2-5}
		// \\[-3.5ex]
		// ~&$a$&$b$&$a$&$a$
		// \end{tabular}
		sb.append(LINE_SEPARATOR + "\\renewcommand\\arraystretch{1.3}" + LINE_SEPARATOR);
		sb.append("\\setlength{\\tabcolsep}{0pt}" + LINE_SEPARATOR);
		sb.append("\\shorthandoff{-}" + LINE_SEPARATOR);

		sb.append("\\begin{tabular}{");
		for (int i = 0; i <= cykTableSize; i++)
		{
			sb.append("c");
		}
		sb.append("}" + LINE_SEPARATOR);
		sb.append("&~~~1~~~~\\\\[0mm]\\cline{2-2}");
		sb.append(LINE_SEPARATOR);

		for (int subwordLength = cykTableSize - 1; subwordLength >= 0; subwordLength--)
		{
			sb.append(subwordLength + 1 + "~~\\vline");
			for (int beginningIndex = 0; beginningIndex < cykTableSize - subwordLength; beginningIndex++)
			{
				Set<String> cellSet = cykTable.get(beginningIndex).get(subwordLength);
				if (cellSet.isEmpty())
				{
					sb.append("&\\hfill~$\\emptyset$~\\hfill\\vline");
				}
				else
				{
					sb.append("&\\hfill~\\{");
					Iterator<String> cellIt = cellSet.iterator();
					while (cellIt.hasNext())
					{
						sb.append(cellIt.next());
						if (cellIt.hasNext())
						{
							sb.append(",");
						}
					}
					sb.append("\\}~\\hfill\\vline");
				}
			}
			if (subwordLength == 0)
			{
				sb.append("\\\\[0mm]\\cline{2-" + (cykTableSize - subwordLength + 1) + "}");
			}
			else
			{
				sb.append("&~~~" + (cykTableSize - subwordLength + 1) + "~~~~\\\\[0mm]\\cline{2-"
					+ (cykTableSize - subwordLength + 2) + "}");
			}
			sb.append(LINE_SEPARATOR);
		}
		sb.append("\\\\[-3.5ex]" + LINE_SEPARATOR);
		sb.append("~");
		for (int i = 0; i < cykTableSize; i++)
		{
			sb.append("&$" + wordChars[i] + "$");
		}

		sb.append(LINE_SEPARATOR + "\\end{tabular}");
		String finalString = sb.toString();
		// FormalLanguagesExampleGenerator.getCoreInstance().getTextAreas().get("plainCZ")
		// .setText(g + LINE_SEPARATOR + finalString);
		return finalString;
	}

	private static String calculateLine(int numberOfElements, int longestString)
	{
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < numberOfElements; j++)
		{
			for (int i = 0; i < longestString + 1; i++)
			{
				sb.append("-");
			}
			//
		}
		sb.append("+");
		// sb.replace(sb.length() - 1, sb.length(),"+");
		sb.replace(0, 1, "+");
		return sb.toString();
	}

	private static String adjustToLength(String s, int longestString)
	{
		int numberOfSpaces = ((longestString - s.length()) / 2);
		String spaces = "";
		for (int i = 0; i < numberOfSpaces; i++)
		{
			spaces += " ";
		}
		return spaces + s + spaces;
	}

	private static String setToString(Set<String> stringSet)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String string : stringSet)
		{
			sb.append(string + ",");
		}
		sb.replace(sb.length() - 1, sb.length(), "}");
		return sb.toString();
	}
}
