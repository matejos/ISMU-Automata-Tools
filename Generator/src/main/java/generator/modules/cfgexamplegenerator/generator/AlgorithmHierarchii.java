/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author drasto, bafco
 */
public class AlgorithmHierarchii
{

	// public static final Tree<AlgorithmType> HIERARCHII =
	// new Tree<AlgorithmType>(AlgorithmType.NULL_ALG,
	// new Tree<AlgorithmType>(AlgorithmType.NONGENERATING_ELIM_ALG,
	// new Tree(AlgorithmType.UNREACHABLE_ELIM_ALG,
	// new Tree(AlgorithmType.EPSILON_ELIM_ALG,
	// new Tree(AlgorithmType.SIMPLE_RULES_ELIM_ALG,
	// new Tree(AlgorithmType.CNF_CREATION_ALG),
	// new Tree(AlgorithmType.LRECURSION_ELIM_ALG,
	// new Tree(AlgorithmType.GNF_CREATION_ALG))
	// )))));
	private static final Map<InputGrammarForm, Set<OutputGrammarForm>> AFTER = new HashMap<InputGrammarForm, Set<OutputGrammarForm>>();
	private static final Map<OutputGrammarForm, Set<InputGrammarForm>> BEFORE = new HashMap<OutputGrammarForm, Set<InputGrammarForm>>();
	private static final Map<IO, AlgorithmType[]> BETWEEN = new HashMap<IO, AlgorithmType[]>();

	/*
	 * static { add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_NONGENERATING, AlgorithmType.NONGENERATING_ELIM_ALG);
	 * add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_UNREACHABLE, AlgorithmType.UNREACHABLE_ELIM_ALG);
	 * add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_USELESS, AlgorithmType.NONGENERATING_ELIM_ALG,
	 * AlgorithmType.UNREACHABLE_ELIM_ALG); add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_EPSILON,
	 * AlgorithmType.EPSILON_ELIM_ALG); add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_EPSILON_NO_USELESS,
	 * OutputGrammarForm.NO_USELESS, AlgorithmType.EPSILON_ELIM_ALG); add(InputGrammarForm.GENERAL,
	 * OutputGrammarForm.NO_SIMPLE, AlgorithmType.EPSILON_ELIM_ALG, AlgorithmType.SIMPLE_RULES_ELIM_ALG);
	 * add(InputGrammarForm.GENERAL, OutputGrammarForm.PROPER_NO_SIMPLE, OutputGrammarForm.NO_EPSILON_NO_USELESS,
	 * AlgorithmType.SIMPLE_RULES_ELIM_ALG); add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_LEFT_RECURSION,
	 * OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.LRECURSION_ELIM_ALG); add(InputGrammarForm.GENERAL,
	 * OutputGrammarForm.CNF, OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.CNF_CREATION_ALG);
	 * add(InputGrammarForm.GENERAL, OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION,
	 * AlgorithmType.GNF_CREATION_ALG); add(InputGrammarForm.NO_NONGENERATING, OutputGrammarForm.NO_USELESS,
	 * AlgorithmType.UNREACHABLE_ELIM_ALG); add(InputGrammarForm.NO_NONGENERATING,
	 * OutputGrammarForm.NO_EPSILON_NO_USELESS, OutputGrammarForm.NO_USELESS, AlgorithmType.EPSILON_ELIM_ALG);
	 * add(InputGrammarForm.NO_NONGENERATING, OutputGrammarForm.PROPER_NO_SIMPLE,
	 * OutputGrammarForm.NO_EPSILON_NO_USELESS, AlgorithmType.SIMPLE_RULES_ELIM_ALG);
	 * add(InputGrammarForm.NO_NONGENERATING, OutputGrammarForm.NO_LEFT_RECURSION, OutputGrammarForm.PROPER_NO_SIMPLE,
	 * AlgorithmType.LRECURSION_ELIM_ALG); add(InputGrammarForm.NO_NONGENERATING, OutputGrammarForm.CNF,
	 * OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.CNF_CREATION_ALG); add(InputGrammarForm.NO_NONGENERATING,
	 * OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION, AlgorithmType.GNF_CREATION_ALG);
	 * add(InputGrammarForm.NO_USELESS, OutputGrammarForm.NO_EPSILON_NO_USELESS, AlgorithmType.EPSILON_ELIM_ALG);
	 * add(InputGrammarForm.NO_USELESS, OutputGrammarForm.PROPER_NO_SIMPLE, OutputGrammarForm.NO_EPSILON_NO_USELESS,
	 * AlgorithmType.SIMPLE_RULES_ELIM_ALG); add(InputGrammarForm.NO_USELESS, OutputGrammarForm.NO_LEFT_RECURSION,
	 * OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.LRECURSION_ELIM_ALG); add(InputGrammarForm.NO_USELESS,
	 * OutputGrammarForm.CNF, OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.CNF_CREATION_ALG);
	 * add(InputGrammarForm.NO_USELESS, OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION,
	 * AlgorithmType.GNF_CREATION_ALG); add(InputGrammarForm.WITHOUT_EPSILON, OutputGrammarForm.NO_SIMPLE,
	 * AlgorithmType.SIMPLE_RULES_ELIM_ALG); add(InputGrammarForm.NO_EPSILON_NO_USELESS,
	 * OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.SIMPLE_RULES_ELIM_ALG);
	 * add(InputGrammarForm.NO_EPSILON_NO_USELESS, OutputGrammarForm.NO_LEFT_RECURSION,
	 * OutputGrammarForm.PROPER_NO_SIMPLE, AlgorithmType.LRECURSION_ELIM_ALG);
	 * add(InputGrammarForm.NO_EPSILON_NO_USELESS, OutputGrammarForm.CNF, OutputGrammarForm.PROPER_NO_SIMPLE,
	 * AlgorithmType.CNF_CREATION_ALG); add(InputGrammarForm.NO_EPSILON_NO_USELESS, OutputGrammarForm.GNF,
	 * OutputGrammarForm.NO_LEFT_RECURSION, AlgorithmType.GNF_CREATION_ALG); add(InputGrammarForm.PROPER_NO_SIMPLE,
	 * OutputGrammarForm.NO_LEFT_RECURSION, AlgorithmType.LRECURSION_ELIM_ALG); add(InputGrammarForm.PROPER_NO_SIMPLE,
	 * OutputGrammarForm.CNF, AlgorithmType.CNF_CREATION_ALG); add(InputGrammarForm.PROPER_NO_SIMPLE,
	 * OutputGrammarForm.GNF, AlgorithmType.LRECURSION_ELIM_ALG, AlgorithmType.GNF_CREATION_ALG);
	 * add(InputGrammarForm.READY_FOR_GNF_ALG, OutputGrammarForm.GNF, AlgorithmType.GNF_CREATION_ALG); }
	 */

	static
	{
		add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_EPSILON, AlgorithmType.EPSILON_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_SIMPLE, AlgorithmType.EPSILON_ELIM_ALG,
			AlgorithmType.SIMPLE_RULES_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_NONGENERATING, AlgorithmType.NONGENERATING_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_UNREACHABLE, AlgorithmType.UNREACHABLE_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_USELESS, AlgorithmType.NONGENERATING_ELIM_ALG,
			AlgorithmType.UNREACHABLE_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.PROPER, OutputGrammarForm.NO_SIMPLE,
			AlgorithmType.NONGENERATING_ELIM_ALG, AlgorithmType.UNREACHABLE_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.NO_LEFT_RECURSION, OutputGrammarForm.NO_USELESS,
			AlgorithmType.EPSILON_ELIM_ALG, AlgorithmType.SIMPLE_RULES_ELIM_ALG, AlgorithmType.LRECURSION_ELIM_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.CNF, OutputGrammarForm.NO_USELESS,
			AlgorithmType.EPSILON_ELIM_ALG, AlgorithmType.SIMPLE_RULES_ELIM_ALG, AlgorithmType.CNF_CREATION_ALG);
		add(InputGrammarForm.GENERAL, OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION,
			AlgorithmType.GNF_CREATION_ALG);

		add(InputGrammarForm.WITHOUT_EPSILON, OutputGrammarForm.NO_SIMPLE, AlgorithmType.SIMPLE_RULES_ELIM_ALG);
		add(InputGrammarForm.WITHOUT_EPSILON, OutputGrammarForm.PROPER, OutputGrammarForm.NO_SIMPLE,
			AlgorithmType.NONGENERATING_ELIM_ALG, AlgorithmType.UNREACHABLE_ELIM_ALG);
		add(InputGrammarForm.WITHOUT_EPSILON, OutputGrammarForm.NO_LEFT_RECURSION,
			AlgorithmType.NONGENERATING_ELIM_ALG, AlgorithmType.UNREACHABLE_ELIM_ALG,
			AlgorithmType.SIMPLE_RULES_ELIM_ALG, AlgorithmType.LRECURSION_ELIM_ALG);
		add(InputGrammarForm.WITHOUT_EPSILON, OutputGrammarForm.CNF, AlgorithmType.NONGENERATING_ELIM_ALG,
			AlgorithmType.UNREACHABLE_ELIM_ALG, AlgorithmType.SIMPLE_RULES_ELIM_ALG, AlgorithmType.CNF_CREATION_ALG);
		add(InputGrammarForm.WITHOUT_EPSILON, OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION,
			AlgorithmType.GNF_CREATION_ALG);

		add(InputGrammarForm.NO_USELESS, OutputGrammarForm.NO_EPSILON, AlgorithmType.EPSILON_ELIM_ALG);
		add(InputGrammarForm.NO_USELESS, OutputGrammarForm.NO_SIMPLE, AlgorithmType.EPSILON_ELIM_ALG,
			AlgorithmType.SIMPLE_RULES_ELIM_ALG);
		add(InputGrammarForm.NO_USELESS, OutputGrammarForm.CNF, OutputGrammarForm.NO_SIMPLE,
			AlgorithmType.CNF_CREATION_ALG);
		add(InputGrammarForm.NO_USELESS, OutputGrammarForm.NO_LEFT_RECURSION, AlgorithmType.EPSILON_ELIM_ALG,
			AlgorithmType.SIMPLE_RULES_ELIM_ALG, AlgorithmType.LRECURSION_ELIM_ALG);
		add(InputGrammarForm.NO_USELESS, OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION,
			AlgorithmType.GNF_CREATION_ALG);

		add(InputGrammarForm.PROPER, OutputGrammarForm.NO_LEFT_RECURSION, AlgorithmType.LRECURSION_ELIM_ALG);
		add(InputGrammarForm.PROPER, OutputGrammarForm.GNF, AlgorithmType.LRECURSION_ELIM_ALG,
			AlgorithmType.GNF_CREATION_ALG);
		add(InputGrammarForm.PROPER_NO_SIMPLE, OutputGrammarForm.CNF, AlgorithmType.CNF_CREATION_ALG);
		add(InputGrammarForm.PROPER_NO_LEFT_REC, OutputGrammarForm.GNF, AlgorithmType.GNF_CREATION_ALG);

		// add(InputGrammarForm.NO_EPSILON_NO_SIMPLE, OutputGrammarForm.CNF, AlgorithmType.CNF_CREATION_ALG);
		// add(InputGrammarForm.NOT_CYCLIC_NO_EPSILON, OutputGrammarForm.NO_LEFT_RECURSION,
		// AlgorithmType.LRECURSION_ELIM_ALG);
		// add(InputGrammarForm.NOT_CYCLIC_NO_EPSILON, OutputGrammarForm.GNF, AlgorithmType.LRECURSION_ELIM_ALG,
		// AlgorithmType.GNF_CREATION_ALG);
		// add(InputGrammarForm.READY_FOR_GNF_ALG, OutputGrammarForm.GNF, AlgorithmType.GNF_CREATION_ALG);

		add(InputGrammarForm.NO_EPSILON_NO_SIMPLE, OutputGrammarForm.CNF, AlgorithmType.NONGENERATING_ELIM_ALG,
			AlgorithmType.UNREACHABLE_ELIM_ALG, AlgorithmType.CNF_CREATION_ALG);
		add(InputGrammarForm.NOT_CYCLIC_NO_EPSILON, OutputGrammarForm.NO_LEFT_RECURSION,
			AlgorithmType.NONGENERATING_ELIM_ALG, AlgorithmType.UNREACHABLE_ELIM_ALG, AlgorithmType.LRECURSION_ELIM_ALG);
		add(InputGrammarForm.NOT_CYCLIC_NO_EPSILON, OutputGrammarForm.GNF, OutputGrammarForm.NO_LEFT_RECURSION,
			AlgorithmType.GNF_CREATION_ALG);
		add(InputGrammarForm.READY_FOR_GNF_ALG, OutputGrammarForm.GNF, AlgorithmType.NONGENERATING_ELIM_ALG,
			AlgorithmType.UNREACHABLE_ELIM_ALG, AlgorithmType.GNF_CREATION_ALG);
	}

	private static void add(InputGrammarForm in, OutputGrammarForm out, AlgorithmType... algorithms)
	{
		if (!AFTER.containsKey(in))
		{
			AFTER.put(in, new HashSet<OutputGrammarForm>());
		}
		if (!BEFORE.containsKey(out))
		{
			BEFORE.put(out, new HashSet<InputGrammarForm>());
		}

		AFTER.get(in).add(out);
		BEFORE.get(out).add(in);

		IO io = new IO(in, out);
		BETWEEN.put(io, algorithms);
	}

	private static void add(InputGrammarForm in, OutputGrammarForm out, OutputGrammarForm same,
		AlgorithmType... algorithms)
	{
		AlgorithmType[] sameAlg = BETWEEN.get(new IO(in, same));
		AlgorithmType[] result = new AlgorithmType[sameAlg.length + algorithms.length];
		int i = 0;
		for (AlgorithmType al : sameAlg)
		{
			result[i] = al;
			i++;
		}
		for (AlgorithmType al : algorithms)
		{
			result[i] = al;
			i++;
		}
		add(in, out, result);
	}

	private static class IO
	{

		private InputGrammarForm in;
		private OutputGrammarForm out;

		public IO(InputGrammarForm in, OutputGrammarForm out)
		{
			this.in = in;
			this.out = out;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final IO other = (IO) obj;
			if (this.in != other.in)
			{
				return false;
			}
			if (this.out != other.out)
			{
				return false;
			}
			return true;
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 79 * hash + (this.in != null ? this.in.hashCode() : 0);
			hash = 79 * hash + (this.out != null ? this.out.hashCode() : 0);
			return hash;
		}
	}

	public static OutputGrammarForm[] after(InputGrammarForm start)
	{
		if (start == null)
		{
			throw new NullPointerException("start");
		}

		Set<OutputGrammarForm> toAdd = AFTER.get(start);
		OutputGrammarForm[] result = new OutputGrammarForm[toAdd.size()];
		int i = 0;
		for (OutputGrammarForm o : OutputGrammarForm.values())
		{
			if (toAdd.contains(o))
			{
				result[i] = o;
				i++;
			}
		}

		return result;

	}

	public static AlgorithmType[] between(InputGrammarForm start, OutputGrammarForm end)
	{
		if (end == null)
		{
			throw new NullPointerException("end");
		}
		if (start == null)
		{
			throw new NullPointerException("start");
		}

		return Arrays.copyOf(BETWEEN.get(new IO(start, end)), BETWEEN.get(new IO(start, end)).length);

	}

}
