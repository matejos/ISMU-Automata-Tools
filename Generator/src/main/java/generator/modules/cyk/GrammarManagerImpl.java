
package generator.modules.cyk;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

public class GrammarManagerImpl implements GrammarManager
{

	public Grammar create(String initialNonTerminal)
	{
		return new Grammar(initialNonTerminal);
	}

	public void addCFGRule(Grammar grammar, String leftSideNonTerminal, String rightSideString)
	{
		SortedMap<String, Set<String>> leftSideNonTerminals = grammar.getRules();
		if (!leftSideNonTerminals.keySet().contains(leftSideNonTerminal))
		{
			if (!grammar.getNonTerminals().contains(leftSideNonTerminal))
			{
				grammar.getNonTerminals().add(leftSideNonTerminal);
			}
			leftSideNonTerminals.put(leftSideNonTerminal, new HashSet<String>());
			// create rule (don't forget to add terminals!!)
		}
		addRuleExtendNonTerminalsAndTerminals(grammar, leftSideNonTerminal, rightSideString);

	}

	private void addRuleExtendNonTerminalsAndTerminals(Grammar grammar, String leftSideNonTerminal,
		String rightSideString)
	{
		Set<String> grammarTerminals = grammar.getTerminals();
		Set<String> grammarNonTerminals = grammar.getNonTerminals();

		char[] rightRuleSideCharArray = rightSideString.toCharArray();
		for (char c : rightRuleSideCharArray)
		{
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))
			{
				if (!grammarTerminals.contains(String.valueOf(c)))
				{
					grammarTerminals.add(String.valueOf(c));
				}
			}
			else if (c >= 'A' && c <= 'Z')
			{
				if (!grammarNonTerminals.contains(String.valueOf(c)))
				{
					grammarNonTerminals.add(String.valueOf(c));
				}
			}
			else
			{
				System.err.println(c);
				System.err.println("Given righthand char is not supported.");
				throw new UnsupportedOperationException("Given right side char is not supported");
			}
		}
		grammar.getRules().get(leftSideNonTerminal).add(rightSideString);
	}

	public void addTerminal(Grammar g, String terminal)
	{
		g.getTerminals().add(terminal);
	}

	public void addNonterminal(Grammar g, String nonTerminal)
	{
		g.getNonTerminals().add(nonTerminal);

	}
}
