
package generator.modules.reglang;

import generator.modules.reglang.automaton.Automaton;
import generator.modules.reglang.automaton.NoSuchStateException;
import generator.modules.reglang.regularGrammar.NoSuchVariableException;
import generator.modules.reglang.regularGrammar.RegularGrammar;
import java.util.List;

public class GrammarTransformator
{
	public static Automaton convertGrammarToAutomaton(RegularGrammar g) throws NoSuchVariableException,
		NoSuchStateException
	{
		Automaton a = new Automaton(g.getStartVariable());
		a.setIsNFA(true);
		// add artifical final state
		a.addState("qf");
		a.addToFinalStates("qf");
		for (String nonterminal : g.getVariables())
		{
			a.addState(nonterminal);
			for (List<String> rightHandRules : g.getRulesFromTheVariable(nonterminal))
			{
				if (rightHandRules.size() == 1)
				{
					if (nonterminal.equals(g.getStartVariable()) && g.containsEpsilon())
					{
						a.addToFinalStates(g.getStartVariable());
					}
					else
					{
						a.addTransition(nonterminal, rightHandRules.get(0), "qf");
					}
				}
				else
				{
					if (!a.getStates().contains(rightHandRules.get(1)))
					{
						a.addState(rightHandRules.get(1));
					}
					a.addTransition(nonterminal, rightHandRules.get(0), rightHandRules.get(1));
				}

			}
		}
		return a;
	}
}
