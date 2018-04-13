
package generator.modules.reglang.regularGrammar;

import generator.common.tools.CommonUtils;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.reglang.automaton.NoSuchStateException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author JUH Generator for generating regular grammars
 */
public class RegularGrammarGenerator
{

	public static RegularGrammar generateGrammar(int minRules, int maxRules, int minTerminals, int maxTerminals,
		int minNonterminals, int maxNonterminals, boolean hasEps, int minLoops, int maxLoops)
		throws NoSuchVariableException, NoSuchStateException
	{
		boolean terminalUsed = false;
		int usedLoops = 0;
		RegularGrammar g = new RegularGrammar("S");
		Random rand = new Random();
		Set<String> usedNonTerminals = new HashSet<String>();
		Set<String> unusedTerminals = new HashSet<String>();

		Set<String> terminableNonterminals = new HashSet<String>();

		int nonterminals;

		do
		{
			terminableNonterminals = new HashSet<String>();
			usedNonTerminals = new HashSet<String>();
			unusedTerminals = new HashSet<String>();
			terminalUsed = false;
			int rules = CommonUtils.randInt(minRules, maxRules, rand);
			int terminals = CommonUtils.randInt(minTerminals, maxTerminals, rand);
			nonterminals = CommonUtils.randInt(minNonterminals, maxNonterminals, rand);

			g = new RegularGrammar("S");
			usedLoops = 0;
			usedNonTerminals = new HashSet<String>();

			for (int i = 0; i < nonterminals - 1; i++)
			{
				g.addNonTerminal(String.valueOf((char) ('A' + i)));
			}

			Set<String> grammarTerminals = new HashSet<String>();
			for (int i = 0; i < terminals; i++)
			{
				grammarTerminals.add(String.valueOf((char) ('a' + i)));
			}
			g.setTerminals(grammarTerminals);
			unusedTerminals.addAll(grammarTerminals);

			int usedRules = 0;

			List<String> unreachableNonterminals = new ArrayList<>(g.getNonterminals());
			List<String> newlyReachedNonterminals = new ArrayList<String>();
			newlyReachedNonterminals.add("S");
			CommonUtils.removeElementFromList(unreachableNonterminals, "S");

			if (hasEps)
			{
				g.addRule("S", "epsilon");
				terminableNonterminals.add("S");
				usedRules++;
			}
			usedNonTerminals.add("S");
			// dopredne prechody
			while (!unreachableNonterminals.isEmpty())
			{
				List<String> newNonterminals = new ArrayList<String>();
				boolean newNonTermFound = false;
				for (String string : newlyReachedNonterminals)
				{
					int rulesAddedPerNonTerm = 0;
					for (String terminal : grammarTerminals)
					{
						if (rand.nextBoolean())
						{
							continue;
						}
						if (unreachableNonterminals.size() == 0 || rulesAddedPerNonTerm > 0)
						{
							break;
						}
						try
						{
							String randomUnusedNonterm = unreachableNonterminals.get(CommonUtils.randInt(0,
								unreachableNonterminals.size() - 1, rand));
							if (g.addRule(string, terminal, randomUnusedNonterm))
							{
								unusedTerminals.remove(terminal);
								usedNonTerminals.add(string);
								usedRules++;
								rulesAddedPerNonTerm++;
								newNonterminals.add(randomUnusedNonterm);
								newNonTermFound = true;
								CommonUtils.removeElementFromList(unreachableNonterminals, randomUnusedNonterm);
							}

						}
						catch (NoSuchVariableException e)
						{
							e.printStackTrace();
						}
					}
				}
				if (newNonTermFound)
					newlyReachedNonterminals = newNonterminals;
			}

			List<String> grammarNonterminalsAll = new ArrayList<String>(g.getNonterminals());
			List<String> grammarNonterminalsWithoutInitial = new ArrayList<String>(g.getNonterminals());
			CommonUtils.removeElementFromList(grammarNonterminalsWithoutInitial, "S");
			List<String> grammarTerminalsList = new ArrayList<String>(grammarTerminals);

			Set<String> nonterminableNonterminals = new HashSet<>();

			List<String> unusedNonterminals = new ArrayList<String>();
			if (hasEps)
			{
				unusedNonterminals.addAll(grammarNonterminalsWithoutInitial);
				nonterminableNonterminals.addAll(grammarNonterminalsWithoutInitial);
			}
			else
			{
				unusedNonterminals.addAll(grammarNonterminalsAll);
				nonterminableNonterminals.addAll(grammarNonterminalsAll);
			}
			unusedNonterminals.removeAll(usedNonTerminals);

			// zpetne + loopy
			int safeguard = 0;
			while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && usedRules < rules)
			{
				String nonTerminalFrom = grammarNonterminalsAll.get(CommonUtils.randInt(0,
					grammarNonterminalsAll.size() - 1, rand));
				String nonTerminalTo = null;

				if (hasEps)
				{
					nonTerminalTo = grammarNonterminalsWithoutInitial.get(CommonUtils.randInt(0,
						grammarNonterminalsWithoutInitial.size() - 1, rand));
				}
				else
				{
					nonTerminalTo = grammarNonterminalsAll.get(CommonUtils.randInt(0,
						grammarNonterminalsAll.size() - 1, rand));
				}
				if (terminalUsed && nonterminableNonterminals.size() > 0)
				{
					Iterator<String> nonterminableNonterminalsIt = nonterminableNonterminals.iterator();
					nonTerminalFrom = nonterminableNonterminalsIt.next();
					nonterminableNonterminalsIt.remove();
				}

				String terminal = null;
				if (unusedTerminals.size() > 0)
				{
					Iterator<String> unusedtermIt = unusedTerminals.iterator();
					terminal = unusedtermIt.next();
					unusedtermIt.remove();
				}
				else
				{
					terminal = grammarTerminalsList.get(CommonUtils.randInt(0, grammarTerminals.size() - 1, rand));
				}
				if (!terminalUsed)
				{
					if (g.addRule(nonTerminalFrom, terminal))
					{
						usedRules++;
						usedNonTerminals.add(nonTerminalFrom);
						terminableNonterminals.add(nonTerminalFrom);
						terminalUsed = true;
						continue;
					}

				}

				if (rand.nextBoolean())
				{
					if (g.addRule(nonTerminalFrom, terminal, nonTerminalTo))
					{
						usedRules++;
						usedNonTerminals.add(nonTerminalFrom);

						if (nonTerminalFrom.equals(nonTerminalTo))
						{
							usedLoops++;
						}
					}
				}
				else
				{
					if (g.addRule(nonTerminalFrom, terminal))
					{
						usedRules++;
						usedNonTerminals.add(nonTerminalFrom);
						terminableNonterminals.add(nonTerminalFrom);
					}
				}
				safeguard++;
				if (safeguard > 50)
				{
					break;
				}

			}

		}
		while ((FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
			&& (!terminalUsed || !(usedLoops < maxLoops && usedLoops >= minLoops) || usedNonTerminals.size() != nonterminals));

		return g;

	}

}
