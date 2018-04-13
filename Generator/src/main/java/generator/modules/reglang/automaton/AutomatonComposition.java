/*
 * This class making the synchronous parallel composition (or complement).
 */

package generator.modules.reglang.automaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The class AutomatonComposition makes the parallel composition of two languages of the given DFAs or the complemet of
 * the given DFA
 * 
 * @author Jana Kadlecova
 */
public class AutomatonComposition
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private AutomatonComposition()
	{
	}

	/**
	 * makes the new automaton as the union of two given automatons. The automaton does not have unreachable states and
	 * has total transtion function.
	 * 
	 * @param automaton1
	 *            the deterministic automaton with total transition function
	 * @param automaton2
	 *            the deterministic automaton with total transition function and the same alphabet as the automaton1
	 * @return new deterministic automaton without unreachable states, which is the union of two given automatons
	 * @throws NullPointerException
	 *             some of given parameters is null
	 * @throws IllegalArgumentException
	 *             some of given parameters is not the automaton or is nondeterministic automaton or has not total
	 *             transition function or the alphabets of automatons are different
	 */
	public static Automaton union(Automaton automaton1, Automaton automaton2)
	{
		AutomatonComposition.checkInput(automaton1);
		AutomatonComposition.checkInput(automaton2);
		Automaton a1 = new Automaton(automaton1);
		Automaton a2 = new Automaton(automaton2);
		Automaton a = AutomatonComposition.composition(automaton1, automaton2);
		Set<String> finalStates1 = a1.getFinalStates();
		Set<String> finalStates2 = a2.getFinalStates();
		for (String state : a.getStates())
		{
			String state1 = AutomatonComposition.getPartOfState(state, true);
			String state2 = AutomatonComposition.getPartOfState(state, false);
			if (finalStates1.contains(state1) || finalStates2.contains(state2))
			{
				try
				{
					a.addToFinalStates(state);
				}
				catch (NoSuchStateException e)
				{
					throw new Error("AutomatonComposition.union(): " + e.getMessage());
				}
			}
		}
		return a;
	}

	/**
	 * makes the new automaton as the intersection of two given automatons. The automaton does not have unreachable
	 * states and has total transtion function.
	 * 
	 * @param automaton1
	 *            the deterministic automaton with total transition function
	 * @param automaton2
	 *            the deterministic automaton with total transition function and the same alphabet as the automaton1
	 * @return new deterministic automaton without unreachable states, which is the intersection of two given automatons
	 * @throws NullPointerException
	 *             some of given parameters is null
	 * @throws IllegalArgumentException
	 *             some of given parameters is not the automaton or is nondeterministic automaton or has not total
	 *             transition function or the alphabets of automatons are different
	 */
	public static Automaton intersection(Automaton automaton1, Automaton automaton2)
	{
		AutomatonComposition.checkInput(automaton1);
		AutomatonComposition.checkInput(automaton2);
		Automaton a1 = new Automaton(automaton1);
		Automaton a2 = new Automaton(automaton2);
		Automaton a = AutomatonComposition.composition(automaton1, automaton2);
		Set<String> finalStates1 = a1.getFinalStates();
		Set<String> finalStates2 = a2.getFinalStates();
		for (String state : a.getStates())
		{
			String state1 = AutomatonComposition.getPartOfState(state, true);
			String state2 = AutomatonComposition.getPartOfState(state, false);
			if (finalStates1.contains(state1) && finalStates2.contains(state2))
			{
				try
				{
					a.addToFinalStates(state);
				}
				catch (NoSuchStateException e)
				{
					throw new Error("AutomatonComposition.intersection(): " + e.getMessage());
				}
			}
		}
		return a;
	}

	/**
	 * makes the new automaton as the difference of two given automatons. The automaton does not have unreachable states
	 * and has total transtion function.
	 * 
	 * @param automaton1
	 *            the deterministic automaton with total transition function
	 * @param automaton2
	 *            the deterministic automaton with total transition function and the same alphabet as the automaton1
	 * @return new deterministic automaton without unreachable states, which is the difference of two given automatons
	 * @throws NullPointerException
	 *             some of given parameters is null
	 * @throws IllegalArgumentException
	 *             some of given parameters is not the automaton or is nondeterministic automaton or has not total
	 *             transition function or the alphabets of automatons are different
	 */
	public static Automaton difference(Automaton automaton1, Automaton automaton2)
	{
		AutomatonComposition.checkInput(automaton1);
		AutomatonComposition.checkInput(automaton2);
		Automaton a1 = new Automaton(automaton1);
		Automaton a2 = new Automaton(automaton2);
		Automaton a = composition(automaton1, automaton2);
		Set<String> finalStates1 = a1.getFinalStates();
		Set<String> finalStates2 = a2.getFinalStates();
		for (String state : a.getStates())
		{
			String state1 = AutomatonComposition.getPartOfState(state, true);
			String state2 = AutomatonComposition.getPartOfState(state, false);
			if (finalStates1.contains(state1) && !finalStates2.contains(state2))
			{
				try
				{
					a.addToFinalStates(state);
				}
				catch (NoSuchStateException e)
				{
					throw new Error("AutomatonComposition.difference(): " + e.getMessage());
				}
			}
		}
		return a;
	}

	/**
	 * makes the complement of the given automaton
	 * 
	 * @param automaton
	 *            the deterministic automaton with total transition function
	 * @return the new deterministic automaton with total transition function, which is the complement of the given
	 *         automaton
	 * @throws NullPointerException
	 *             some of given parameters is null
	 * @throws IllegalArgumentException
	 *             given parameter is not automaton or is nondeterministic automaton or has not total transition
	 *             function
	 */
	public static Automaton complement(Automaton automaton)
	{
		AutomatonComposition.checkInput(automaton);
		Automaton a = new Automaton(automaton);
		Automaton co = new Automaton(a);
		for (String state : co.getStates())
		{
			try
			{
				if (a.getFinalStates().contains(state))
				{
					co.removeFinalState(state);
				}
				else
				{
					co.addToFinalStates(state);
				}
			}
			catch (NoSuchStateException e)
			{
				throw new Error("AutomatonComposition.complement(): " + e.getMessage());
			}
		}
		return co;
	}

	/**
	 * makes the new automaton as the composition of two given automatons. The automaton does not have unreachable
	 * states and has total transtion function and has empty set of final states
	 * 
	 * @param a1
	 *            the deterministic automaton with total transition function
	 * @param a2
	 *            the deterministic automaton with total transition function
	 * @return new deterministic automaton without unreachable states, which is the composition of two given automatons
	 *         and without final states
	 * @throws NullPointerException
	 *             some of given parameters is null
	 * @throws IllegalArgumentException
	 *             some of given parameters is not the automaton or is nondeterministic automaton or has not total
	 *             transition function or the alphabets of automatons are different
	 */
	private static Automaton composition(Automaton a1, Automaton a2)
	{
		if (!a1.getAlphabet().equals(a2.getAlphabet()))
		{
			throw new IllegalArgumentException("AutomatonComposition.union(): "
				+ "given automatons have different alphabets " + a1 + "\n" + a2);
		}
		// new builded state is in form: (state1, state2)
		StringBuilder sbState = new StringBuilder();
		// start state of the new automaton a
		sbState.append("(" + a1.getStartState()).append(", ");
		sbState.append(a2.getStartState() + ")");
		Automaton a = new Automaton(sbState.toString());
		List<String> newStates = new ArrayList<String>(); // list of new states
		newStates.add(sbState.toString());
		while (!newStates.isEmpty())
		{
			String newState = newStates.get(0);
			newStates.remove(newState);
			// first part of the new state is state1 (of automaton a1)
			String state1 = AutomatonComposition.getPartOfState(newState, true);
			// second part of the new state is state2 (of automaton a2)
			String state2 = AutomatonComposition.getPartOfState(newState, false);
			for (String under : a1.getAlphabet())
			{
				sbState = new StringBuilder();
				try
				{
					sbState.append("(" + a1.getStates(state1, under).toString().replace("[", "").replace("]", ""));
					sbState.append(", ");
					sbState.append(a2.getStates(state2, under).toString().replace("[", "").replace("]", "") + ")");
					if (!a.containsState(sbState.toString()))
					{
						newStates.add(sbState.toString());
						a.addState(sbState.toString());
					}
					a.addTransition(newState, under, sbState.toString());
				}
				catch (NoSuchStateException e)
				{
					throw new Error("AutomatonComposition: " + e.getMessage());
				}
			}
		}
		return a;
	}

	/**
	 * returns the first part(state1) or the second part(state2) of the state, which is in format: state = (state1,
	 * state2)
	 * 
	 * @param state
	 *            state in format: state = (state1, state2)
	 * @param first
	 *            if true, returns the first part else return the second part
	 * @return returns the first part(state1) or the second part(state2) of the state, which is in format: state =
	 *         (state1, state2)
	 */
	private static String getPartOfState(String state, boolean first)
	{
		String[] result = state.split(", ");
		if (first)
		{
			return result[0].replace("(", "");
		}
		return result[1].replace(")", "");
	}

	/**
	 * checks the input automaton
	 * 
	 * @param automaton
	 *            the deterministic automaton with total transition function
	 * @throws NullPointerException
	 *             some of given parameters is null
	 * @throws IllegalArgumentException
	 *             given parameter is not automaton or is nondeterministic automaton or has not total transition
	 *             function
	 */
	private static void checkInput(Automaton a)
	{
		if (a == null)
		{
			throw new NullPointerException("AutomatonComposition");
		}
		if (!(a instanceof Automaton))
		{
			throw new IllegalArgumentException("AutomatonComposition: "
				+ "given parameter is not instance of automaton " + a);
		}
		if (!a.isDeterministic())
		{
			throw new IllegalArgumentException("AutomatonComposition: " + "given automaton is nondeterministic " + a);
		}
		if (!a.isTransitionFunctionTotal())
		{
			throw new IllegalArgumentException("AutomatonComposition: "
				+ "given automaton has not total transition functions " + a);
		}
	}
}
