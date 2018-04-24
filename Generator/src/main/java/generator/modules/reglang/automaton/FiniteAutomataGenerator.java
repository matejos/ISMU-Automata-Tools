
package generator.modules.reglang.automaton;

import generator.common.tools.CommonUtils;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.reglang.regularGrammar.NoSuchVariableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author JUH Generator for generating regular grammars
 */
public class FiniteAutomataGenerator
{

	public static Automaton generateFA(int minAlphabet, int maxAlphabet, int minStates, int maxStates,
		int minFinalStates, int maxFinalStates, boolean totalTransitionFunction, int minLoops, int maxLoops,
		int minTransitions, int maxTransitions, int minUnreachable, int maxUnreachable, boolean deterministic)
		throws NoSuchVariableException, NoSuchStateException

	{

		Automaton a = new Automaton("q0");
		a.setAlphabetType(0);
		Random rand = new Random();
		Set<String> usedStates = new HashSet<String>();
		Set<String> unusedTerminals = new HashSet<>();
		int unreachableStates = 0;
		Set<String> statesNotReachingFinalState = null;
		int unreachableTransitions = 0;
		int statesWithoutUnreach;
		List<String> automatonAlphabetList = null;

		do
		{
			unreachableTransitions = 0;
			unreachableStates = 0;
			unusedTerminals = new HashSet<>();
			int alphChars = CommonUtils.randInt(minAlphabet, maxAlphabet, rand);
			statesWithoutUnreach = CommonUtils.randInt(minStates, maxStates, rand);

			int finalStates = CommonUtils.randInt(minFinalStates, maxFinalStates, rand);
			int transitions = CommonUtils.randInt(minTransitions, maxTransitions, rand);
			if (maxUnreachable > 0)
			{
				unreachableStates = CommonUtils.randInt(minUnreachable, maxUnreachable, rand);
				if (unreachableStates != 0)
				{
					unreachableTransitions = (alphChars * unreachableStates) - unreachableStates;
				}
			}
			if (totalTransitionFunction)
			{
				if (maxUnreachable > 0)
				{
					transitions = (statesWithoutUnreach - 1) * alphChars;
					unreachableTransitions = unreachableStates * alphChars;
				}
				else
				{
					transitions = (statesWithoutUnreach - 1) * alphChars;
				}
			}

			if (totalTransitionFunction)
			{
				statesWithoutUnreach = statesWithoutUnreach - 1; // peklo
			}

			a = new Automaton("q0");

			for (int i = 0; i < statesWithoutUnreach - 1; i++)
			{
				a.addState("q" + (i + 1));
			}

			List<String> automatonStatesAll = new ArrayList<String>(a.getStates());
			for (int k = 0; k < finalStates; k++)
			{
				boolean stateAdded = false;
				do
				{
					int selectedIndex = CommonUtils.randInt(0, automatonStatesAll.size() - 1, rand);
					if (!a.getFinalStates().contains(automatonStatesAll.get(selectedIndex)))
					{
						a.addToFinalStates(automatonStatesAll.get(selectedIndex));
						stateAdded = true;
					}
				}
				while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && !stateAdded);
			}

			Set<String> finalStatesReachability = new HashSet<>(a.getFinalStates());

			Set<String> automatonAlphChars = new HashSet<String>();
			for (int i = 0; i < alphChars; i++)
			{
				automatonAlphChars.add(String.valueOf((char) ('a' + i)));
			}

			unusedTerminals.addAll(automatonAlphChars);

			int usedTransitions = 0;

			List<String> unusedStates = new ArrayList<>(a.getStates());
			List<String> newlyReachedStates = new ArrayList<String>();
			newlyReachedStates.add("q0");
			CommonUtils.removeElementFromList(unusedStates, "q0");

			usedStates.add("q0");
			// dopredne prechody
			while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && !unusedStates.isEmpty())
			{
				List<String> newStates = new ArrayList<String>();
				boolean newStateFound = false;
				for (String alreadyReachedStateFrom : newlyReachedStates)
				{
					int transitionsAddedPerState = 0;
					for (String terminal : automatonAlphChars)
					{
						if (rand.nextBoolean())
						{
							continue;
						}
						if (unusedStates.size() == 0 || transitionsAddedPerState > 0)
						{
							break;
						}
						try
						{
							String randomUnusedStateTo = unusedStates.get(CommonUtils.randInt(0,
								unusedStates.size() - 1, rand));
							if (a.addTransition(alreadyReachedStateFrom, terminal, randomUnusedStateTo))
							{
								usedStates.add(alreadyReachedStateFrom);
								if (finalStatesReachability.contains(randomUnusedStateTo))
								{
									finalStatesReachability.add(alreadyReachedStateFrom);
								}
								usedTransitions++;
								transitionsAddedPerState++;
								unusedTerminals.remove(terminal);

								newStates.add(randomUnusedStateTo);
								newStateFound = true;
								CommonUtils.removeElementFromList(unusedStates, randomUnusedStateTo);
							}

						}

						catch (NoSuchStateException e)
						{
							e.printStackTrace();
						}
					}
				}
				if (newStateFound)
					newlyReachedStates = newStates;

			}

			automatonAlphabetList = new ArrayList<String>(automatonAlphChars);

			statesNotReachingFinalState = new HashSet<>(a.getStates());
			statesNotReachingFinalState.removeAll(finalStatesReachability);

			// zpetne + loopy
			int safeguard = 0;
			try
			{
				while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && usedTransitions < transitions)
				{
					if (safeguard > 50)
					{
						break;
					}
					String stateFrom = null;

					stateFrom = automatonStatesAll.get(CommonUtils.randInt(0, automatonStatesAll.size() - 1, rand));

					String stateTo = null;

					stateTo = automatonStatesAll.get(CommonUtils.randInt(0, automatonStatesAll.size() - 1, rand));
					if (finalStatesReachability.contains(stateTo) && statesNotReachingFinalState.size() > 0)
					{
						Iterator<String> statesNotReachingFinalStateIt = statesNotReachingFinalState.iterator();
						stateFrom = statesNotReachingFinalStateIt.next();
						statesNotReachingFinalStateIt.remove();
					}
					else if (statesNotReachingFinalState.size() > 0)
					{
						continue;
					}

					String terminal = null;
					if (unusedTerminals.size() > 0)
					{
						Iterator<String> unusedTermIt = unusedTerminals.iterator();
						terminal = unusedTermIt.next();
						unusedTermIt.remove();
					}
					else
					{
						terminal = automatonAlphabetList
							.get(CommonUtils.randInt(0, automatonAlphChars.size() - 1, rand));
					}

					if (a.getTransitions(stateFrom).containsKey(terminal) && deterministic)
					{
						safeguard++;
						continue;
					}

					if (a.addTransition(stateFrom, terminal, stateTo))
					{
						usedTransitions++;
						usedStates.add(stateFrom);
					}

					safeguard++;
				}
				if (safeguard > 50)
					continue;

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && (usedStates.size() != statesWithoutUnreach || statesNotReachingFinalState.size() > 1));
		// keep some tolerance for blackholes

		List<String> unreachableStatesList = new ArrayList<>();
		for (int i = statesWithoutUnreach; i < statesWithoutUnreach + unreachableStates; i++)
		{
			a.addState("q" + i);
			a.addUnreachableState("q" + i);
			unreachableStatesList.add("q" + i);
		}

		List<String> allStatesList = new ArrayList<>();
		for (int i = 0; i < statesWithoutUnreach + unreachableStates; i++)
		{
			allStatesList.add("q" + i);
		}
		Set<String> unreachStatesUnused = new HashSet<>(a.getUnreachableStates());

		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && unreachableTransitions != 0)
		{
			int stateToIndex = CommonUtils.randInt(1, statesWithoutUnreach + unreachableStates, rand) - 1;
			int stateFromIndex = CommonUtils.randInt(1, unreachableStates, rand) - 1;
			int alphCharIndex = CommonUtils.randInt(1, automatonAlphabetList.size(), rand) - 1;

			String stateTo = allStatesList.get(stateToIndex);
			String stateFrom = null;
			if (unreachStatesUnused.size() > 0)
			{
				Iterator<String> unreachUnusedIt = unreachStatesUnused.iterator();
				stateFrom = unreachUnusedIt.next();
				unreachUnusedIt.remove();
			}
			else
			{
				stateFrom = unreachableStatesList.get(stateFromIndex);
			}
			String alphChar = automatonAlphabetList.get(alphCharIndex);

			if (deterministic && a.getTransitions(stateFrom).containsKey(alphChar))
			{
				continue;
			}
			if (a.addTransition(stateFrom, alphChar, stateTo))
			{
				unreachableTransitions--;
				a.increaseUnreachTransCount();
			}
		}

		if (deterministic && totalTransitionFunction)
		{
			a.makeTransitionFunctionTotal("N");
		}

		if (!deterministic)
		{
			a.setIsNFA(true);
		}

		return a;

	}
	public static Automaton generateEFA(int minAlphabet, int maxAlphabet, int minStates, int maxStates,
		int minFinalStates, int maxFinalStates, int minLoops, int maxLoops, int minTransitions, int maxTransitions,
		int minUnreachable, int maxUnreachable, int minEpsTransitions, int maxEpsTransitions)
		throws NoSuchVariableException, NoSuchStateException

	{
		if (minEpsTransitions < 1)
		{
			throw new IllegalArgumentException("Minimal number of eps transitions has to be bigger than 0.");
		}
		Automaton a = generateFA(minAlphabet, maxAlphabet, minStates, maxStates, minFinalStates, maxFinalStates, false,
			minLoops, maxLoops, minTransitions, maxTransitions, minUnreachable, maxUnreachable, false);

		Random rand = new Random();

		int usedEpsTransitions = 0;

		int epsTrans = CommonUtils.randInt(minEpsTransitions, maxEpsTransitions, rand);
		int safeguard = 0;

		Set<String> statesWithoutUnreach = new HashSet<>(a.getStates());
		statesWithoutUnreach.removeAll(a.getUnreachableStates());

		List<String> automatonStatesWithoutUnreach = new ArrayList<String>(statesWithoutUnreach);

		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && usedEpsTransitions < epsTrans)
		{
			if (safeguard > 50)
			{
				break;
			}
			String stateFrom = automatonStatesWithoutUnreach.get(CommonUtils.randInt(0,
				automatonStatesWithoutUnreach.size() - 1, rand));
			String stateTo = null;

			stateTo = automatonStatesWithoutUnreach.get(CommonUtils.randInt(0,
				automatonStatesWithoutUnreach.size() - 1, rand));

			String terminal = "epsilon";

			if (stateFrom.equals(stateTo))
			{
				safeguard++;
				continue;
			}

			if (a.addTransition(stateFrom, terminal, stateTo))
			{
				usedEpsTransitions++;
			}
			safeguard++;

		}
		return a;

	}

}
