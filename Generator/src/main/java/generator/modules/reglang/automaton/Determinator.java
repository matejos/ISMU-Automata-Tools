/**
 * The class for transformation NFA to DFA
 */

package generator.modules.reglang.automaton;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The class converts the given NFA to the DFA with the total transition function and without unreachable states
 * 
 * @author Jana Kadlecova
 */
public class Determinator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private Determinator()
	{
	}

	/**
	 * converts NFA without epsilon transfers to DFA with the total transition function, if the given automaton is DFA,
	 * return new DFA without unreachable states and with total transition function
	 * 
	 * @param automaton
	 *            NFA without epsilon transfers
	 * @return DFA
	 */
	public static Automaton getDFA(Automaton automaton)
	{
		if (automaton == null)
		{
			throw new NullPointerException("Method Determinizator.getDFA().");
		}
		// test if epsilon transfer is present
		if (automaton.containsEpsilonTransitions())
		{
			throw new IllegalArgumentException(" Method " + "Determinizator.getDFA() : the automaton contains "
				+ "epsilon. Use function " + "EpsilonTransitionsEliminator.eliminateEpsilonTransitions().");
		}
		Automaton DFA;
		if (automaton.isDeterministic())
		{
			DFA = new Automaton(automaton);
			DFA.setIsNFA(false);
			DFA.removeUnreachableStates();
			DFA.makeTransitionFunctionTotal("s_{N}");
			return DFA;
		}
		DFA = new Automaton(automaton.getStartState());
		if (automaton.getFinalStates().contains(automaton.getStartState()))
		{
			try
			{
				DFA.addToFinalStates(automaton.getStartState());
			}
			catch (NoSuchStateException e)
			{
				throw new Error("Determinizator.getDFA(): " + e.getMessage());
			}
		}
		// key is new state, value is a string from states, which build this new
		// state
		Map<String, Set<String>> newStates = new HashMap<String, Set<String>>();
		Set<String> oldStates = new HashSet<String>();

		while (oldStates.size() != DFA.getStates().size())
		{
			oldStates = new HashSet<String>(DFA.getStates());
			Set<String> statesDFA = new HashSet<String>(DFA.getStates());
			for (String from : statesDFA)
			{
				for (String under : automaton.getAlphabet())
				{
					Set<String> to; // to which states can we reach under
					if (automaton.containsState(from))
					{
						try
						{
							to = AlphabetGenerator.sortSet(automaton.getStates(from, under), automaton.getStatesType());
						}
						catch (NoSuchStateException nsse)
						{
							throw new Error("Method Determinizator.getDFA(): " + nsse.getMessage());
						}
					}
					else
					{
						to = new TreeSet<String>(AlphabetGenerator.getComparator(automaton.getStatesType()));
						for (String state : newStates.get(from))
						{
							try
							{
								to.addAll(automaton.getStates(state, under));
							}
							catch (NoSuchStateException nsse)
							{
								throw new Error("Method " + "Determinizator.getDFA(): " + nsse.getMessage());
							}
						}
					}
					if (to.size() == 1)
					{ // only one state is added
						for (String addedState : to)
						{
							DFA.addState(addedState);
							try
							{
								if (automaton.getFinalStates().contains(addedState))
								{
									DFA.addToFinalStates(addedState);
								}
								DFA.addTransition(from, under, addedState);
							}
							catch (NoSuchStateException nsse)
							{
								throw new Error("Method Determinizator.getDFA(): " + nsse.getMessage());
							}
						}
					}
					else
					{
						if (to.size() > 1)
						{ // new state is built
							String newState = "";
							for (String state : to)
							{
								newState = newState.concat(state);
							}
							// if automaton contains states ze, bra, zebra
							// new state zebra' is generated
							if (automaton.containsState(newState))
							{
								newState = automaton.generateUniqueState(newState);
							}
							if (!DFA.containsState(newState))
							{
								DFA.addState(newState);
								newStates.put(newState, to);
							}
							// new state is added to final states, if one state,
							// it consists of, is final
							for (String state : to)
							{
								if (automaton.getFinalStates().contains(state))
								{
									try
									{
										DFA.addToFinalStates(newState);
									}
									catch (NoSuchStateException nsse)
									{
										throw new Error("Method " + "Determinizator.getDFA(): " + nsse.getMessage());
									}
									break;
								}
							}
							try
							{
								DFA.addTransition(from, under, newState);
							}
							catch (NoSuchStateException nsse)
							{
								throw new Error("Method Determinizator.getDFA(): " + nsse.getMessage());
							}
						}
					}
				}
			}
		}
		DFA.setIsNFA(false);
		DFA.setAlphabetType(automaton.getAlphabetType());
		DFA.setStatesType(automaton.getStatesType());
		DFA.makeTransitionFunctionTotal("s_{N}");
		return DFA;
	}
}
