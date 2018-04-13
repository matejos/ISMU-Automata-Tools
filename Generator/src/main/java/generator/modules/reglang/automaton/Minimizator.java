/*
 * Class Minimizator minimizes the given DFA.
 */

package generator.modules.reglang.automaton;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import generator.modules.reglang.alphabet.RomanNumbersConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The class minimize the given DFA
 * 
 * @author Jana Kadlecova
 */
public class Minimizator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private Minimizator()
	{
	}

	/**
	 * minimalize given automat
	 * 
	 * @param a1
	 *            automaton to minimalization
	 * @return minimal automaton
	 * @throws IllegalArgumenException
	 *             given automaton is not deterministic, contains unreachable states, transition function is not total
	 */
	public static Automaton minimize(Automaton a1)
	{
		if (!a1.isDeterministic())
		{
			throw new IllegalArgumentException("Minimalizator.minimize(): "
				+ "The given automaton is nondeterministic.");
		}
		if (a1.getNumberOfUnreachableStates() != 0)
		{
			throw new IllegalArgumentException("Minimalizator.minimize(): "
				+ "the given automaton contains unreachable states.");
		}
		if (!a1.isTransitionFunctionTotal())
		{
			throw new IllegalArgumentException("Minimalizator.minimize(): " + "the transition function is not total.");
		}
		List<Reduct> reducts = getSteps(a1);
		Reduct r = reducts.get(reducts.size() - 1);

		Automaton a2 = new Automaton(r.getClass(a1.getStartState()));
		a2.setStatesType(7);
		for (String state : a1.getStates())
		{
			a2.addState(r.getClass(state));
			if (a1.getFinalStates().contains(state))
			{
				try
				{
					a2.addToFinalStates(r.getClass(state));
				}
				catch (NoSuchStateException ex)
				{
					throw new Error("Minimalizator.minimize(): " + ex.getMessage());
				}
			}
		}
		for (String state : a1.getStates())
		{
			for (String under : a1.getAlphabet())
			{
				try
				{
					a2.addTransition(r.getClass(state), under, r.getTransitionToClass(state, under));
				}
				catch (NoSuchStateException ex)
				{
					throw new Error("Minimalizator.minimize(): " + ex.getMessage());
				}
			}

		}
		return a2;
	}

	/**
	 * finds the list of reducts, which build steps of minimalization
	 * 
	 * @param automaton
	 *            automaton to minimalize
	 * @return all steps of minimalization as a list of reducts
	 * @throws IllegalArgumenException
	 *             given automaton is not deterministic, contains unreachable states, transition function is not total
	 */
	private static List<Reduct> getSteps(Automaton automaton)
	{
		if (!automaton.isDeterministic())
		{
			throw new IllegalArgumentException("Minimalizator.getSteps(): "
				+ "The given automaton is nondeterministic.");
		}
		if (automaton.getNumberOfUnreachableStates() != 0)
		{
			throw new IllegalArgumentException("Minimalizator.getSteps(): "
				+ "The given automaton contains unreachable states.");
		}
		if (!automaton.isTransitionFunctionTotal())
		{
			throw new IllegalArgumentException("Minimalizator.getSteps(): " + "the transition function is not total.");
		}
		List<Reduct> reducts = new ArrayList<Reduct>();
		Reduct r1 = new Reduct(automaton);
		if (automaton.getStates().size() - automaton.getFinalStates().size() == 0
			|| automaton.getFinalStates().isEmpty())
		{
			for (String s : automaton.getStates())
			{
				r1.setClassToState(s, "I");
			}
		}
		else
		{
			for (String s : automaton.getStates())
			{
				if (automaton.getFinalStates().contains(s))
				{
					r1.setClassToState(s, "II");
				}
				else
				{
					r1.setClassToState(s, "I");
				}
			}
		}
		int i = 0;
		r1.setIndex(i);
		i++;
		reducts.add(r1);
		Reduct r2 = getNextStep(r1, automaton);
		while (!r1.equals(r2))
		{
			reducts.add(r2);
			r2.setIndex(i);
			i++;
			r1 = r2;
			r2 = getNextStep(r1, automaton);
		}

		return reducts;
	}

	/**
	 * returns number of steps of minimalization
	 * 
	 * @param a
	 *            automaton
	 * @return number of steps of minimalization
	 */
	public static int getNumberOfSteps(Automaton a)
	{
		return getSteps(a).size();
	}

	/**
	 * returns next step of minimalization as a reduct
	 * 
	 * @param r1
	 *            reduct
	 * @param a
	 *            automaton
	 * @return reduct, which is next in the minimalization
	 */
	private static Reduct getNextStep(Reduct r1, Automaton a)
	{
		Reduct r2 = new Reduct(a);
		SortedSet<String> used = new TreeSet<String>(AlphabetGenerator.getComparator(a.getStatesType()));
		int numberOfClasses = 0;
		for (String classes : AlphabetGenerator.sortSet(r1.getClasses(), r1.getClassesType()))
		{
			for (String state : AlphabetGenerator.sortSet(r1.getStates(classes), a.getStatesType()))
			{
				if (!used.contains(state))
				{
					SortedSet<String> equalStates = r1.getEqualStates(state);
					numberOfClasses++;
					for (String added : equalStates)
					{
						r2.setClassToState(added, RomanNumbersConverter.arabicToRoman(numberOfClasses));
					}
					used.addAll(equalStates);
				}
			}
		}
		return r2;
	}

	/**
	 * returns all steps of minimalization as a string
	 * 
	 * @param a
	 *            automaton
	 * @return returns all steps of minimalization as a string
	 */
	public static String stepsToString(Automaton a)
	{
		StringBuilder s = new StringBuilder();
		List<Reduct> steps = getSteps(a);
		int i = 0;
		for (Reduct r : steps)
		{
			s.append("Step " + i + "\n" + r.toString() + "\n");
			i++;
		}
		return s.toString();
	}

	/**
	 * returns steps of minimalization in LaTeX
	 * 
	 * @param a
	 *            automaton
	 * @return steps of minimalization in LaTeX
	 */
	public static String stepsToLaTeX(Automaton a)
	{
		StringBuilder s = new StringBuilder();
		List<Reduct> steps = getSteps(a);
		int i = 1;
		for (Reduct r : steps)
		{
			s.append(r.toLaTeX());
			if (i != steps.size())
			{
				s.append("\\bigskip\n\n");
				i++;
			}
		}
		return s.toString();
	}

}
