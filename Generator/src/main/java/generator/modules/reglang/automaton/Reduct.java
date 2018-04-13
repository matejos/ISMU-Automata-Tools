/*
 * Reduct represents step of minimalization
 */

package generator.modules.reglang.automaton;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The class Reduct is the supporting class for minimization
 * 
 * @author Jana Kadlecova
 */
public class Reduct
{

	private Automaton automaton;
	private TreeMap<String, String> m; // states -> classes
	private int numberOfRelations = -1; // number of relation
	private int classesType = 7;

	/**
	 * Constructor
	 * 
	 * @param automaton
	 */
	public Reduct(Automaton automaton)
	{
		this.automaton = automaton;
		m = new TreeMap<String, String>(AlphabetGenerator.getComparator(automaton.getStatesType()));
	}

	/**
	 * sets index of relations
	 * 
	 * @param numberOfRelations
	 *            number of relation
	 */
	public void setIndex(int numberOfRelations)
	{
		this.numberOfRelations = numberOfRelations;
	}

	/**
	 * Returns reduct as a string in LaTeX
	 * 
	 * @return string in LaTeX
	 */
	public String toLaTeX()
	{
		StringBuilder s = new StringBuilder();
		SortedSet<String> alphabet = AlphabetGenerator.sortSet(this.getAutomaton().getAlphabet(), this.getAutomaton()
			.getAlphabetType());
		int columnsCount = alphabet.size();
		s.append("$$\\begin{array}{lr|");
		for (int j = 0; j < columnsCount; j++)
		{
			s.append("c|");
		}
		s.append("}\n ");
		if (numberOfRelations == -1)
		{
			s.append("\\equiv & ");
		}
		else
		{
			s.append("\\equiv_{" + numberOfRelations + "} & ");
		}
		for (String under : alphabet)
		{
			s.append("& " + under + " ");
		}
		s.append("\\\\\\hline\n");
		for (String classes : AlphabetGenerator.sortSet(new HashSet<String>(this.m.values()), this.classesType))
		{
			s.append(classes + " ");
			for (String state : AlphabetGenerator.sortSet(this.m.keySet(), this.getAutomaton().getStatesType()))
			{
				if (this.getClass(state).equals(classes))
				{
					s.append("& ");
					s.append(state + " ");
					for (String under : alphabet)
					{
						s.append("& " + this.getTransitionToClass(state, under) + " ");
					}
					s.append("\\\\\n");
				}
			}
			s.replace(s.length() - 1, s.length(), " \\hline\n");
		}
		s.append("\\end{array}$$\n");
		return s.toString();
	}

	public int getClassesType()
	{
		return this.classesType;
	}

	/**
	 * sets class to the state
	 * 
	 * @param state
	 * @param classOfSplit
	 */
	public void setClassToState(String state, String classOfSplit)
	{
		this.m.put(state, classOfSplit);
	}

	/**
	 * returns class to the given state or null
	 * 
	 * @param state
	 * @return class to the given state or null
	 */
	public String getClass(String state)
	{
		return this.m.get(state);
	}

	/**
	 * returns set of states to the given class
	 * 
	 * @param classOfSplit
	 * @return empty set or set of states
	 */
	public Set<String> getStates(String classOfSplit)
	{
		Set<String> states = new HashSet<String>();
		for (String state : this.m.keySet())
		{
			if (this.m.get(state).equals(classOfSplit))
			{
				states.add(state);
			}
		}
		return states;
	}

	/**
	 * returns the transfer from the given state under character to the class
	 * 
	 * @param state
	 * @param under
	 * @return the transfer from the given state under character to the class
	 */
	public String getTransitionToClass(String state, String under)
	{
		String to = "";
		try
		{
			to = automaton.getStates(state, under).toString().replace("[", "").replace("]", "");
		}
		catch (NoSuchStateException ex)
		{
			return to;
		}
		return this.getClass(to);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof Reduct)
		{
			Reduct r = (Reduct) o;
			if (r.getNewTransitions().equals(this.getNewTransitions()) && r.getAutomaton().equals(this.getAutomaton()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 67 * hash + (this.automaton != null ? this.automaton.hashCode() : 0);
		hash = 67 * hash + (this.getNewTransitions() != null ? this.getNewTransitions().hashCode() : 0);
		return hash;
	}

	/**
	 * uses method equals()
	 * 
	 * @return
	 */
	private SortedMap<String, String> getNewTransitions()
	{
		return Collections.unmodifiableSortedMap(m);
	}

	/**
	 * returns automaton
	 * 
	 * @return automaton
	 */
	private Automaton getAutomaton()
	{
		return automaton;
	}

	/**
	 * returns all classes as a sorted set
	 * 
	 * @return all classes as a sorted set
	 */
	public SortedSet<String> getClasses()
	{
		return AlphabetGenerator.sortSet(new HashSet<String>(this.m.values()), this.classesType);
	}

	/**
	 * returns true reduct contains state, false otherwise
	 * 
	 * @param state
	 *            finds out, if the given state is present
	 * @return true reduct contains state, false otherwise
	 */
	public boolean containsState(String state)
	{
		return this.m.containsKey(state);
	}

	/**
	 * returns states, which are in the same relation of fragmentation
	 * 
	 * @param state
	 * @return states, which are in the same relation of fragmentation
	 */
	public SortedSet<String> getEqualStates(String state)
	{
		SortedSet<String> states = new TreeSet<String>(AlphabetGenerator.getComparator(this.getAutomaton()
			.getStatesType()));
		for (String s : this.getStates(this.getClass(state)))
		{
			if (this.getAllTransitionsFromState(state).equals(this.getAllTransitionsFromState(s)))
			{
				states.add(s);
			}
		}
		return states;
	}

	/**
	 * private method, which is used with getEqualStates
	 * 
	 * @param state
	 * @return
	 */
	private SortedMap<String, String> getAllTransitionsFromState(String state)
	{
		SortedMap<String, String> t = new TreeMap<String, String>(AlphabetGenerator.getComparator(this.getAutomaton()
			.getAlphabetType()));
		for (String under : this.getAutomaton().getAlphabet())
		{
			t.put(under, this.getTransitionToClass(state, under));
		}
		return t;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		SortedSet<String> alphabet = AlphabetGenerator.sortSet(this.getAutomaton().getAlphabet(), this.getAutomaton()
			.getAlphabetType());
		SortedSet<String> states = AlphabetGenerator.sortSet(this.getAutomaton().getStates(), this.getAutomaton()
			.getStatesType());
		SortedSet<String> classes = this.getClasses();
		int longest = 0;
		int j = this.getSizeOfLongestString(states);
		longest = (longest > j ? longest : j);
		j = this.getSizeOfLongestString(classes);
		longest = (longest > j ? longest : j);
		j = this.getSizeOfLongestString(alphabet);
		longest = (longest > j ? longest : j);
		boolean first = true;
		StringBuilder tab = new StringBuilder(this.generateStringOfSpaces(8));
		// first row
		sb.append(this.generateStringOfSpaces(2 * longest) + tab.toString());
		sb.append(" | ");
		for (String a : alphabet)
		{
			int z1 = (longest - stringToPlainText(a).length()) / 2;
			int z2 = (longest - stringToPlainText(a).length()) - z1;
			sb.append(this.generateStringOfSpaces(z1));
			sb.append(this.stringToPlainText(a));
			sb.append(this.generateStringOfSpaces(z2));
			sb.append(" | ");
		}
		sb.append("\n");
		// rows
		for (String c : classes)
		{
			// divide row
			for (int i = 0; i < 2 * longest + tab.toString().length() + alphabet.size() + 2
				+ ((longest + 2) * alphabet.size()); i++)
			{
				sb.append("-");
			}
			sb.append("\n");
			// end divide
			sb.append(c); // class of split
			sb.append(this.generateStringOfSpaces(longest - c.length()));
			first = true;
			// states in this class of split
			for (String s : this.getStates(c))
			{
				if (!first)
				{
					sb.append(this.generateStringOfSpaces(longest));
				}
				sb.append(tab.toString());
				sb.append(this.generateStringOfSpaces(longest - this.stringToPlainText(s).length()));
				sb.append(this.stringToPlainText(s) + " | ");
				for (String a : alphabet)
				{
					String to = this.getTransitionToClass(s, a);
					int toLength = to.length();
					int z1 = (longest - toLength) / 2;
					int z2 = (longest - toLength) - z1;
					sb.append(this.generateStringOfSpaces(z1));
					sb.append(to);
					sb.append(this.generateStringOfSpaces(z2));
					sb.append(" | ");
				}
				first = false;
				sb.append("\n");
			}
		}
		// divide row
		for (int i = 0; i < 2 * longest + tab.toString().length() + alphabet.size() + 2
			+ ((longest + 2) * alphabet.size()); i++)
		{
			sb.append("-");
		}
		sb.append("\n");
		// end divide
		return sb.toString();
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
	 * returns the longest string of the given set or zero, if the set is empty
	 * 
	 * @param c
	 *            set of strings
	 * @return the longest string of the given set or zero, if the set is empty
	 * @throws IllegalArgumentException
	 *             the parameter c is not set
	 * @throws NullPointerException
	 *             the parameter c is null
	 */
	private int getSizeOfLongestString(Set<String> c)
	{
		if (c == null)
		{
			throw new NullPointerException("AutomatonModificator."
				+ "getSizeOfLongestString(): the parameter c is null");
		}
		if (!(c instanceof Set))
		{
			throw new IllegalArgumentException("AutomatonModificator."
				+ "getSizeOfLongestString(): the parameter c is not set " + c);
		}
		int i = 0;
		for (String s : c)
		{
			String s2 = this.stringToPlainText(s);
			if (s2.length() > i)
			{
				i = s2.length();
			}
		}
		return i;
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
}
