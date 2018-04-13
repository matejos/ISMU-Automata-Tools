/**
 * The class for transformation NFA with epsilon-transitions to NFA without them.
 */

package generator.modules.reglang.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class converts NFA with epsilon-transitions to NFA without epsilon-transitions
 * 
 * @author Jana Kadlecova
 */
public class EpsilonTransitionsEliminator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private EpsilonTransitionsEliminator()
	{
	}

	/**
	 * eliminates epsilon transfers, if the given automaton is without epsilon transfers, do nothing
	 * 
	 * @param a
	 *            automaton to elimination epsilon transfers
	 * @return NFA without epsilon transfers
	 */
	public static Automaton eliminateEpsilonTransitions(Automaton a)
	{
		if (a == null)
		{
			throw new NullPointerException("Method EpsilonTransitionsEliminator." + "eliminateEpsilonTransitions().");
		}
		Automaton automaton = new Automaton(a);
		// given automaton is deterministic
		if (!automaton.containsEpsilonTransitions())
		{
			return automaton;
		}

		// states, which can be reached under epsilon from each state
		Map<String, Set<String>> epsilonTransitions = new HashMap<String, Set<String>>();
		// states of automaton
		Set<String> states = new HashSet<String>(automaton.getStates());
		// alphabet of automaton
		Set<String> alphabet = new HashSet<String>(automaton.getAlphabet());
		// transfers of the automaton without epsilon
		Map<String, Map<String, Set<String>>> transfers = new HashMap<String, Map<String, Set<String>>>();
		for (String state : states)
		{
			transfers.put(state, new HashMap<String, Set<String>>());
			epsilonTransitions.put(state, new HashSet<String>());
			// each state itself can be reached under epsilon
			epsilonTransitions.get(state).add(state);
			try
			{
				// adds all states which can be reached from one state under epsilon
				epsilonTransitions.get(state).addAll(automaton.getStates(state, "epsilon"));
				for (String t : new HashSet<String>(epsilonTransitions.get(state)))
				{
					epsilonTransitions.get(state).addAll(automaton.getStates(t, "epsilon"));
				}
			}
			catch (NoSuchStateException nsse)
			{
				throw new Error("Method EpsilonTransitionsEliminator." + "eliminateEpsilonTransitions(): "
					+ nsse.getMessage());
			}
		}
		automaton.setEpsSurrounding(epsilonTransitions);
		// if De(start state) contains final state, is start state
		// added to final
		for (String s : epsilonTransitions.get(automaton.getStartState()))
		{
			if (automaton.getFinalStates().contains(s))
			{
				try
				{
					automaton.addToFinalStates(automaton.getStartState());
				}
				catch (NoSuchStateException e)
				{
					throw new Error("EpsilonTransitionsEliminator." + "eliminateEpsilonTransitions(): "
						+ e.getMessage());
				}
			}
		}

		// /
		for (String from : states)
		{
			// S0 all states, wich can be reached from one state under epsilon
			Set<String> S0 = new HashSet<String>();
			S0.addAll(epsilonTransitions.get(from));
			for (String under : alphabet)
			{
				Set<String> S1 = new HashSet<String>();
				Set<String> S2 = new HashSet<String>();

				// S1 states, which can be reached from S0 under character a
				for (String state : S0)
				{
					try
					{
						S1.addAll(automaton.getStates(state, under));
					}
					catch (NoSuchStateException nsse)
					{
						throw new Error(nsse);
					}
				}
				// S2 states, which can be reached under character a, when
				// epsilon transfers will be eliminated
				for (String state2 : S1)
				{
					S2.addAll(epsilonTransitions.get(state2));
				}
				transfers.get(from).put(under, S2);
			}
		}
		for (String from : states)
		{
			// epsilon transfers are eliminated
			try
			{
				if (!automaton.getStates(from, "epsilon").isEmpty())
				{
					automaton.removeTransition(from, "epsilon");
				}
			}
			catch (NoSuchStateException nsse)
			{
				throw new Error(nsse);
			}
			catch (NoSuchTransitionException nste)
			{
				throw new Error(nste);
			}
			for (String under : alphabet)
			{
				for (String to : transfers.get(from).get(under))
				{
					// new transfers
					try
					{
						automaton.addTransition(from, under, to);
					}
					catch (NoSuchStateException nsse)
					{
						throw new Error(nsse);
					}
				}

			}
		}
		automaton.setIsNFA(true);
		return automaton;
	}
}
