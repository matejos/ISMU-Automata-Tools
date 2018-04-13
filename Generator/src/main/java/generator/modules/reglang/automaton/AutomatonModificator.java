/*
 * The class is the modificator, which changes attributes of the FA.
 */

package generator.modules.reglang.automaton;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

/**
 * The class Automaton Modificator stands for the modificator, which change some characteristics of the given automaton
 * (states, alphabet...)
 * 
 * @author Jana Kadlecova
 */
@SuppressWarnings({ "rawtypes" })
public class AutomatonModificator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private AutomatonModificator()
	{
	}

	/**
	 * Changes the alphabet of the given automaton
	 * 
	 * @param automaton
	 * @param type
	 *            0 a,b,c 1 A,B,C 2 1,2,3 3 x,y,z 4 X,Y,Z 5 t0,t1, t2 6 p0, p1, p2 7 I,II,III, IV 8 i, ii, iii, iv 9 1,
	 *            10, 11, 100
	 * @throws IllegalArgumentException
	 *             the unknown type or parameter automaton is not instance of the class Automaton
	 * @throws NullPointerException
	 *             the parameter automaton is null
	 */
	public static void modifyAlphabet(Automaton automaton, int type)
	{
		if (type < 0 || type > 9)
		{
			throw new IllegalArgumentException("AutomatonModificator." + "modifyAlphabet(): the unknown type " + type);
		}
		if (automaton == null)
		{
			throw new NullPointerException("AutomatonModificator."
				+ "modifyAlphabet(): the parameter automaton is null");
		}
		if (!(automaton instanceof Automaton))
		{
			throw new IllegalArgumentException("AutomatonModificator."
				+ "modifyAlphabet(): the parameter is not instance of the " + "class Automaton " + automaton);
		}
		SortedSet<String> alphabet = AlphabetGenerator.sortSet(automaton.getAlphabet(), automaton.getAlphabetType());
		int count = alphabet.size();
		SortedSet<String> newAlphabet = AlphabetGenerator.getNewSortedAlphabet(type, count);
		boolean renamed = false;
		for (String s : alphabet)
		{ // find collisions
			if (newAlphabet.contains(s))
			{
				renamed = true;
				// nessessary to rename old state
				StringBuilder uniqueChar = new StringBuilder(s);
				while (newAlphabet.contains(uniqueChar.toString()))
				{
					uniqueChar.append("'");
				}
				automaton.renameCharacterInAlphabet(s, uniqueChar.toString());
			}
		}
		if (renamed)
		{
			alphabet = AlphabetGenerator.sortSet(automaton.getAlphabet(), automaton.getAlphabetType());
		}
		for (int i = 0; i < count; i++)
		{
			String oldCharacter = alphabet.first();
			String newCharacter = (String) newAlphabet.first();
			automaton.renameCharacterInAlphabet(oldCharacter, newCharacter);
			alphabet.remove(oldCharacter);
			newAlphabet.remove(newCharacter);
		}
		automaton.setAlphabetType(type);
	}

	/**
	 * changes names of states of the given automaton
	 * 
	 * @param automaton
	 *            the automaton
	 * @param type
	 *            0 a,b,c 1 A,B,C 2 1,2,3 3 x,y,z 4 X,Y,Z 5 q0,q1, q2 6 s0, s1, s2 7 I,II,III, IV 8 i, ii, iii, iv 9 1,
	 *            10, 11, 100
	 * @throws IllegalArgumentException
	 *             the unknown type or parameter automaton is not instance of the class Automaton
	 * @throws NullPointerException
	 *             the parameter automaton is null
	 */
	public static void modifyStates(Automaton automaton, int type)
	{
		if (type < 0 || type > 9)
		{
			throw new IllegalArgumentException("AutomatonModificator." + "modifyStates(): the unknown type " + type);
		}
		if (automaton == null)
		{
			throw new NullPointerException("AutomatonModificator." + "modifyStates(): the parameter automaton is null");
		}
		if (!(automaton instanceof Automaton))
		{
			throw new IllegalArgumentException("AutomatonModificator."
				+ "modifyStates(): the parameter is not instance of the " + "class Automaton " + automaton);
		}
		SortedSet<String> states = AlphabetGenerator.sortSet(automaton.getStates(), automaton.getStatesType());
		int count = states.size();
		SortedSet newStates;
		switch (type)
		{
			case 5:
				newStates = AlphabetGenerator.getLetterWithIndex(count, "q");
				break;
			case 6:
				newStates = AlphabetGenerator.getLetterWithIndex(count, "s");
				break;
			default:
				newStates = AlphabetGenerator.getNewSortedAlphabet(type, count);
				break;
		}
		boolean renamed = false;
		for (String s : states)
		{ // find collisions
			if (newStates.contains(s))
			{
				renamed = true;
				// nessessary to rename old state
				StringBuilder uniqueState = new StringBuilder(s);
				while (newStates.contains(uniqueState.toString()))
				{
					uniqueState.append("'");
				}
				try
				{
					automaton.renameState(s, uniqueState.toString());
				}
				catch (NoSuchStateException e)
				{
					throw new Error("AutomatonModifikator.modifyStates(): " + e.getMessage());
				}
			}
		}
		if (renamed)
		{
			states = AlphabetGenerator.sortSet(automaton.getStates(), automaton.getStatesType());
		}
		for (int i = 0; i < count; i++)
		{
			String oldState = states.first();
			String newState = (String) newStates.first();
			try
			{
				automaton.renameState(oldState, newState);
				states.remove(oldState);
				newStates.remove(newState);
			}
			catch (NoSuchStateException e)
			{
				throw new Error("AutomatonModificator.modifyStates(): " + e.getMessage());
			}
		}
		automaton.setStatesType(type);
	}

	/**
	 * renames start state (start state is not first in the table)
	 * 
	 * @param a
	 *            automaton
	 * @throws IllegalArgumentException
	 *             the parameter automaton is not instance of the class Automaton
	 * @throws NullPointerException
	 *             the parameter automaton is null
	 */
	public static void renameStartState(Automaton a)
	{
		if (a == null)
		{
			throw new NullPointerException("AutomatonModificator."
				+ "renameStartState(): the parameter automaton is null.");
		}
		if (!(a instanceof Automaton))
		{
			throw new IllegalArgumentException("AutomatonModificator."
				+ "renameStartState(): the parameter is not instance of " + "the class Automaton " + a);
		}
		String state0 = a.getStartState(); // beginnig state of automaton
		String state1; // another state of automaton
		StringBuilder state2 = new StringBuilder().append("XX"); // state not associated with automaton
		do
		{
			state1 = getRandomElement(a.getStates());
		}
		while (state0.equals(state1));
		while (a.getStates().contains(state2.toString()))
		{ // if this state is included, modify it
			state2.append("X");
		}
		try
		{
			a.renameState(state1, state2.toString());
			a.renameState(state0, state1);
			a.renameState(state2.toString(), state0);
		}
		catch (NoSuchStateException ex)
		{
			throw new Error("AutomatonModificator.renameStartState(): " + ex.getMessage());
		}
	}

	/**
	 * returns random element in collection
	 * 
	 * @param c
	 *            collection
	 * @return random element of collection
	 */
	private static String getRandomElement(Collection<String> c)
	{
		return new ArrayList<String>(c).get(AutomatonModificator.randomInt(0, c.size() - 1));
	}

	/**
	 * returns random number in the given interval
	 * 
	 * @param min
	 *            start of the interval
	 * @param max
	 *            end of the interval
	 * @return random number in the given interval
	 */
	private static int randomInt(int min, int max)
	{
		return (int) Math.floor(Math.random() * (max - min) + min + 0.5);
	}

}
