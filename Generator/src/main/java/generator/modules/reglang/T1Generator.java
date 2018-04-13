/*
 * The class makes exercises for transformations between various kinds of finite automata
 */

package generator.modules.reglang;

import generator.common.GeneratingLogic;
import generator.common.GeneratorWorker;
import generator.communication.dto.ExampleDTO;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.reglang.automaton.Automaton;
import generator.modules.reglang.automaton.AutomatonModificator;
import generator.modules.reglang.automaton.Canonizator;
import generator.modules.reglang.automaton.Determinator;
import generator.modules.reglang.automaton.EpsilonTransitionsEliminator;
import generator.modules.reglang.automaton.FiniteAutomataGenerator;
import generator.modules.reglang.automaton.Minimizator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The class T1Generator stands for the background thread, which generates exercises for transformations between various
 * kinds of finite automata
 * 
 * @author Jana Kadlecova
 */
public class T1Generator extends GeneratorWorker implements GeneratingLogic
{

	private int operationFrom;
	private int operationTo;
	private int numberOfStatesMin;
	private int numberOfStatesMax;
	private int numberOfFinalStatesMin;
	private int numberOfFinalStatesMax;
	private int numberOfTransitionsMin;
	private int numberOfTransitionsMax;
	private int numberOfEpsTransitionsMin;
	private int numberOfEpsTransitionsMax;
	private int sizeOfAlphabetMin;
	private int sizeOfAlphabetMax;
	private int alphabet;
	private int states;
	private boolean stepsWriteOut;
	private boolean totalTransitionFunction;
	private int numberOfStepsMin; // -1, if is not set
	private int numberOfStepsMax; // -1, if is not set
	private int numberOfUnreachableStatesMin;
	private int numberOfUnreachableStatesMax;
	private boolean firstStateNotInitial;

	// result attributes are -1, if are not set
	private int resultNumberOfStatesMin;
	private int resultNumberOfStatesMax;
	private int resultNumberOfTransitionsMin;
	private int resultNumberOfTransitionsMax;

	private int exercises;

	/**
	 * Constructor, which makes new backgroud thread. The thread generates the exercises for converting between various
	 * FA
	 * 
	 * @param frame
	 *            ExerciseGeneratorGUI
	 * @param output
	 *            JTextAreas, where should be the result printed
	 * @param exercises
	 *            number of exercises
	 * @param operationFrom
	 *            kind of the start automaton: 0 -- NFA with epsilon transitions; 1 -- NFA; 2 -- DFA; 3 -- minimal DFA
	 * @param operationTo
	 *            kind of the resulting automaton: 0 -- NFA, 1 -- DFA, 3 -- minimal DFA; 4 -- minimal DFA in the
	 *            canonical form
	 * @param numberOfStatesMin
	 *            number of states min
	 * @param numberOfStatesMax
	 *            number of states max
	 * @param numberOfFinalStatesMin
	 *            number of final states min
	 * @param numberOfFinalStatesMax
	 *            number of final states max
	 * @param numberOfTransitionsMin
	 *            number of transitions min
	 * @param numberOfTransitionsMax
	 *            number of transitions max
	 * @param sizeOfAlphabetMin
	 *            size of alphabet min
	 * @param sizeOfAlphabetMax
	 *            size of alphabet max
	 * @param alphabet
	 *            type of the alphabet: 0 -- a, b, c (lower case); 1 -- A, B, C (upper case); 2 -- 1, 2, 3 (numbers); 3
	 *            -- x, y, z (lower case end of rhe alphabet); 4 -- X, Y, Z (upper case end of the alphabet); 5 -- t0,
	 *            t1, t2 (letter 't' with index); 6 -- p0, p1, p2 (letter 'p' with index); 7 -- I, II, III, IV (roman
	 *            numbers); 8 -- i, ii, iii, iv (roman numbers lower case); 9 -- 1, 10, 11, 100 (binary numbers)
	 * @param states
	 *            type of the states: 0 -- a, b, c (lower case); 1 -- A, B, C (upper case); 2 -- 1, 2, 3 (numbers); 3 --
	 *            x, y, z (lower case end of rhe alphabet); 4 -- X, Y, Z (upper case end of the alphabet); 5 -- q0, q1,
	 *            q2 (letter 'q' with index); 6 -- s0, s1, s2 (letter 's' with index); 7 -- I, II, III, IV (roman
	 *            numbers); 8 -- i, ii, iii, iv (roman numbers lower case); 9 -- 1, 10, 11, 100 (binary numbers)
	 * @param stepsWriteOut
	 *            write out the steps of minimization
	 * @param totalTransitionFunction
	 *            total transition function required
	 * @param firstStateNotInitial
	 *            firts state in the table is not initial
	 * @param numberOfStepsMin
	 *            number of steps of minimization min
	 * @param numberOfStepsMax
	 *            number of steps of minimization max
	 * @param numberOfUnreachableStatesMin
	 *            number of unreachable states min
	 * @param numberOfUnreachableStatesMax
	 *            number of unreachable states max
	 * @param resultNumberOfStatesMin
	 *            resulting automaton: number of states min
	 * @param resultNumberOfStatesMax
	 *            resulting automaton: number of states max
	 * @param resultNumberOfTransitionsMin
	 *            resulting automaton: number of transitions min
	 * @param resultNumberOfTransitionsMax
	 *            resulting automaton: number of transitions max
	 */
	public T1Generator(int exercises, int operationFrom, int operationTo, int numberOfStatesMin, int numberOfStatesMax,
		int numberOfFinalStatesMin, int numberOfFinalStatesMax, int numberOfTransitionsMin, int numberOfTransitionsMax,
		int sizeOfAlphabetMin, int sizeOfAlphabetMax, int alphabet, int states, boolean stepsWriteOut,
		boolean totalTransitionFunction, boolean firstStateNotInitial, int numberOfStepsMin, int numberOfStepsMax,
		int numberOfUnreachableStatesMin, int numberOfUnreachableStatesMax, int resultNumberOfStatesMin,
		int resultNumberOfStatesMax, int resultNumberOfTransitionsMin, int resultNumberOfTransitionsMax,
		int numberOfEpsTransitionsMin, int numberOfEpsTransitionsMax)
	{

		this.operationFrom = operationFrom;
		this.operationTo = operationTo;
		this.numberOfStatesMin = numberOfStatesMin;
		this.numberOfStatesMax = numberOfStatesMax;
		this.numberOfFinalStatesMin = numberOfFinalStatesMin;
		this.numberOfFinalStatesMax = numberOfFinalStatesMax;
		this.numberOfTransitionsMin = numberOfTransitionsMin;
		this.numberOfTransitionsMax = numberOfTransitionsMax;
		this.numberOfEpsTransitionsMin = numberOfEpsTransitionsMin;
		this.numberOfEpsTransitionsMax = numberOfEpsTransitionsMax;
		this.sizeOfAlphabetMin = sizeOfAlphabetMin;
		this.sizeOfAlphabetMax = sizeOfAlphabetMax;
		this.alphabet = alphabet;
		this.states = states;
		this.stepsWriteOut = stepsWriteOut;
		this.totalTransitionFunction = totalTransitionFunction;
		this.firstStateNotInitial = firstStateNotInitial;
		this.numberOfStepsMin = numberOfStepsMin;
		this.numberOfStepsMax = numberOfStepsMax;
		this.numberOfUnreachableStatesMin = numberOfUnreachableStatesMin;
		this.numberOfUnreachableStatesMax = numberOfUnreachableStatesMax;

		this.resultNumberOfStatesMin = resultNumberOfStatesMin;
		this.resultNumberOfStatesMax = resultNumberOfStatesMax;
		this.resultNumberOfTransitionsMin = resultNumberOfTransitionsMin;
		this.resultNumberOfTransitionsMax = resultNumberOfTransitionsMax;

		this.exercises = exercises;

	}

	public void executeGeneration()
	{
		Map<String, String> result = new HashMap<String, String>();
		// StringBulder for each kind of result form
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbISEN = new StringBuilder();
		// the beginnig of some kinds of exercises is set
		sbLatex.append("\\documentclass{article}\n\\usepackage{czech}\n"
			+ "\\usepackage[utf8]{inputenc}\n\\begin{document}\n");
		sbLatexEN.append("\\documentclass{article}\n\\begin{document}\n");
		sbIS.append("\n");
		sbISEN.append("\n");
		// the task is set
		Map<String, String> task = setTask();

		// make new exercise
		for (int i = 0; i < exercises; i++)
		{
			Automaton a = null;
			// generates automaton with the given parameters

			do
			{
				a = callFunctionGenerateAutomaton();
				if (a == null)
				{
					break;
				}
			}
			while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && !isSuitable(a));

			if (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
			{
				// divide exercises
				// _______________ Exercise (number of exercise)____________________
				sbLatex = new StringBuilder();
				sbPlainText = new StringBuilder();
				sbIS = new StringBuilder();
				sbLatexEN = new StringBuilder();
				sbPlainTextEN = new StringBuilder();
				sbISEN = new StringBuilder();

				// sets the task
				sbLatex.append(task.get("latex"));
				sbPlainText.append(task.get("plainText"));
				sbIS.append(task.get("IS"));
				sbLatexEN.append(task.get("latexEN"));
				sbPlainTextEN.append(task.get("plainTextEN"));
				sbISEN.append(task.get("ISEN"));
				// changes the alphabet and the names of the states
				AutomatonModificator.modifyAlphabet(a, alphabet);
				AutomatonModificator.modifyStates(a, states);
				// makes another state initial (not the first)
				if (firstStateNotInitial)
				{
					AutomatonModificator.renameStartState(a);
				}
				// Automaton and the row with text "Result"
				sbLatex.append(a.toLaTeX() + "\n\n\\noindent \\textbf{Řešení.} \n");
				sbLatexEN.append(a.toLaTeX() + "\n\n\\noindent \\textbf{Solution.} \n");
				sbPlainText.append(a.toString() + "\n\nŘešení:\n\n");
				sbPlainTextEN.append(a.toString() + "\n\nSolution:\n\n");

				sbIS.append("<M>" + a.toLaTeX().replace("$", "") + "</M>\n");
				sbISEN.append("<M>" + a.toLaTeX().replace("$", "") + "</M>\n");
				// result
				try
				{
					Map<String, String> generatedResult = generateResults(a);
					sbLatexEN.append(generatedResult.get("latexEN") + "\\bigskip \n\n");
					sbPlainText.append(generatedResult.get("plainText") + "\n\n");
					sbIS.append(generatedResult.get("IS"));
					sbLatex.append(generatedResult.get("latex") + "\\bigskip \n\n");
					sbPlainTextEN.append(generatedResult.get("plainTextEN") + "\n\n");
					sbISEN.append(generatedResult.get("ISEN"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				if (FormalLanguagesExampleGenerator.MULTI_THREAD_GENERATING)
				{
					// Option1
					if (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
					{
						ExampleDTO example = new ExampleDTO(1);

						example.setPlainCZOutput(sbPlainText.toString());
						example.setPlainENOutput(sbPlainTextEN.toString());
						example.setLateXCZOutput(sbLatex.toString());
						example.setLateXENOutput(sbLatexEN.toString());
						example.setISCZOutput(sbIS.toString());
						example.setISENOutput(sbISEN.toString());

						try
						{
							FormalLanguagesExampleGenerator.getCoreInstance().getExampleQueue().put(example);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					List<StringAndAreaDTO> publishResults = new ArrayList<StringAndAreaDTO>();
					publishResults.add(new StringAndAreaDTO(sbPlainText.toString(), getPlainAreaCz()));
					publishResults.add(new StringAndAreaDTO(sbPlainTextEN.toString(), getPlainAreaEn()));
					publishResults.add(new StringAndAreaDTO(sbLatex.toString(), getLatexAreaCz()));
					publishResults.add(new StringAndAreaDTO(sbLatexEN.toString(), getLatexAreaEn()));
					publishResults.add(new StringAndAreaDTO(sbIS.toString(), getIsAreaCz()));
					publishResults.add(new StringAndAreaDTO(sbISEN.toString(), getIsAreaEn()));
					publish(publishResults);
					moduleToCoreCommunication.updateProgress(i + 1, exercises);
				}
			}
		}
		// the end of some kinds of exercises is set
		sbLatex.append("\n\\end{document}\n");
		sbLatexEN.append("\n\\end{document}\n");
		// ready exercises are put into the map result
		result.put("latex", sbLatex.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put(
			"plainText",
			sbPlainText.toString().replace("transition function", "přechodová funkce").replace("Step", "Krok")
				.replace("Automaton", "Automat"));
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("IS", sbIS.toString());
		result.put("ISEN", sbISEN.toString());
		// for (String s : output.keySet())
		// {
		// output.get(s).setText(result.get(s) + "\n");
		// }

	}

	/**
	 * sets the task
	 * 
	 * @param operationFrom
	 *            beginnig of interval
	 * @param operationTo
	 *            end of interval
	 * @return task as a Map <String, String>
	 */
	private Map<String, String> setTask()
	{
		Map<String, String> result = new HashMap<String, String>();
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		StringBuilder sbISEN = new StringBuilder();
		sbIS.append("<p>Najděte jazykově ekvivalentní ");
		sbISEN.append("<p>Find a language equivalent ");
		switch (operationFrom)
		{
			case 0:
				sbLatexEN.append(" Eliminate $\\varepsilon$-transitions");
				sbLatex.append(" Odstraňte $\\varepsilon$-kroky");
				sbPlainText.append("Odstraňte epsilon kroky");
				sbPlainTextEN.append("Eliminate epsilon-transitions");
				switch (operationTo)
				{
					case 0:
						sbLatexEN.append(" of");
						sbLatex.append(" u");
						sbPlainTextEN.append(" of");
						sbPlainText.append(" u");
						sbIS.append("NFA bez <M>\\varepsilon</M>-kroků ");
						sbISEN.append("NFA without <M>\\varepsilon</M>-transitions ");
						break;
					case 1:
						sbLatexEN.append(" and determine");
						sbLatex.append(" a determinizujte");
						sbPlainText.append(" a determinizujte");
						sbPlainTextEN.append(" and determine");
						sbIS.append("DFA s totální přechodovou funkcí ");
						sbISEN.append("DFA with the total transition function ");
						break;
					case 2:
						sbLatexEN.append(", determine and minimize");
						sbLatex.append(", determinizujte a minimalizujte");
						sbPlainText.append(", determinizujte a minimalizujte");
						sbPlainTextEN.append(", determine and minimize");
						sbIS.append("minimální DFA ");
						sbISEN.append("minimal DFA ");
						break;
					case 3:
						sbLatexEN.append(", determine, minimize and canonize");
						sbLatex.append(", determinizujte, minimalizujte a kanonizujte");
						sbPlainText.append(", determinizujte, minimalizujte a kanonizujte");
						sbPlainTextEN.append(", determine, minimize and canonize");
						sbIS.append("minimální DFA v kanonickém tvaru ");
						sbISEN.append("minimal DFA in the canonical form ");
						break;
				}
				sbPlainText.append(" NFA s epsilon-kroky");
				sbPlainTextEN.append(" the NFA with epsilon-transitions");
				sbLatex.append(" NFA s $\\varepsilon$-kroky");
				sbLatexEN.append(" the NFA with $\\varepsilon$-transitions");
				sbIS.append("pro NFA s <M>\\varepsilon</M>-kroky zadaný tabulkou:");
				sbISEN.append("to the NFA with <M>\\varepsilon</M>-transitions given with the table:");
				break;
			case 1:
				sbLatexEN.append(" Determine");
				sbLatex.append(" Determinizujte");
				sbPlainTextEN.append("Determine");
				sbPlainText.append("Determinizujte");
				switch (operationTo)
				{
					case 1:
						sbIS.append("DFA s totální přechodovou funkcí ");
						sbISEN.append("DFA with the total transition function ");
						break;
					case 2:
						sbIS.append("minimální DFA ");
						sbISEN.append("minimal DFA ");
						sbLatexEN.append(" and minimize");
						sbLatex.append(" a minimalizujte");
						sbPlainText.append(" a minimalizujte");
						sbPlainTextEN.append(" and minimize");
						break;
					case 3:
						sbLatex.append(", minimalizujte a kanonizujte");
						sbLatexEN.append(", minimize and canonize");
						sbPlainTextEN.append(", minimize and canonize");
						sbPlainText.append(", minimimalizujte a kanonizujte");
						sbIS.append("minimální DFA v kanonickém tvaru ");
						sbISEN.append("minimal DFA in the canonical form ");
						break;
				}
				sbIS.append("pro NFA zadaný tabulkou:");
				sbISEN.append("to the NFA given with the table:");
				sbLatexEN.append(" the NFA");
				sbLatex.append(" NFA");
				sbPlainTextEN.append(" the NFA");
				sbPlainText.append(" NFA");
				break;
			case 2:
				sbLatexEN.append(" Minimize");
				sbLatex.append(" Minimalizujte");
				sbPlainText.append("Minimalizujte");
				sbPlainTextEN.append("Minimize");
				switch (operationTo)
				{
					case 2:
						sbIS.append("minimální DFA ");
						sbISEN.append("minimal DFA ");
						break;
					case 3:
						sbLatexEN.append(" and canonize");
						sbLatexEN.append(" a kanonizujte");
						sbPlainText.append(" a kanonizujte");
						sbPlainTextEN.append(" and canonize");
						sbIS.append("minimální DFA v kanonickém tvaru ");
						sbISEN.append("minimal DFA in the canonical form ");
						break;
				}
				sbIS.append("pro DFA zadaný tabulkou:");
				sbISEN.append("to the DFA given with the table:");
				sbLatex.append(" DFA");
				sbLatexEN.append(" the DFA");
				sbPlainText.append(" DFA");
				sbPlainTextEN.append(" the DFA");
				break;
			case 3:
				sbLatexEN
					.append(" Canonize the minimal DFA with the total transition function and without the unreachable states");
				sbLatex.append(" Kanonizujte minimální totální DFA bez nedosažitelných stavů");
				sbPlainTextEN
					.append("Canonize the minimal DFA with the total transition function and without the unreachable states");
				sbPlainText.append("Kanonizujte minimální totální DFA bez nedosažitelných stavů:");
				sbIS.append("DFA v kanonickém tvaru pro minimální totální DFA bez nedosažitelných stavů zadaný tabulkou:");
				sbISEN
					.append("DFA in the canonical form to the minimal DFA with the total transition function and without the unreachable states given with the table:");
				break;
		}
		sbLatexEN.append(" given with the table:" + "\n\n");
		sbLatex.append(", který je zadaný následující tabulkou:" + "\n\n");
		sbPlainText.append(", který je zadaný následující tabulkou:" + "\n\n");
		sbPlainTextEN.append(" given with the table:\n\n");
		sbIS.append("</p>\n\n");
		sbISEN.append("</p>\n\n");
		result.put("latex", sbLatex.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainText", sbPlainText.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("IS", sbIS.toString());
		result.put("ISEN", sbISEN.toString());
		return result;
	}

	/**
	 * generates automaton with given parameters or null
	 * 
	 * @param operationFrom
	 * @param numberOfStatesMin
	 * @param numberOfStatesMax
	 * @param numberOfFinalStatesMin
	 * @param numberOfFinalStatesMax
	 * @param numberOfTransitionsMin
	 * @param numberOfTransitionsMax
	 * @param sizeOfAlphabetMin
	 * @param sizeOfAlphabetMax
	 * @return automaton or null
	 */
	private Automaton callFunctionGenerateAutomaton()
	{
		Automaton a = null;
		Automaton b = null;

		do
		{
			try
			{
				switch (operationFrom)
				{
					case 0:
						do
						{
							
							a = FiniteAutomataGenerator.generateEFA(sizeOfAlphabetMin, sizeOfAlphabetMax,
								numberOfStatesMin, numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax,
								0, 0, numberOfTransitionsMin, numberOfTransitionsMax, numberOfUnreachableStatesMin,
								numberOfUnreachableStatesMax, numberOfEpsTransitionsMin, numberOfEpsTransitionsMax);
							if (a == null)
							{
								return a;
							}
							b = new Automaton(a);
							b = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(b);
						}
						while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && b.isDeterministic());
						break;
					case 1:
						a = FiniteAutomataGenerator.generateFA(sizeOfAlphabetMin, sizeOfAlphabetMax, numberOfStatesMin,
							numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax, false, 0, 0,
							numberOfTransitionsMin, numberOfTransitionsMax, numberOfUnreachableStatesMin,
							numberOfUnreachableStatesMax, false);
						break;
					case 2:
						a = FiniteAutomataGenerator.generateFA(sizeOfAlphabetMin, sizeOfAlphabetMax, numberOfStatesMin,
							numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax, totalTransitionFunction,
							0, 0, numberOfTransitionsMin, numberOfTransitionsMax, numberOfUnreachableStatesMin,
							numberOfUnreachableStatesMax, true);
						if (a == null)
						{
							return a;
						}
						break;
					case 3:
						a = FiniteAutomataGenerator.generateFA(sizeOfAlphabetMin, sizeOfAlphabetMax, numberOfStatesMin,
							numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax, true, 0, 0,
							numberOfTransitionsMin, numberOfTransitionsMax, 0, 0, true);
						totalTransitionFunction = true;
						if (a == null)
						{
							return a;
						}
						a = Minimizator.minimize(a);
						break;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				javax.swing.JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE
			&& !((a.getStates().size() >= numberOfStatesMin + numberOfUnreachableStatesMin)
				&& (a.getStates().size() <= numberOfStatesMax + numberOfUnreachableStatesMax)
				&& (totalTransitionFunction || a.getNumberOfTransitions() - a.getUnreachTransCount() >= (operationFrom == 0 ? numberOfTransitionsMin
					+ numberOfEpsTransitionsMin
					: numberOfTransitionsMin))
				&& (totalTransitionFunction || a.getNumberOfTransitions() - a.getUnreachTransCount() <= (operationFrom == 0 ? numberOfTransitionsMax
					+ numberOfEpsTransitionsMax
					: numberOfTransitionsMax)) && (a.getAlphabet().size() >= sizeOfAlphabetMin) && (a.getAlphabet()
				.size() <= sizeOfAlphabetMax)));

		if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		{
			return null;
		}
		return a;
	}

	/**
	 * returns true, if the automaton is well done, false otherwise
	 * 
	 * @param a
	 *            Automaton
	 * @return returns true, if the automaton is well done, false otherwise
	 */
	private boolean isSuitable(Automaton a)
	{
		try{
		Automaton b = new Automaton(a);
		int numberOfSteps = 0;
		switch (operationTo)
		{
			case 0:
				b = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(b);
				break;
			case 1:
				b = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(b);
				b = Determinator.getDFA(b);
				break;
			case 2:
				b = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(b);
				b = Determinator.getDFA(b);
				numberOfSteps = Minimizator.getNumberOfSteps(b);
				b = Minimizator.minimize(b);
				break;
			case 3:
				b = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(b);
				b = Determinator.getDFA(b);
				numberOfSteps = Minimizator.getNumberOfSteps(b);
				b = Minimizator.minimize(b);
				b = Canonizator.canonize(b);
				break;
		}

		if (a.getAlphabet().size() != b.getAlphabet().size())
		{
			return false;
		}
		int statesCount = b.getStates().size();
		if (resultNumberOfStatesMax > -1 && resultNumberOfStatesMin > -1)
		{
			if (statesCount < resultNumberOfStatesMin || statesCount > resultNumberOfStatesMax)
			{
				return false;
			}
		}
		int transitionsCount = b.getNumberOfTransitions();
		if (resultNumberOfTransitionsMax > -1 && resultNumberOfTransitionsMin > -1)
		{
			if (transitionsCount < resultNumberOfTransitionsMin || transitionsCount > resultNumberOfTransitionsMax)
			{
				return false;
			}
		}
		if (numberOfStepsMax > -1 && numberOfStepsMin > -1 && (operationTo == 2 || operationTo == 3))
		{
			if (numberOfSteps < numberOfStepsMin || numberOfSteps > numberOfStepsMax)
			{
				return false;
			}
		}
		if (firstStateNotInitial)
		{
			if (a.getStates().size() < 2)
			{
				return false;
			}
		}

		return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * generates the result for the given operations
	 * 
	 * @param operationFrom
	 *            beginning of interval
	 * @param operationTo
	 *            end of interval
	 * @param a
	 *            automaton
	 * @param steps
	 *            steps of minimization
	 * @return result as a Map<String,String>
	 */
	private Map<String, String> generateResults(Automaton a)
	{
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		Automaton b = new Automaton(a);
		String from = "";
		String to = "";
		sbIS.append("\n");
		switch (operationTo)
		{
			case 0:
				from = "NFA";
				to = "NFA";
				break;
			case 1:
				from = "DFA";
				to = "TOT";
				break;
			case 2:
				from = "DFA";
				to = "MIN";
				break;
			case 3:
				from = "DFA";
				to = "MIC";
				break;
		}

		// epsilon transfers elimination
		if (operationFrom == 0)
		{
			sbLatexEN.append("\\\\ $\\varepsilon$ surrounding:\\\\");
			sbPlainTextEN.append("NFA without epsilon-steps:\n");
			sbLatex.append("\\\\ $\\varepsilon$ okolí:\\\\");
			sbPlainText.append("NFA bez epsilon-kroků:\n");
			b = EpsilonTransitionsEliminator.eliminateEpsilonTransitions(b);

			sbLatexEN.append(b.epsSurroundStringLatex() + "\\\\ \n" + "The NFA without epsilon-transitions:\\\\ \n");
			sbLatexEN.append(b.toLaTeX() + "\n");
			sbPlainTextEN.append(b.epsSurroundString() + "\n");
			sbPlainTextEN.append(b.toString() + "\n");
			sbLatex.append(b.epsSurroundStringLatex() + "\\\\ \n" + "NFA bez $\\varepsilon$-kroků:\\\\ \n\n");
			sbLatex.append(b.toLaTeX() + "\n");
			sbPlainText.append(b.epsSurroundString() + "\n");
			sbPlainText.append(b.toString() + "\n");

			if (operationTo == 0)
			{
				sbIS.append(b.toIS(from, to));
			}
		}
		// determinization
		if (operationFrom <= 1)
		{
			if (operationTo > 0)
			{
				sbLatexEN.append("\n\\noindent\nThe DFA:\n\n");
				sbPlainTextEN.append("The DFA:\n");
				sbLatex.append("\n\\noindent\nDFA:\n\n");
				sbPlainText.append("DFA:\n");
				b = Determinator.getDFA(b);
				sbLatexEN.append(b.toLaTeX() + "\n");
				sbPlainTextEN.append(b.toString() + "\n");
				sbLatex.append(b.toLaTeX() + "\n");
				sbPlainText.append(b.toString() + "\n");
				if (operationTo == 1)
				{
					sbIS.append(b.toIS(from, to));
				}
			}
		}
		// minimization
		if (operationFrom <= 2)
		{
			if (operationTo >= 2)
			{

				// removing unreachable states
				if (b.getNumberOfUnreachableStates() != 0)
				{
					b.removeUnreachableStates();
					if (FormalLanguagesExampleGenerator.getCoreInstance().isShowSubresults())
					{
						sbLatexEN.append("\n\\noindent\nThe DFA without unreachable states:\n\n");
						sbPlainTextEN.append("The DFA without unreachable states:\n");
						sbLatexEN.append(b.toLaTeX() + "\n");
						sbPlainTextEN.append(b.toString() + "\n");
						sbLatex.append("\n\\noindent\nDFA bez nedosažitelných stavů:\n\n");
						sbPlainText.append("DFA bez nedosažitelných stavů:\n");
						sbLatex.append(b.toLaTeX() + "\n");
						sbPlainText.append(b.toString() + "\n");
					}
				}

				// transition function total
				if (!b.isTransitionFunctionTotal())
				{
					b.makeTransitionFunctionTotal();
					if (FormalLanguagesExampleGenerator.getCoreInstance().isShowSubresults())
					{
						sbLatexEN.append("\n\\noindent\nThe DFA with total transition function:\n\n");
						sbPlainTextEN.append("The DFA with total transition function:\n");
						sbLatexEN.append(b.toLaTeX() + "\n");
						sbPlainTextEN.append(b.toString() + "\n");
						sbLatex.append("\n\\noindent\nDFA s totální přechodovou funkcí:\n\n");
						sbPlainText.append("DFA s totální přechodovou funkcí:\n");
						sbLatex.append(b.toLaTeX() + "\n");
						sbPlainText.append(b.toString() + "\n");
					}
				}
				if (stepsWriteOut)
				{
					sbLatexEN.append("\n\\noindent\nThe steps of the minimization:\n\n");
					sbPlainTextEN.append("The steps of the minimization:\n");
					sbLatexEN.append(Minimizator.stepsToLaTeX(b) + "\n");
					sbPlainTextEN.append(Minimizator.stepsToString(b) + "\n");
					sbLatex.append("\n\\noindent\nKroky minimalizace:\n\n");
					sbPlainText.append("Kroky minimalizace:\n");
					sbLatex.append(Minimizator.stepsToLaTeX(b) + "\n");
					sbPlainText.append(Minimizator.stepsToString(b) + "\n");
				}
				b = Minimizator.minimize(b);
				sbLatexEN.append("\n\\noindent\nThe minimal DFA:\n\n");
				sbPlainTextEN.append("The minimal DFA:\n");
				sbLatexEN.append(b.toLaTeX() + "\n");
				sbPlainTextEN.append(b.toString() + "\n");
				sbLatex.append("\n\\noindent\nMinimální DFA:\n\n");
				sbPlainText.append("Minimální DFA:\n");
				sbLatex.append(b.toLaTeX() + "\n");
				sbPlainText.append(b.toString() + "\n");
				if (operationTo == 2)
				{
					sbIS.append(b.toIS(from, to));
				}
			}
		}
		// canonization
		if (operationFrom <= 3)
		{
			if (operationTo == 3)
			{
				b = Canonizator.canonize(b);
				sbLatexEN.append("\n\\noindent\nThe minimal DFA in the canonical form:\n\n");
				sbPlainTextEN.append("The minimal DFA in the canonical form:\n");
				sbLatexEN.append(b.toLaTeX() + "\n");
				sbPlainTextEN.append(b.toString() + "\n");
				sbLatex.append("\n\\noindent\nMinimální DFA v kanonickém tvaru:\n\n");
				sbPlainText.append("Minimální DFA v kanonickém tvaru:\n");
				sbLatex.append(b.toLaTeX() + "\n");
				sbPlainText.append(b.toString() + "\n");
				if (operationTo == 3)
				{
					sbIS.append(b.toIS(from, to));
				}
			}
		}
		Map<String, String> result = new HashMap<String, String>();
		result.put("latex", sbLatex.toString());
		result.put("plainText", sbPlainText.toString());
		result.put("IS", sbIS.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("ISEN", sbIS.toString());
		return result;
	}

}
