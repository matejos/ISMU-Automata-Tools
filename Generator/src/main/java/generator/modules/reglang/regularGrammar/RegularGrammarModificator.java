/*
 * Changes variables or terminals of the given regular grammar
 */

package generator.modules.reglang.regularGrammar;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.SortedSet;

/**
 * Class RegularGrammarModificator modifies some characteristics of the given regular grammar (variables...)
 * 
 * @author Jana Kadlecova
 */
@SuppressWarnings({ "rawtypes" })
public class RegularGrammarModificator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private RegularGrammarModificator()
	{
	}

	/**
	 * changes variables to the varibales of given type
	 * 
	 * @param g
	 *            the regular grammar
	 * @param type
	 *            0 a, b, c... 1 A, B, C... 2 1, 2, 3... 3 x, y, z... 4 X, Y, Z... 5 t0,t1, t2.... 6 p0, p1, p2... 7 I,
	 *            II, III, IV... 8 i, ii, iii, iv... 9 1, 10, 11, 100... changes variables to the varibales of given
	 *            type
	 */
	public static void modifyVariables(RegularGrammar g, int type)
	{
		if (type < 0 || type > 9)
		{
			throw new IllegalArgumentException("RegularGrammarModificator." + "modifyVariables(): " + type);
		}
		if (!(g instanceof RegularGrammar))
		{
			throw new IllegalArgumentException("RegularGrammarModificator."
				+ "modifyVariables(): given parameter is not regular grammar.");
		}
		SortedSet<String> variables = AlphabetGenerator.sortSet(g.getVariables(), g.getVariablesType());
		int count = variables.size();
		SortedSet newVariables;

		switch (type)
		{
			case 5:
				newVariables = AlphabetGenerator.getLetterWithIndex(count, "q");
				break;
			case 6:
				newVariables = AlphabetGenerator.getLetterWithIndex(count, "s");
				break;
			default:
				newVariables = AlphabetGenerator.getNewSortedAlphabet(type, count);
				break;
		}
		boolean renamed = false;
		for (String s : variables)
		{ // find collisions
			if (newVariables.contains(s))
			{
				renamed = true;
				// nessessary to rename old variable
				StringBuilder uniqueState = new StringBuilder(s);
				while (newVariables.contains(uniqueState.toString()))
				{
					uniqueState.append("'");
				}
				try
				{
					g.renameVariable(s, uniqueState.toString());
				}
				catch (NoSuchVariableException e)
				{
					throw new Error("RegularGrammarModificator." + "modifyVariables(): " + e.getMessage());
				}
			}
		}
		if (renamed)
		{
			variables = AlphabetGenerator.sortSet(g.getVariables(), g.getVariablesType());
		}
		for (int i = 0; i < count; i++)
		{
			String oldVariable = variables.first();
			String newVariable = (String) newVariables.first();
			try
			{
				g.renameVariable(oldVariable, newVariable);
				variables.remove(oldVariable);
				newVariables.remove(newVariable);
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammarModificator." + "modifyVariables(): " + e.getMessage());
			}
		}
		g.setVariablesType(type);
	}

}
