/**
 * The class for comparating languages of two automatons
 */

package generator.modules.reglang.automaton;

/**
 * Finds out, if two automatons are language equal
 * 
 * @author Jana Kadlecova
 */
public class LanguageEqualityChecker
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private LanguageEqualityChecker()
	{
	}

	/**
	 * Finds out, if two automatons are language equal
	 * 
	 * @param a
	 *            automaton
	 * @param b
	 *            automaton
	 * @return true if are language equal, false otherwise
	 */
	public static boolean checkLanguageEquality(Automaton a, Automaton b)
	{
		if (a == null || b == null)
		{
			throw new NullPointerException("Method " + "LanquageEqualizator.languageEquals()");
		}
		Automaton aa = new Automaton(a);
		Automaton bb = new Automaton(b);
		aa.removeUnreachableStates();
		bb.removeUnreachableStates();
		aa = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(aa);
		bb = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(bb);
		aa = Determinator.getDFA(aa);
		bb = Determinator.getDFA(bb);
		aa = Minimizator.minimize(aa);
		bb = Minimizator.minimize(bb);
		if (!aa.getAlphabet().equals(bb.getAlphabet()))
		{
			return false;
		}
		aa = Canonizator.canonize(aa);
		bb = Canonizator.canonize(bb);
		if (aa.equals(bb))
		{
			return true;
		}
		return false;
	}
}
