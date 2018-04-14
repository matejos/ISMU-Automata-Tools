/**
 * The finite automaton.
 */

package generator.modules.reglang.automaton;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The class Automaton represents the finite automaton.
 * 
 * @author Jana Kadlecova
 */
public class Automaton
{

	private Map<String, Map<String, Set<String>>> transitions;
	private Map<String, Set<String>> epsSurrounding;
	private Set<String> finalStates;
	private Set<String> unreachableStates = new HashSet<String>();
	private int unreachableTransitionsCount = 0;
	private String startState;
	private String name = "M";
	private int alphabetType;
	private int statesType;

	private boolean isNFA = false;

	public int getUnreachTransCount()
	{
		return unreachableTransitionsCount;
	}

	public void increaseUnreachTransCount()
	{
		unreachableTransitionsCount++;
	}

	/**
	 * sets the automaton as the NFA
	 * 
	 * @param isNFA
	 *            the automaton is nondeterministic
	 */
	public void setIsNFA(boolean isNFA)
	{
		this.isNFA = isNFA;
	}

	public void setEpsSurrounding(Map<String, Set<String>> epsSurrounding)
	{
		this.epsSurrounding = epsSurrounding;
	}

	public void addUnreachableState(String s)
	{
		unreachableStates.add(s);
	}

	/**
	 * Constructor creates a new automaton with one (start) state
	 * 
	 * @param startState
	 *            the name of the start state
	 * @throws NullPointerException
	 *             the start state is null
	 * @throws IllegalArgumentException
	 *             the start state is an empty string
	 * @throws ClassCastException
	 *             start state is not a string
	 */
	public Automaton(String startState)
	{
		// check the input
		if (startState == null)
		{
			throw new NullPointerException("Automaton : a start state " + "is null.");
		}
		if (startState.isEmpty())
		{
			throw new IllegalArgumentException("Automaton: a name of " + "the start state is empty.");
		}
		if (!(startState instanceof String))
		{
			throw new ClassCastException("start state");
		}
		// initialize the atributes
		this.transitions = new HashMap<String, Map<String, Set<String>>>();
		this.finalStates = new HashSet<String>();
		this.transitions.put(startState, new HashMap<String, Set<String>>());
		this.startState = startState;
		this.alphabetType = 0; // a, b, c..
		this.statesType = 6; // s0, s1, s2...
	}

	/**
	 * Constructor which makes new automaton as the copy of the given automaton
	 * 
	 * @param automaton
	 *            the automaton
	 * @throws NullPointerException
	 *             the given automaton is null ClassCastException the given parameter is not instance of the class
	 *             Automaton
	 */
	public Automaton(Automaton automaton)
	{
		if (automaton == null)
		{
			throw new NullPointerException("Automaton: a given automaton is " + "null.");
		}
		if (!(automaton instanceof Automaton))
		{
			throw new ClassCastException("Automaton: a given parameter " + "is not an instance of the class Automaton.");
		}
		this.startState = automaton.getStartState();
		this.transitions = new HashMap<String, Map<String, Set<String>>>();
		for (String state : automaton.getStates())
		{
			this.addState(state);
		}
		if (automaton.containsEpsilonTransitions())
		{
			try
			{
				for (String from : automaton.getStates())
				{
					for (String to : automaton.getStates(from, "epsilon"))
					{
						this.addEpsilon(from, to);
					}
				}
			}
			catch (NoSuchStateException ex)
			{
				throw new Error("Automaton constructor : " + ex.getMessage());
			}
		}
		for (String from : automaton.getStates())
		{
			for (String under : automaton.getAlphabet())
			{
				try
				{
					for (String to : automaton.getStates(from, under))
					{
						this.addTransition(from, under, to);
					}
				}
				catch (NoSuchStateException e)
				{
					throw new Error("Automaton Constructor: " + e.getMessage());
				}
			}
		}
		this.finalStates = new HashSet<String>(automaton.getFinalStates());
		this.name = automaton.getName();
		this.statesType = automaton.getStatesType();
		this.alphabetType = automaton.getAlphabetType();
	}

	/**
	 * sets type of alphabet
	 * 
	 * @param type
	 *            0 -- lower case; 1 -- upper case; 2 -- numbers; 3 -- lower case end of alphabet; 4 -- upper case end
	 *            of alphabet; 5 -- letter 't' with index; 6 -- letter 'p' with index; 7 -- roman numbers; 8 --
	 *            lowercase roman numbers; 9 -- binary numbers;
	 */
	public void setAlphabetType(int type)
	{
		this.alphabetType = type;
	}

	public Set<String> getStatesHavingTransitionToState(String toState)
	{
		Set<String> statesHavingTransition = new HashSet<>();
		for (String fromState : transitions.keySet())
		{
			for (String alphChar : transitions.get(fromState).keySet())
			{
				if (transitions.get(fromState).get(alphChar).contains(toState))
				{
					statesHavingTransition.add(fromState);
				}
			}
		}
		return statesHavingTransition;
	}

	/**
	 * returns type of alphabet 0 -- lower case; 1 -- upper case; 2 -- numbers; 3 -- lower case end of alphabet; 4 --
	 * upper case end of alphabet; 5 -- t0,t1....; 6 -- p0, p1, p2; 7 -- roman numbers; 8 -- lowercase roman numbers; 9
	 * -- binary numbers;
	 * 
	 * @return type of alphabet
	 */
	public int getAlphabetType()
	{
		return this.alphabetType;
	}

	/**
	 * sets type of states
	 * 
	 * @param type
	 *            0 -- lower case; 1 upper case; 2 numbers; 3 lower case end of alphabet; 4 upper case end of alphabet;
	 *            5 q0,q1....; 6 s0, s1, s2; 7 roman numbers; 8 lowercase roman numbers; 9 binary numbers;
	 */
	public void setStatesType(int type)
	{
		this.statesType = type;
	}

	/**
	 * returns type of states states type 0 -- lower case; 1 upper case; 2 numbers; 3 lower case end of alphabet; 4
	 * upper case end of alphabet; 5 q0,q1....; 6 s0, s1, s2; 7 roman numbers; 8 lowercase roman numbers; 9 binary
	 * numbers;
	 * 
	 * @return type of states
	 */
	public int getStatesType()
	{
		return this.statesType;
	}

	/**
	 * returns name of the automaton as a string
	 * 
	 * @return name of the automaton as a string
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * sets the given string as a name of the automaton
	 * 
	 * @param name
	 *            a new name of the automaton
	 * @throws NullPointerException
	 *             the name is null
	 * @throws IllegalArgumentException
	 *             the given parameter is not string
	 */
	public void setName(String name)
	{
		if (name == null)
		{
			throw new NullPointerException("Automaton.setName().");
		}
		if (!(name instanceof String))
		{
			throw new IllegalArgumentException("Automaton.setName().");
		}
		this.name = name;
	}

	/**
	 * returns transitions of the automaton
	 * 
	 * @return transitions of the automaton
	 */
	public Map<String, Map<String, Set<String>>> getTransitions()
	{
		return transitions;
	}

	/**
	 * returns number of transitions of the automaton
	 * 
	 * @return number of transitions of the automaton
	 */
	public int getNumberOfTransitions()
	{
		int number = 0;
		Set<String> alphabet = new HashSet<String>(getAlphabet());
		if (this.containsEpsilonTransitions())
		{
			alphabet.add("epsilon");
		}
		for (String s_from : getStates())
		{
			for (String under : alphabet)
			{
				try
				{
					number += getStates(s_from, under).size();
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error("Automaton.getNumberOfTransitions(): " + nsse.getMessage());
				}
			}
		}
		return number;
	}

	/**
	 * A new state is added, if the state is not new, makes nothing
	 * 
	 * @param newState
	 *            name of the new state as a string
	 * @throws NullPointerException
	 *             the given new state is null
	 * @throws IllegalArgumentException
	 *             the given parameter is not string or is an empty string
	 */
	public void addState(String newState)
	{
		if (newState.equals(null))
		{
			throw new NullPointerException("Automaton.addState(): an added state " + "is null.");
		}
		if (!(newState instanceof String))
		{
			throw new IllegalArgumentException("Automaton.addState(): a name of " + "an added state is not a string.");
		}
		if (newState.isEmpty())
		{
			throw new IllegalArgumentException("Automaton.addState(): a name of " + "an added state is empty");
		}
		if (!transitions.containsKey(newState))
		{
			transitions.put(newState, new HashMap<String, Set<String>>());

		}
	}

	/**
	 * sets the state as a start, if the state is beginnig before, it is set again (it does not change)
	 * 
	 * @param state
	 *            the start state
	 * @throws NoSuchStateException
	 *             a given state is not included NullPointerException a given state is null
	 */
	public void setStartState(String state) throws NoSuchStateException
	{
		if (state == null)
		{
			throw new NullPointerException("Automaton.setStartState(): new " + "start state is null.");
		}
		if (this.containsState(state))
		{
			this.startState = state;
		}
		else
		{
			throw new NoSuchStateException("Automaton.setBeginnigState(): a new " + "start state " + state
				+ " is unknown");
		}
	}

	/**
	 * returns the start state
	 * 
	 * @return the start state
	 */
	public String getStartState()
	{
		return startState;
	}

	/**
	 * adds the state to the set of final states; if the given state was final before, makes nothing
	 * 
	 * @param newFinal
	 *            the final state
	 * @throws NoSuchStateException
	 *             state is not included NullPointerException
	 */
	public void addToFinalStates(String newFinal) throws NoSuchStateException
	{
		if (newFinal == null)
		{
			throw new NullPointerException("Automaton.addToFinalStates(): A given" + " state is null.");
		}
		if (!this.containsState(newFinal))
		{
			throw new NoSuchStateException("Automaton.addToFinalStates(): A given" + " state " + newFinal
				+ " is unknown.");
		}
		finalStates.add(newFinal);
	}

	/**
	 * returns an unmodifiable set of final states
	 * 
	 * @return an unmodifiable set of final states
	 */
	public Set<String> getFinalStates()
	{
		return Collections.unmodifiableSet(finalStates);
	}

	/**
	 * removes the final state
	 * 
	 * @param state
	 *            final state to remove
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchStateException
	 * @throws NullPointerException
	 *             the given parameter is null
	 * @throws IllegalArgumentException
	 *             the given parameter is not final state
	 */
	public void removeFinalState(String state) throws NoSuchStateException
	{
		if (state == null)
		{
			throw new NullPointerException("Automaton.removeFinalState(): " + state);
		}
		if (!this.containsState(state))
		{
			throw new NoSuchStateException("Automaton.removeFinalState(): " + state);
		}
		if (!this.getFinalStates().contains(state))
		{
			throw new IllegalArgumentException("Automaton.removeFinalState(): " + state);
		}
		this.finalStates.remove(state);
	}

	/**
	 * finds out, if the automaton contains given state
	 * 
	 * @param state
	 *            the name of the state
	 * @return true, if the automaton contains given state, false otherwise
	 */
	public boolean containsState(String state)
	{
		return this.transitions.containsKey(state);
	}

	/**
	 * the transition from one state to another under the given character is added
	 * 
	 * @param from
	 *            the source state
	 * @param under
	 *            the symbol (is the string) String "epsilon" is special type of string, is choosen only for epsilon
	 *            transitions; the automaton does not read any character, but the state is changed
	 * @param to
	 *            the goal state
	 * @throws NoSuchStateException
	 *             state from or state to is not included
	 * @throws NullPointerException
	 *             from, under or to is null
	 * @throws IllegalArgumentException
	 *             under is an empty string
	 */
	public boolean addTransition(String from, String under, String to) throws NoSuchStateException
	{
		if (from == null || under == null || to == null)
		{
			throw new NullPointerException("Method addTransition(): (" + from + ", " + under + ") -> " + to + ".");
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Method addTransition(): " + from);
		}
		if (!this.containsState(to))
		{
			throw new NoSuchStateException("Method addTransition(): " + to);
		}
		if (under.isEmpty())
		{
			throw new IllegalArgumentException("Method addTransition(): under is " + "empty.");
		}
		if (!transitions.get(from).containsKey(under))
		{
			transitions.get(from).put(under, new HashSet<String>());
		}
		return transitions.get(from).get(under).add(to);
	}

	/**
	 * adds the transition under epsilon, the string "epsilon" is special character String "epsilon" is special type of
	 * string, is choosen only for epsilon transitions; the automaton does notread any character, but the state is
	 * changed
	 * 
	 * @param from
	 *            the source state
	 * @param to
	 *            the goal
	 * @throws NoSuchStateException
	 *             state from, to is not included
	 * @throws NullPoinerException
	 *             state from, to is null
	 */
	public void addEpsilon(String from, String to) throws NoSuchStateException
	{
		if (from == null || to == null)
		{
			throw new NullPointerException("Automaton.addEpsilon(): (" + from + ", epsilon) -> " + to + ".");
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Automaton.addEpsilon(): " + from);
		}
		if (!this.containsState(to))
		{
			throw new NoSuchStateException("Automaton.addEpsilon(): " + to);
		}
		this.addTransition(from, "epsilon", to);
	}

	/**
	 * returns the set of states of the automaton, the set is unmodifiable
	 * 
	 * @return set of states of the automaton
	 */
	public Set<String> getStates()
	{
		return Collections.unmodifiableSet(transitions.keySet());
	}

	/**
	 * Returns the set of states, which is possible to reach from the given state, if the character "under" is read or
	 * empty set
	 * 
	 * @param from
	 *            the source state
	 * @param under
	 *            the read character
	 * @return set of states or empty set
	 * @throws NoSuchStateException
	 *             state from is unknown
	 * @throws NullPoinerException
	 *             state from is null, or under is null
	 */
	public Set<String> getStates(String from, String under) throws NoSuchStateException
	{
		if (from == null || under == null)
		{
			throw new NullPointerException("Method getStates(): from " + from + " under " + under);
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Method getStates(): from " + from);
		}
		if (transitions.get(from).get(under) != null)
		{
			return Collections.unmodifiableSet(transitions.get(from).get(under));
		}
		else
		{
			return Collections.emptySet();
		}
	}

	/**
	 * returns all states, which can be reached from the given state
	 * 
	 * @param from
	 *            the source state
	 * @return unmodifiable set of states of the automaton, otherwise the empty set
	 * @throws NoSuchStateException
	 *             state from is unknown
	 * @throws NullPointerException
	 *             state from is null
	 */
	public Set<String> getStates(String from) throws NoSuchStateException
	{
		if (from == null)
		{
			throw new NullPointerException("Method getStates(): from " + from);
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Method getStates(): from " + from);
		}
		if (transitions.get(from).isEmpty())
		{
			return Collections.emptySet();
		}
		Set<String> states = new HashSet<String>();
		for (String s : transitions.get(from).keySet())
		{
			if (!this.getStates(from, s).isEmpty())
			{
				states.addAll(this.getStates(from, s));
			}
		}
		return Collections.unmodifiableSet(states);
	}

	/**
	 * finds out, if the automaton contains epsilon transitions
	 * 
	 * @return true epsilon transition is present, false otherwise
	 */
	public boolean containsEpsilonTransitions()
	{
		Collection<String> states = this.getStates();
		if (states.isEmpty())
		{
			throw new Error("the empty automaton");
		}
		for (String s : states)
		{
			if (transitions.get(s).keySet().contains("epsilon"))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * finds out, if the automaton is deterministic
	 * 
	 * @return true is deterministic, false otherwise
	 */
	public boolean isDeterministic()
	{
		// test epsilon
		if (this.containsEpsilonTransitions())
		{
			return false;
		}
		for (String s : this.getStates())
		{
			for (String a : this.getAlphabet())
			{
				try
				{
					if (this.getStates(s, a).size() > 1)
					{
						return false;
					}
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error(nsse);
				}
			}
		}

		return true;
	}

	/**
	 * returns the alphabet of the automaton as the set
	 * 
	 * @return returns alphabet of the automaton as an unmodifiable set of strings or an empty set
	 */
	public Set<String> getAlphabet()
	{
		Collection<String> states = this.getStates();
		if (this.transitions.values().isEmpty())
		{
			return Collections.emptySet();
		}
		Set<String> alphabet = new HashSet<String>();
		for (String s : states)
		{
			alphabet.addAll(transitions.get(s).keySet());
		}
		alphabet.remove("epsilon"); // epsilon is removed
		return Collections.unmodifiableSet(alphabet);
	}

	/**
	 * removes all states, which are reached from the given state under the given character
	 * 
	 * @param from
	 *            the source state
	 * @param under
	 *            the character
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchStateException
	 *             state from is unknown
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchTransitionException
	 *             transitin is unknown
	 */
	public void removeTransition(String from, String under) throws NoSuchStateException, NoSuchTransitionException
	{
		if (from == null || under == null)
		{
			throw new NullPointerException("Method removeTransition(): from " + from + " under " + under);
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Method removeTransition(): from " + from);
		}
		if (this.getStates(from, under).isEmpty())
		{
			throw new NoSuchTransitionException("Method removeTransition(): from " + from + " under " + under);
		}
		transitions.get(from).remove(under);
	}

	/**
	 * removes transition (from,under) -> to, if is present, otherwise the exception is thrown
	 * 
	 * @param from
	 *            the given state
	 * @param under
	 *            the character
	 * @param to
	 *            the goal state
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchStateException
	 *             state from, to is unknown
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchTransitionException
	 *             transition is unknown
	 */
	public void removeTransition(String from, String under, String to) throws NoSuchStateException,
		NoSuchTransitionException
	{
		if (from == null || under == null || to == null)
		{
			throw new NullPointerException("Method removeTransition(): (" + from + ", " + under + ") -> " + to);
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Method removeTransition(): from " + from);
		}
		if (!this.containsState(to))
		{
			throw new NoSuchStateException("Method removeTransition(): to " + to);
		}
		if (this.getStates(from, under).isEmpty())
		{
			throw new NoSuchTransitionException("Method removeTransition(): (" + from + ", " + under + ") -> " + to);
		}
		if (!this.getStates(from, under).contains(to))
		{
			throw new NoSuchTransitionException("Method removeTransition(): (" + from + ", " + under + ") -> " + to);
		}
		transitions.get(from).get(under).remove(to);
		if (this.getStates(from, under).isEmpty())
		{
			transitions.get(from).remove(under);
		}
	}

	/**
	 * removes the state, if is present, otherwise throws exception
	 * 
	 * @param state
	 *            the state to removing
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchStateException
	 *             the unknown state
	 */
	public void removeState(String state) throws NoSuchStateException
	{
		if (state == null)
		{
			throw new NullPointerException("Method removeState(): " + state);
		}
		if (!this.containsState(state))
		{
			throw new NoSuchStateException("Method removeState(): " + state);
		}
		if (this.getStartState().equals(state))
		{
			throw new IllegalArgumentException("Method removeState(): start" + " state: " + state
				+ " cannot be removed. Please, set" + " another state as the beginnig state before removing " + state
				+ ".");
		}
		if (this.getFinalStates().contains(state))
		{
			this.removeFinalState(state);
		}
		Set<String> alphabet = new HashSet<String>(this.getAlphabet());
		if (this.containsEpsilonTransitions())
		{
			alphabet.add("epsilon");
		}
		for (String from : this.getStates())
		{ // vymaze prechody do tohoto stavu
			for (String under : alphabet)
			{
				try
				{
					if (this.getStates(from, under).contains(state))
					{
						this.removeTransition(from, under, state);
					}
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error("Method removeState(): " + nsse.getMessage());
				}
				catch (NoSuchTransitionException nste)
				{
					throw new Error("Method removeState(): " + nste.getMessage());
				}
			}
		}
		transitions.remove(state);
	}

	/**
	 * removes all states, which are unreachable
	 */
	public void removeUnreachableStates()
	{
		Set<String> S0 = new HashSet<String>(); // reachable states
		S0.add(this.getStartState()); // the start state is reachable
		Set<String> S1 = new HashSet<String>(); // new set of reachable states

		while (!S0.equals(S1))
		{
			S1.addAll(S0);
			for (String s : new HashSet<String>(S0))
			{
				try
				{
					S0.addAll(this.getStates(s));
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error("Method removeUnrecheableStates(): " + nsse.getMessage());
				}
			}
		}
		transitions.keySet().retainAll(S0);
		finalStates.retainAll(S0);

	}

	/**
	 * makes transition function total
	 * 
	 * @param blackHole
	 *            new state
	 */
	public void makeTransitionFunctionTotal(String blackHole)
	{
		if (!this.isTransitionFunctionTotal())
		{
			String newState = generateUniqueState(blackHole); // the black hole
			for (String state : new HashSet<String>(this.getStates()))
			{
				for (String under : this.getAlphabet())
				{
					try
					{
						if (this.getStates(state, under).isEmpty())
						{
							if (!this.containsState(newState))
							{
								this.addState(newState);
							}
							this.addTransition(state, under, newState);
						}
					}
					catch (NoSuchStateException nsse)
					{
						throw new Error("Method maketransitionFunctionTotal(): " + nsse.getMessage());
					}
				}
			}
			if (this.containsState(newState))
			{
				for (String under : this.getAlphabet())
				{
					try
					{
						this.addTransition(newState, under, newState);
					}
					catch (NoSuchStateException nsse)
					{
						throw new Error("Method maketransitionFunctionTotal(): " + nsse.getMessage());
					}
				}
			}
		}
	}

	/**
	 * makes total transition function
	 */
	public void makeTransitionFunctionTotal()
	{
		if (!this.isTransitionFunctionTotal())
		{
			SortedSet<String> states1 = AlphabetGenerator.sortSet(this.getStates(), this.getStatesType());
			SortedSet<String> states2 = null;
			switch (this.getStatesType())
			{
				case 0:
					states2 = AlphabetGenerator.getLowerCase(states1.size() + 1);
					break;
				case 1:
					states2 = AlphabetGenerator.getUpperCase(states1.size() + 1);
					break;
				case 2:
					states2 = AlphabetGenerator.getNumbers(states1.size() + 1);
					break;
				case 3:
					states2 = AlphabetGenerator.getLowerCaseEnd(states1.size() + 1);
					break;
				case 4:
					states2 = AlphabetGenerator.getUpperCaseEnd(states1.size() + 1);
					break;
				case 5:
					states2 = AlphabetGenerator.getLetterWithIndex(states1.size() + 1, "q");
					break;
				case 6:
					states2 = AlphabetGenerator.getLetterWithIndex(states1.size() + 1, "s");
					break;
			}
			for (String s : states1)
			{
				states2.remove(s);
			}
			String newState;
			try
			{
				newState = states2.first();
			}
			catch (NoSuchElementException e)
			{
				newState = ("N");
			}
			this.makeTransitionFunctionTotal(newState);
		}
	}

	/**
	 * support function generates a unique state
	 * 
	 * @param state
	 * @return unique state
	 */
	public String generateUniqueState(String state)
	{
		StringBuilder uniqueState = new StringBuilder(state);
		while (this.containsState(uniqueState.toString()))
		{
			uniqueState.append("'");
		}
		return uniqueState.toString();
	}

	/**
	 * renames the state
	 * 
	 * @param oldState
	 *            the state to rename
	 * @param newState
	 *            the new name
	 * @throws cz.muni.fi.xkadlec6.bp.NoSuchStateException
	 *             oldState is unknown
	 * @throws NullPointerException
	 *             some parameter is null
	 * @throws illegalArgumentException
	 *             the given new state is included (is not new)
	 */
	public void renameState(String oldState, String newState) throws NoSuchStateException
	{
		if (oldState == null || newState == null)
		{
			throw new NullPointerException("Method renameState(): oldstate " + oldState + ", newState " + newState);
		}
		if (!this.getStates().contains(oldState))
		{
			throw new NoSuchStateException("Method renameState(): oldState " + oldState);
		}
		if (this.getStates().contains(newState))
		{
			throw new IllegalArgumentException("Method renameState(): a given " + "state " + newState + " is not new.");
		}
		// tests, if an old state was start
		if (this.getStartState().equals(oldState))
		{
			this.addState(newState);
			this.setStartState(newState);
		}
		else
		{
			this.addState(newState);
		}
		// tests, if old state was final
		if (this.getFinalStates().contains(oldState))
		{
			this.addToFinalStates(newState);
		}
		for (String under : transitions.get(oldState).keySet())
		{
			for (String to : this.getStates(oldState, under))
			{
				this.addTransition(newState, under, to);
			}
		}
		Set<String> alphabet = new HashSet<String>(this.getAlphabet());
		if (this.containsEpsilonTransitions())
		{
			alphabet.add("epsilon");
		}
		for (String from : this.getStates())
		{
			for (String under : alphabet)
			{
				if (this.getStates(from, under).contains(oldState))
				{
					transitions.get(from).get(under).remove(oldState);
					this.addTransition(from, under, newState);
				}
			}
		}
		this.removeState(oldState);
	}

	/**
	 * Finds out, if two automaton are equal
	 **/
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Automaton))
		{
			return false;
		}
		Automaton a = (Automaton) o;
		if (a.getStartState().equals(this.getStartState()) && (a.getFinalStates().equals(this.getFinalStates()))
			&& a.getTransitions().equals(this.getTransitions()))
		{
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 83 * hash + (this.transitions != null ? this.transitions.hashCode() : 0);
		hash = 83 * hash + (this.finalStates != null ? this.finalStates.hashCode() : 0);
		hash = 83 * hash + (this.startState != null ? this.startState.hashCode() : 0);
		hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 83 * hash + this.alphabetType;
		hash = 83 * hash + this.statesType;
		return hash;
	}

	/**
	 * finds out, if transition function is total
	 * 
	 * @return true transition function is total, false otherwise
	 */
	public boolean isTransitionFunctionTotal()
	{
		Set<String> alphabet = new HashSet<String>(this.getAlphabet());
		int numberOfTransitions;
		int numberOfStates = this.getStates().size();
		if (isDeterministic())
		{
			numberOfTransitions = this.getNumberOfTransitions();
			if (numberOfTransitions != alphabet.size() * numberOfStates)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * returns transitions from the given state as a map, where keys are read characters and values are states
	 * 
	 * @param from
	 *            the given state
	 * @return transitions from the given state as a map
	 */
	public Map<String, Set<String>> getTransitions(String from) throws NoSuchStateException
	{
		if (from == null)
		{
			throw new NullPointerException("Automaton.getTransitions(): " + from);
		}
		if (!this.containsState(from))
		{
			throw new NoSuchStateException("Automaton.getTransitions(): " + from);
		}
		return Collections.unmodifiableMap(transitions.get(from));
	}

	/**
	 * The character in alphabet is renamed
	 * 
	 * @param oldCharacter
	 *            character, which should be renamed
	 * @param newCharacter
	 *            new Character
	 * @throws IllegaArgumentException
	 *             oldCharacter is not included or newCharacter is included
	 * @throws NullPointerException
	 *             oldCharacter or newCharacter is null
	 */
	public void renameCharacterInAlphabet(String oldCharacter, String newCharacter)
	{
		if (oldCharacter == null || newCharacter == null)
		{
			throw new NullPointerException("Automaton." + "renameCharacterInAlphabet(): " + "old character: "
				+ oldCharacter + ", new character: " + newCharacter);
		}
		if (!this.getAlphabet().contains(oldCharacter) || this.getAlphabet().contains(newCharacter))
		{
			throw new IllegalArgumentException("Automaton." + "renameCharacterInAlphabet(): " + oldCharacter + " "
				+ newCharacter);
		}
		for (String from : new HashSet<String>(this.getStates()))
		{
			try
			{
				Set<String> to = new HashSet<String>(getStates(from, oldCharacter));
				if (!to.isEmpty())
				{
					for (String s : to)
					{
						this.addTransition(from, newCharacter, s);
					}
					this.removeTransition(from, oldCharacter);
				}
			}
			catch (NoSuchStateException ex)
			{
				throw new Error("Automaton.renameCharacterInAlphabet(): " + ex);
			}
			catch (NoSuchTransitionException ex)
			{
				throw new Error("Automaton.renameCharacterInAlphabet(): " + ex);
			}
		}
	}

	/**
	 * finds out, if automaton contains unreachable states and return them as number (0 -- does not contain)
	 * 
	 * @return number of unrecheable states
	 */
	public int getNumberOfUnreachableStates()
	{
		int numberOfUnrecheableStates = 0;
		Set<String> S0 = new HashSet<String>(); // reachable states
		S0.add(this.getStartState()); // the start state is reachable
		Set<String> S1 = new HashSet<String>(); // new set of reachable states

		while (!S0.equals(S1))
		{
			S1.addAll(S0);
			for (String s : new HashSet<String>(S0))
			{
				try
				{
					S0.addAll(this.getStates(s));
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error("Method removeUnrecheableStates(): " + nsse.getMessage());
				}
			}
		}
		numberOfUnrecheableStates = this.getStates().size() - (S0).size();
		return numberOfUnrecheableStates;
	}

	/**
	 * returns the size of longest transition
	 * 
	 * @return the size of longest transition
	 */
	private int getSizeOfLongestTransition()
	{
		int i = 0;
		i = this.getSizeOfLongestState();
		i = (!this.isDeterministic() ? i + 2 : i);
		for (String c : this.getAlphabet())
		{
			String character = this.stringToPlainText(c);
			if (character.length() > i)
			{
				i = character.length();
			}
		}
		Set<String> alphabet = new HashSet<String>(this.getAlphabet());
		if (this.containsEpsilonTransitions())
		{ // special character "epsilon"
			i = (i < 2 ? 2 : i); // "\\e" in the tabular
			alphabet.add("epsilon");
		}
		for (String from : this.getStates())
		{
			for (String under : alphabet)
			{
				int j = this.getSizeOfTransition(from, under);
				i = (j > i ? j : i);
			}
		}
		return i;
	}

	/**
	 * returns the size of transition from given state under the given character
	 * 
	 * @param from
	 *            the state
	 * @param under
	 *            the character
	 * @return the size of transition from given state under the given character
	 */
	private int getSizeOfTransition(String from, String under)
	{
		int j = 0;
		try
		{
			if (!this.getStates(from, under).isEmpty())
			{
				for (String to : this.getStates(from, under))
				{
					String t = this.stringToPlainText(to);
					j = (this.isNFA ? j + t.length() + 2 : j + t.length()); // rezie +2
				}
			}
		}
		catch (NoSuchStateException nsse)
		{
			throw new Error("Automaton.getSizeOfTransition()" + nsse.getMessage());
		}
		return j;
	}

	/**
	 * returns the size of the longest state of the automaton
	 * 
	 * @return the size of the longest state of the automaton
	 */
	private int getSizeOfLongestState()
	{
		int i = 0;
		for (String state : this.getStates())
		{
			String s = this.stringToPlainText(state);
			if (s.length() > i)
			{
				i = s.length();
			}
		}
		return i;
	}

	/**
	 * returns string of spaces, with length i
	 * 
	 * @param i
	 *            number of spaces
	 * @return returns string of spaces, with length i
	 */
	private String generateStringOfSpaces(int i)
	{
		StringBuilder sb = new StringBuilder();
		while (i > 0)
		{
			sb.append(" ");
			i--;
		}
		return sb.toString();
	}

	/**
	 * string without some characters: '_' and '{' and '}'
	 * 
	 * @param s
	 *            string
	 * @return string without some characters: '_' and '{' and '}'
	 */
	private String stringToPlainText(String s)
	{
		return s.replace("_{", "").replace("}", "");
	}

	public String epsSurroundString()
	{
		StringBuilder sb = new StringBuilder();
		for (String state : epsSurrounding.keySet())
		{
			String stringToAppend = "D\u03b5(" + state + ")=" + epsSurrounding.get(state)
				+ System.getProperty("line.separator");
			stringToAppend = stringToAppend.replace("_{", "").replace("}", "").replace("[", "{").replace("]", "}");
			sb.append(stringToAppend);
		}
		return sb.toString();
	}
	
	public String epsSurroundStringLatex()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\\noindent ");
		for (String state : epsSurrounding.keySet())
		{
			String stringToAppend = "$D_\\varepsilon(" + state + ")=" + epsSurrounding.get(state) + "$\\\\"
				+ System.getProperty("line.separator");
			stringToAppend = stringToAppend.replace("_{", "").replace("}", "").replace("[", "\\{").replace("]", "\\}");
			sb.append(stringToAppend);
		}
		return sb.toString();
	}

	@Override
	public String toString()
	{

		StringBuilder sb = new StringBuilder();
		int transitionLength = this.getSizeOfLongestTransition();
		int longestStateLength = this.getSizeOfLongestState();
		String emptyCharacter;
		if (!this.isNFA)
		{
			emptyCharacter = "-";
		}
		else
		{
			emptyCharacter = "{}";
		}
		SortedSet<String> alphabet = AlphabetGenerator.sortSet(this.getAlphabet(), this.getAlphabetType());
		SortedSet<String> states = AlphabetGenerator.sortSet(this.getStates(), this.getStatesType());
		SortedSet<String> finStates = AlphabetGenerator.sortSet(this.getFinalStates(), this.getStatesType());

		if (this.containsEpsilonTransitions())
		{
			alphabet.add("epsilon");
		}
		// first row
		sb.append(this.generateStringOfSpaces(longestStateLength + 4));
		sb.append(" | ");
		for (String a : alphabet)
		{
			if (a.equals("epsilon"))
			{
				a = "\u03b5";
			}
			int z1 = (transitionLength - this.stringToPlainText(a).length()) / 2;
			int z2 = (transitionLength - this.stringToPlainText(a).length()) - z1;
			sb.append(this.generateStringOfSpaces(z1));
			sb.append(this.stringToPlainText(a));
			sb.append(this.generateStringOfSpaces(z2));
			sb.append(" | ");
		}
		sb.append("\n");
		StringBuilder sbRow = new StringBuilder();
		// divide row
		for (int i = 0; i < longestStateLength + 3 + ((transitionLength + 2) * (alphabet.size())) + alphabet.size() + 3; i++)
		{
			sbRow.append("-");
		}
		sb.append(sbRow.toString());
		sb.append("\n");
		// end divide rows
		// rows
		for (String s : states)
		{
			sb.append(this.generateStringOfSpaces(this.getSizeOfLongestState() - this.stringToPlainText(s).length() + 1));
			if (this.getStartState().equals(s))
			{
				if (finStates.contains(s))
				{
					sb.append("<->");
				}
				else
				{
					sb.append(" ->");
				}
			}
			else
			{
				if (finStates.contains(s))
				{
					sb.append(" <-");
				}
				else
				{
					sb.append("   ");
				}
			}
			sb.append(this.stringToPlainText(s) + " | ");
			for (String a : alphabet)
			{
				try
				{
					SortedSet<String> to = AlphabetGenerator.sortSet(this.getStates(s, a), this.getStatesType());
					int toLength = this.getSizeOfTransition(s, a);
					if (toLength == 0)
					{
						toLength = emptyCharacter.length();

					}
					int z1 = (transitionLength - toLength) / 2;
					int z2 = (transitionLength - toLength) - z1;
					sb.append(this.generateStringOfSpaces(z1));
					if (to.isEmpty())
					{
						sb.append(emptyCharacter);
					}
					else
					{
						sb.append(!this.isNFA ? to.toString().replace("_{", "").replace("}", "").replace("[", "")
							.replace("]", "") : to.toString().replace("_{", "").replace("}", "").replace("[", "{")
							.replace("]", "}"));
					}
					sb.append(this.generateStringOfSpaces(z2));
					sb.append(" | ");
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error("Automaton.toString()" + nsse.getMessage());
				}
			}
			sb.append("\n");
		}
		// divide row
		sb.append(sbRow.toString());
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * returns the automaton as the result to IS: :e
	 * :e="b:teacher-student:init=startState transition function F={final states}" ok viz
	 * http://arran.fi.muni.cz:8180/fja/help.html
	 * 
	 * @param teacher
	 *            string, which stands for the type of the automaton EFA -- nondeterministic finite automaton with
	 *            epsilon-transitions NFA -- nondeterministic finite automaton; DFA -- deterministic finite automaton;
	 *            MIN -- minimal deterministic finite automaton; MIC -- minimal deterministic finite automaton in the
	 *            canonical form; TOT -- DFA with total transition function
	 * @param student
	 *            the request for student answer EFA -- nondeterministic finite automaton with epsilon-transitions; NFA
	 *            -- nondeterministic finite automaton; DFA -- deterministic finite automaton; MIN -- minimal
	 *            deterministic finite automaton; MIC -- minimal deterministic finite automaton in the canonical form;
	 *            TOT -- deterministic finite automaton with the total transition function; GRA -- regular grammar; REG
	 *            -- regular expression
	 * @param isomorphism
	 *            whether to insert condition for isomorphism (transforms to 'Y'/'N')
	 * @return returns the Automaton in the form to IS: :e
	 *         :e="f:teacher-student-isomorphism:init=startState transition function F={final states}" ok
	 */
	public String toIS(String teacher, String student, boolean isomorphism)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" :e" + "\n" + ":e=\"f:");
		sb.append(teacher + "-" + student + "-");
		if (isomorphism)
			sb.append("Y");
		else
			sb.append("N");
		sb.append(":init=" + this.getStartState().replace("_{", "").replace("}", "") + " ");
		for (String state : AlphabetGenerator.sortSet(this.getStates(), this.getStatesType()))
		{
			SortedSet<String> alphabet = AlphabetGenerator.sortSet(this.getAlphabet(), this.getAlphabetType());
			if (this.containsEpsilonTransitions())
			{
				alphabet.add("epsilon");
			}
			for (String under : alphabet)
			{
				try
				{
					SortedSet<String> to = AlphabetGenerator
						.sortSet(this.getStates(state, under), this.getStatesType());
					if (to.size() != 0)
					{
						if (under.equals("epsilon"))
						{
							sb.append("(" + state.replace("_{", "").replace("}", "") + "," + "\u03b5" + ")="
								+ to.toString().replace("_{", "").replace("}", "").replace("[", "{").replace("]", "}")
								+ " ");
						}
						else
						{
							sb.append("("
								+ state.replace("_{", "").replace("}", "")
								+ ","
								+ under.replace("_{", "").replace("}", "")
								+ ")="
								+ ((!this.isNFA) ? (to.toString().replace("_{", "").replace("}", "").replace("[", "")
									.replace("]", "")) : (to.toString().replace("_{", "").replace("}", "")
									.replace("[", "{").replace("]", "}"))) + " ");
						}
					}
				}
				catch (NoSuchStateException ex)
				{
					throw new Error("Automaton.toIS(): " + ex.getMessage());
				}
			}
		}
		SortedSet<String> finStates = AlphabetGenerator.sortSet(this.getFinalStates(), this.getStatesType());
		sb.append("F=" + finStates.toString().replace("_{", "").replace("}", "").replace("[", "{").replace("]", "}")
			+ "\" ok\n");
		return sb.toString();
	}

	/**
	 * Makes automaton as a string in latex notation
	 * 
	 * @return String automaton in latex notation
	 */
	public String toLaTeX()
	{
		StringBuilder latex = new StringBuilder();
		SortedSet<String> alphabet = AlphabetGenerator.sortSet(this.getAlphabet(), this.alphabetType);
		if (this.containsEpsilonTransitions())
		{
			alphabet.add("epsilon");
		}
		SortedSet<String> states = AlphabetGenerator.sortSet(this.getStates(), this.statesType);
		int columnsCount = alphabet.size(); // count of the columns
		latex.append("$$\\begin{array}{r|");
		for (int i = 0; i < columnsCount; i++)
		{
			latex.append("c|");
		}
		latex.append("}\n");
		for (String a : alphabet)
		{ // firs row with alphabet
			if (a.equals("epsilon"))
			{
				latex.append("& \\varepsilon ");
			}
			else
			{
				latex.append("& " + a + " ");
			}
		}
		latex.append("\\\\ \\hline\n");
		for (String s : states)
		{

			// arrows start state ->, final state <-, start and final <->
			if (this.getStartState().equals(s))
			{
				if (this.getFinalStates().contains(s))
				{
					latex.append("\\leftrightarrow ");
				}
				else
				{
					latex.append("\\rightarrow ");
				}
			}
			else
			{
				if (this.getFinalStates().contains(s))
				{
					latex.append("\\leftarrow ");
				}
			}
			// latex.append(s);
			latex.append(s);
			for (String a : alphabet)
			{
				try
				{
					if (this.getStates(s, a).size() == 0)
					{
						if (!this.isNFA)
						{
							latex.append(" & -");
						}
						else
						{
							latex.append(" & \\emptyset");
						}
					}
					else
					{
						SortedSet<String> actual = new TreeSet<String>(AlphabetGenerator.getComparator(statesType));
						for (String state : this.getStates(s, a))
						{
							actual.add(state);
						}
						if (!this.isNFA)
						{
							latex.append(" & " + actual.toString().replace("[", "").replace("]", ""));
						}
						else
						{
							latex.append(" & " + actual.toString().replace("[", "\\{").replace("]", "\\}"));
						}
					}
				}
				catch (NoSuchStateException nsse)
				{
					throw new Error(nsse);
				}
			}
			latex.append(" \\" + "\\ \\hline\n");
		}
		latex.append("\\end{array}$$\n");
		return latex.toString();
	}

	public Set<String> getUnreachableStates()
	{
		return unreachableStates;
	}

}
