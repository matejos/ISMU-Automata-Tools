/**
 * The class canonizes the DFA.
 */

package generator.modules.reglang.automaton;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The class Canonizator has the static method canonize(), which converts the given minimal DFA to the canonical form
 * 
 * @author Jana Kadlecova
 */
public class Canonizator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private Canonizator()
	{
	}

	/**
	 * Transform the given minimal DFA with total transiton function and without unreachable states to the canonical
	 * form; the names of states are A-Z (number of states <= 26) or 1-n (number of states >= 27)
	 * 
	 * @param a
	 *            the minimal DFA with the total transition function and without unreachable states
	 * @return automaton in canonical form
	 */
	public static Automaton canonize(Automaton a)
	{
		if (a == null)
		{
			throw new NullPointerException("Method Canonizator.canonize().");
		}
		if (!a.isDeterministic())
		{
			throw new IllegalArgumentException("Method Canonizator.canonize(): "
				+ "The given automaton is nondeterministic.");
		}
		if (!a.isTransitionFunctionTotal())
		{
			throw new IllegalArgumentException("Method Canonizator.canonize(): "
				+ "The given automaton has not total transition function.");
		}
		if (a.getNumberOfUnreachableStates() != 0)
		{
			throw new IllegalArgumentException("Method Canonizator.canonize(): "
				+ "The given automaton contains unreachable states");
		}
		Automaton automaton = new Automaton(a);
		automaton.setStatesType(1);
		Automaton minimalAutomaton = Minimizator.minimize(automaton);
		if (a.getStates().size() > minimalAutomaton.getStates().size())
		{
			throw new IllegalArgumentException("Method Canonizator.canonize(): " + "a given automaton is not minimal");
		}
		int countOfStates = automaton.getStates().size();
		ArrayList<String> alphabet = new ArrayList<String>(AlphabetGenerator.getUpperCase(countOfStates));
		Canonizator.renameColisionStates(automaton, alphabet);
		Queue<String> statesToCanonize = new LinkedBlockingQueue<String>();
		try
		{
			automaton.renameState(automaton.getStartState(), "A");
		}
		catch (NoSuchStateException ex)
		{
			throw new Error("Canonizator.canonize(): " + ex);
		}
		statesToCanonize.add("A");
		int i = 1;
		while (statesToCanonize.size() != 0)
		{
			String from = statesToCanonize.poll();
			for (String under : new TreeSet<String>(automaton.getAlphabet()))
			{
				try
				{
					String to = automaton.getStates(from, under).toString().replace("[", "").replace("]", "");
					if (!alphabet.contains(to))
					{
						automaton.renameState(to, alphabet.get(i));
						statesToCanonize.add(alphabet.get(i));
						i++;
					}
				}
				catch (NoSuchStateException ex)
				{
					throw new Error("Canonizator.canonize(): " + ex);
				}
			}
		}
		if (automaton.getStates().size() > 26)
		{
			AutomatonModificator.modifyStates(automaton, 2);
		}
		return automaton;
	}

	/**
	 * renames the states, which have the same name as states in new alphabet
	 * 
	 * @param automaton
	 *            automaton
	 * @param alphabet
	 *            the names of the new states (A, B, C...)
	 */
	private static void renameColisionStates(Automaton automaton, ArrayList<String> alphabet)
	{
		for (String state : new HashSet<String>(automaton.getStates()))
		{
			if (alphabet.contains(state))
			{
				String s = automaton.generateUniqueState(state);
				try
				{
					automaton.renameState(state, s);
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error("Canonizator.renameColisionStates(): " + nsse.getMessage());
				}
			}
		}
	}

}
