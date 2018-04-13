/*
 * Converts the given automaton to regular grammar
 */

package generator.modules.reglang.regularGrammar;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import generator.modules.reglang.automaton.Automaton;
import generator.modules.reglang.automaton.NoSuchStateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * The class stands the convertor, which transfers the regular grammar to the finite automaton and vice versa
 * 
 * @author Jana Kadlecova
 */
public class RegularGrammarConverter
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private RegularGrammarConverter()
	{
	}

	/**
	 * Returns the regular grammar, to which is converted the given finite automaton without epsilon-transitions
	 * 
	 * @param automaton
	 *            the finite automaton without epsilon-transitions
	 * @return the regular grammar, to which is converted the given finite automaton
	 * @throws NullPointerException
	 *             the given parameter is null
	 * @throws IllegalArgumnetException
	 *             the given parameter is not instance of the class Automaton or is NFA with epsilon-transitions
	 */
	public static RegularGrammar convertAutomatonToRegularGrammar(Automaton automaton)
	{
		if (automaton == null)
		{
			throw new NullPointerException("RegularGrammarConvertor." + "convertAutomatonToRegularGrammar()");
		}
		if (!(automaton instanceof Automaton))
		{
			throw new IllegalArgumentException("RegularGrammarConvertor." + "convertAutomatonToRegularGrammar(): "
				+ "the given parameter is not the automaton.");
		}
		if (automaton.containsEpsilonTransitions())
		{
			throw new IllegalArgumentException("RegularGrammarConvertor." + "convertAutomatonToRegularGrammar()"
				+ "the given automaton contains epsilon transitions.");
		}
		// makes copy of the given automaton
		Automaton a = new Automaton(automaton);
		// states of automaton
		ArrayList<String> states = new ArrayList<String>(AlphabetGenerator.sortSet(a.getStates(), a.getStatesType()));
		ArrayList<String> variables = RegularGrammarConverter.addApostroph(states);

		String newStartVariable; // start variable of the new regular grammar
		if (a.getFinalStates().contains(a.getStartState()))
		{ // the rule S -> epsilon
			int count = states.size() + 1;
			SortedSet<String> states2 = null;
			switch (a.getStatesType())
			{
				case 5:
					states2 = AlphabetGenerator.getLetterWithIndex(count, "q");
					break;
				case 6:
					states2 = AlphabetGenerator.getLetterWithIndex(count, "s");
					break;
				default:
					states2 = AlphabetGenerator.getNewSortedAlphabet(a.getStatesType(), count);
			}
			states2.removeAll(states);
			StringBuilder sb = new StringBuilder().append(states2.first());
			while (a.getAlphabet().contains(sb.toString()))
			{
				sb.append("'");
			}
			newStartVariable = sb.toString();
		}
		else
		{
			newStartVariable = variables.get(states.indexOf(a.getStartState()));
		}
		// initialize regular grammar
		RegularGrammar g = new RegularGrammar(newStartVariable);
		g.setVariablesType(a.getStatesType());
		g.setTerminalsType(a.getAlphabetType());
		// all states are new variables of the grammar
		for (String variable : variables)
		{
			g.addVariable(variable);
		}
		// the rules are addded
		for (String from : states)
		{
			for (String under : a.getAlphabet())
			{
				try
				{
					Set<String> to = a.getStates(from, under);
					for (String t : to)
					{
						g.addRule(variables.get(states.indexOf(from)), under, variables.get(states.indexOf(t)));
						if (a.getFinalStates().contains(t))
						{
							g.addRule(variables.get(states.indexOf(from)), under);
						}
					}
				}
				catch (NoSuchStateException e)
				{
					throw new Error("RegularGrammarConvertor." + "convertAutomatonToRegularGrammar(): "
						+ e.getMessage());

				}
				catch (NoSuchVariableException e)
				{
					throw new Error("RegularGrammarConvertor." + "convertAutomatonToRegularGrammar(): "
						+ e.getMessage());
				}
			}
		}

		if (a.getFinalStates().contains(a.getStartState()))
		{
			try
			{
				for (List<String> l : g.getRulesFromTheVariable(variables.get(states.indexOf(a.getStartState()))))
				{
					if (l.size() == 2)
					{
						g.addRule(g.getStartVariable(), l.get(0), l.get(1));
					}
					else
					{
						g.addRule(g.getStartVariable(), l.get(0));
					}
				}
				g.addRule(g.getStartVariable(), "epsilon");
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammarConvertor." + "convertAutomatonToRegularGrammar()" + e.getMessage());
			}
		}

		return g;
	}

	/**
	 * Returns the finite automaton without epsilon-transitions, to which is converted the given regular grammar without
	 * epsilon-transitions
	 * 
	 * @param grammar
	 *            the regular grammar
	 * @return the finite automaton without epsilon-transitions, to which is converted the given regular grammar
	 * @throws NullPointerException
	 *             the given parameter is null
	 * @throws IllegalArgumnetException
	 *             the given parameter is not instance of the class RegularGrammar
	 */
	public static Automaton convertRegularGrammarToAutomaton(RegularGrammar grammar)
	{
		if (grammar == null)
		{
			throw new NullPointerException("RegularGrammarConvertor." + "convertRegularGrammarToAutomaton()");
		}
		if (!(grammar instanceof RegularGrammar))
		{
			throw new IllegalArgumentException("RegularGrammarConvertor." + "convertRegularGrammarToAutomaton(): "
				+ "the given parameter is not the regular grammar.");
		}
		RegularGrammar g = new RegularGrammar(grammar);
		ArrayList<String> variables = new ArrayList<String>(AlphabetGenerator.sortSet(g.getVariables(),
			g.getVariablesType()));
		ArrayList<String> states = RegularGrammarConverter.addApostroph(variables);
		Automaton a = new Automaton(states.get(variables.indexOf(g.getStartVariable())));

		for (String state : states)
		{
			a.addState(state);
		}
		StringBuilder sb = new StringBuilder().append("q_{f}");
		while (g.getVariables().contains(sb.toString()) || g.getTerminals().contains(sb.toString()))
		{
			sb.append("'");
		}
		String newFinalState = sb.toString();
		a.addState(newFinalState);
		try
		{
			if (g.containsEpsilon())
			{
				a.addToFinalStates(states.get(variables.indexOf(g.getStartVariable())));
			}
			a.addToFinalStates(newFinalState);
		}
		catch (NoSuchStateException e)
		{
			throw new Error("RegularGrammarConvertor." + "convertRegularGrammarToAutomaton(): " + e.getMessage());
		}
		for (String variable : variables)
		{
			try
			{
				Set<List<String>> lists = g.getRulesFromTheVariable(variable);
				for (List<String> l : lists)
				{
					if (l.size() == 2)
					{
						a.addTransition(states.get(variables.indexOf(variable)), l.get(0),
							states.get(variables.indexOf(l.get(1))));
					}
					else
					{
						if (!l.get(0).equals("epsilon"))
						{
							a.addTransition(states.get(variables.indexOf(variable)), l.get(0), newFinalState);
						}
					}
				}
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammarConvertor." + "convertRegularGrammarToAutomaton(): " + e.getMessage());
			}
			catch (NoSuchStateException e)
			{
				throw new Error("RegularGrammarConvertor." + "convertRegularGrammarToAutomaton(): " + e.getMessage());
			}
		}
		a.setIsNFA(true);
		return a;
	}

	/**
	 * adds the apostroph to each element of the given list and returns as the new list or empty list, if given list is
	 * empty
	 * 
	 * @param list
	 *            list of strings
	 * @return new list, which contains strings of the given list, each string has an apostroph
	 * @throws IllegalArgumentException
	 *             the parameter is not list
	 * @throws NullPointerException
	 *             the parameter automaton is null
	 */
	private static ArrayList<String> addApostroph(ArrayList<String> list)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (String s : list)
		{
			StringBuilder sb = new StringBuilder().append(s);
			while (list.contains(sb.toString()))
			{
				sb.append("'");
			}
			result.add(sb.toString());
		}
		return result;
	}

}
