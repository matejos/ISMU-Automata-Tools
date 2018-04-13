
package generator.modules.cyk;

import generator.common.tools.CommonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author JUH
 */
public class CYKHelper
{

	public static Grammar generateGrammarInCNFBasedOnParameters(String word, int minNonTerm, int maxNonTerm)
	{
		Random rand = new Random();
		GrammarManager gm = new GrammarManagerImpl();
		Grammar g = gm.create("S");

		for (char c : word.toCharArray())
		{
			gm.addTerminal(g, String.valueOf(c));
		}

		int nonTerminalsCount = CommonUtils.randInt(minNonTerm, maxNonTerm, rand);

		String[] nonterminals = new String[nonTerminalsCount];
		nonterminals[0] = "S";
		char c = 'A';
		for (int i = 1; i < nonTerminalsCount; i++)
		{
			nonterminals[i] = String.valueOf(c);
			gm.addNonterminal(g, nonterminals[i]);
			c++;
		}

		Set<String> unusedNonterminals = new HashSet<>(g.getNonTerminals());

		List<String> nonterminalsConvertedWord = new ArrayList<String>();
		for (int i = 0; i < word.length(); i++)
		{
			String nonterminal = nonterminals[CommonUtils.randInt(0, nonTerminalsCount - 1, rand)];
			nonterminalsConvertedWord.add(nonterminal);
			unusedNonterminals.remove(nonterminal);
		}

		for (int i = 0; i < nonterminalsConvertedWord.size(); i++)
		{
			gm.addCFGRule(g, nonterminalsConvertedWord.get(i), String.valueOf(word.charAt(i)));
		}
		expandNonTerm(gm, g, rand, "S", nonterminalsConvertedWord, 0, nonterminals, unusedNonterminals);
		// expandNonTerm(gm, g, rand, "S", nonterminalsConvertedWord, 0,nonterminals);
		// artificially fill eps/other nonused terminals

		return g;
	}

	private static void expandNonTerm(GrammarManager gm, Grammar g, Random rand, String nonTerminal,
		List<String> subWordNonterminals, int treeDepth, String[] nonterminals, Set<String> unusedNonterminals)
	{
		int subwordNonterminalsSize = subWordNonterminals.size();

		int lastLeftNonTermIndex = CommonUtils.randInt(1, subwordNonterminalsSize - 1, rand);
		lastLeftNonTermIndex = lastLeftNonTermIndex - 1;
		int rightNonTermIndex = lastLeftNonTermIndex + 1;

		String leftNonTerm = subWordNonterminals.get(0);
		String rightNonTerm = subWordNonterminals.get(rightNonTermIndex);

		if (subwordNonterminalsSize > 2)
		{
			if (lastLeftNonTermIndex > 0)
			{
				// Predavani odkazem - mit unused nonterminals jako set a odebirat z nej - bude se propagovat i do
				// dalsich vetvi! - zajisti se pouziti vsech neterm!
				if (unusedNonterminals.size() > 0)
				{
					Iterator<String> nontermIt = unusedNonterminals.iterator();
					leftNonTerm = nontermIt.next();
					nontermIt.remove();
				}
				else
				{
					leftNonTerm = nonterminals[CommonUtils.randInt(1, nonterminals.length, rand) - 1];
				}
			}
			if (subwordNonterminalsSize - rightNonTermIndex > 1)
			{
				if (unusedNonterminals.size() > 0)
				{
					Iterator<String> nontermIt = unusedNonterminals.iterator();
					rightNonTerm = nontermIt.next();
					nontermIt.remove();
				}
				else
				{
					rightNonTerm = nonterminals[CommonUtils.randInt(1, nonterminals.length, rand) - 1];
				}
			}
		}
		gm.addCFGRule(g, nonTerminal, leftNonTerm + rightNonTerm);

		if (subwordNonterminalsSize > 3)
		{
			List<String> leftList = subWordNonterminals.subList(0, rightNonTermIndex);
			List<String> rightList = subWordNonterminals.subList(rightNonTermIndex, subwordNonterminalsSize);
			if (leftList.size() > 1)
				expandNonTerm(gm, g, rand, leftNonTerm, leftList, treeDepth + 1, nonterminals, unusedNonterminals);
			if (rightList.size() > 1)
				expandNonTerm(gm, g, rand, rightNonTerm, rightList, treeDepth + 1, nonterminals, unusedNonterminals);
			// random can take from both
		}
		else if (subwordNonterminalsSize > 2)
		{

			if (lastLeftNonTermIndex == 0)
			{
				List<String> listToExpand = subWordNonterminals.subList(1, 3);
				expandNonTerm(gm, g, rand, rightNonTerm, listToExpand, treeDepth + 1, nonterminals, unusedNonterminals);
			}
			else
			{
				List<String> listToExpand = subWordNonterminals.subList(0, 2);
				expandNonTerm(gm, g, rand, leftNonTerm, listToExpand, treeDepth + 1, nonterminals, unusedNonterminals);
			}

			// just one random, other is fixed.
		}

		// rest is already added (Rule A->a)
	}

	public static Set<String> generateWordsFromGrammarInCNF(Grammar grammar, int minWordLength, int maxWordLength)
	{

		Set<String> terminableNonterminals = new HashSet<String>();
		SortedMap<String, Set<String>> grammarRules = grammar.getRules();
		Set<String> grammarTerminals = grammar.getTerminals();
		Set<String> grammarNonTerminals = grammar.getNonTerminals();

		for (String nonterminal : grammarRules.keySet())
		{
			for (String terminal : grammarTerminals)
			{
				if (grammarRules.get(nonterminal).contains(terminal))
				{
					terminableNonterminals.add(nonterminal);
				}
			}
		}

		Set<String> derivedSentencialForms = new HashSet<String>();
		derivedSentencialForms.add(grammar.getInitialNonTerminal());

		for (int i = 0; i < maxWordLength - 1; i++)
		{
			derivedSentencialForms.addAll(generateNextDerivation(derivedSentencialForms, grammarRules));
		}
		Iterator<String> wordsIt = derivedSentencialForms.iterator();
		while (wordsIt.hasNext())
		{
			String word = wordsIt.next();
			if (word.length() < minWordLength)
			{
				wordsIt.remove();
				continue;
			}
			char[] wordChars = word.toCharArray();
			String[] wordCharsAsStrings = new String[wordChars.length];
			for (int i = 0; i < wordChars.length; i++)
			{
				wordCharsAsStrings[i] = Character.toString(wordChars[i]);
			}
			for (String string : wordCharsAsStrings)
			{
				if (!terminableNonterminals.contains(string) && !grammarTerminals.contains(string))
				{
					wordsIt.remove();
					break;
				}
			}
		}

		Set<String> finalWords = new HashSet<String>(derivedSentencialForms);

		for (int k = 0; k < maxWordLength; k++)
		{
			Set<String> finalWordsCopy = new HashSet<String>();
			Iterator<String> finWordsIt = finalWords.iterator();
			while (finWordsIt.hasNext())
			{
				String string = finWordsIt.next();

				boolean hasNonterm = false;
				char[] stringChars = string.toCharArray();
				String[] stringCharsStrings = new String[stringChars.length];
				for (int i = 0; i < stringChars.length; i++)
				{
					stringCharsStrings[i] = Character.toString(stringChars[i]);
				}
				for (int i = 0; i < stringCharsStrings.length; i++)
				{
					if (grammarNonTerminals.contains(stringCharsStrings[i]))
					{
						hasNonterm = true;
						for (String rightHandRule : grammarRules.get(stringCharsStrings[i]))
						{
							if (grammarTerminals.contains(rightHandRule))
							{
								StringBuilder sb = new StringBuilder();
								for (int j = 0; j < stringCharsStrings.length; j++)
								{
									if (i == j)
									{
										sb.append(rightHandRule);
										continue;
									}
									sb.append(stringCharsStrings[j]);
								}
								finalWordsCopy.add(sb.toString());
							}
						}
					}
				}
				if (hasNonterm)
				{
					finWordsIt.remove();
				}
				else
				{
					finalWordsCopy.add(string);
				}
			}
			finalWords = finalWordsCopy;
		}

		return finalWords;
	}

	private static Set<String> generateNextDerivation(Set<String> previousDerivationResult,
		SortedMap<String, Set<String>> grammarRules)
	{
		Set<String> newDerivation = new HashSet<String>();
		for (String string : previousDerivationResult)
		{
			char[] stringChars = string.toCharArray();
			String[] stringCharsStrings = new String[stringChars.length];
			for (int i = 0; i < stringChars.length; i++)
			{
				stringCharsStrings[i] = Character.toString(stringChars[i]);
			}
			for (int i = 0; i < stringCharsStrings.length; i++)
			{
				if (grammarRules.containsKey(stringCharsStrings[i]))
				{
					for (String rightHandRule : grammarRules.get(stringCharsStrings[i]))
					{
						StringBuilder sb = new StringBuilder();
						for (int j = 0; j < stringCharsStrings.length; j++)
						{
							if (i == j)
							{
								sb.append(rightHandRule);
								continue;
							}
							sb.append(stringCharsStrings[j]);
						}
						newDerivation.add(sb.toString());
					}
				}
			}
		}
		return newDerivation;
	}

	public static Grammar removeRandomRuleFromGrammar(Grammar g)
	{
		SortedMap<String, Set<String>> rules = g.getRules();
		String initialNonterm = g.getInitialNonTerminal();
		Random rand = new Random();

		Map<String, Set<String>> rulesWithoutInitial = new HashMap<>(rules);
		rulesWithoutInitial.remove(initialNonterm);

		boolean ruleRemoved = false;
		boolean removeNonterminalRowFromRules = false;
		String nonterminalToRemoveRuleFrom = null;
		String ruleToRemove = null;

		while (!ruleRemoved)
		{
			Iterator<Entry<String, Set<String>>> rulesEntryIt = rulesWithoutInitial.entrySet().iterator();
			while (rulesEntryIt.hasNext())
			{
				Entry<String, Set<String>> entry = rulesEntryIt.next();
				if (rand.nextBoolean())
				{
					Iterator<String> ruleIt = entry.getValue().iterator();
					nonterminalToRemoveRuleFrom = entry.getKey();
					ruleToRemove = ruleIt.next();
					if (entry.getValue().size() == 1)
						removeNonterminalRowFromRules = true;
					ruleRemoved = true;
					break;
				}
			}
		}

		if (removeNonterminalRowFromRules)
		{
			rules.remove(nonterminalToRemoveRuleFrom);
		}
		else
		{
			rules.get(nonterminalToRemoveRuleFrom).remove(ruleToRemove);
		}
		return g;
	}

}
