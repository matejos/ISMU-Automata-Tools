/*
 * The class makes exercises for equivalence of NFA and regular grammar
 */

package generator.modules.reglang;

import generator.common.GeneratingLogic;
import generator.common.GeneratorWorker;
import generator.communication.dto.ExampleDTO;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.reglang.automaton.Automaton;
import generator.modules.reglang.automaton.AutomatonModificator;
import generator.modules.reglang.automaton.FiniteAutomataGenerator;
import generator.modules.reglang.automaton.NoSuchStateException;
import generator.modules.reglang.regularGrammar.NoSuchVariableException;
import generator.modules.reglang.regularGrammar.RegularGrammar;
import generator.modules.reglang.regularGrammar.RegularGrammarConverter;
import generator.modules.reglang.regularGrammar.RegularGrammarGenerator;
import generator.modules.reglang.regularGrammar.RegularGrammarModificator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The class T0Generator stands for the background thread, which generates exercises for equivalence of finite automata
 * and regular grammars
 * 
 * @author Jana Kadlecova
 */
public class T2Generator extends GeneratorWorker implements GeneratingLogic
{

	// general atributes
	private int operation;
	private int exercises;
	private boolean firstStateIsFinal;

	// atributes for operation 0
	private int numberOfStatesMin;
	private int numberOfStatesMax;
	private int numberOfFinalStatesMin;
	private int numberOfFinalStatesMax;
	private int numberOfTransitionsMin;
	private int numberOfTransitionsMax;
	private int sizeOfAlphabetMin;
	private int sizeOfAlphabetMax;
	private int alphabet;
	private int states;
	// optional attributes

	private int resultNumberOfVariablesMin; // -1, if is not set
	private int resultNumberOfVariablesMax; // -1, if is not set
	private int resultNumberOfRulesMin; // -1, if is not set
	private int resultNumberOfRulesMax; // -1, if is not set
	private int resultNumberOfTerminalsMin; // -1, if is not set
	private int resultNumberOfTerminalsMax; // -1, if is not set

	// atributes for operation 1
	private boolean epsilon;

	private int minLoops;
	private int maxLoops;
	private int variables;
	private int numberOfVariablesMin;
	private int numberOfVariablesMax;
	private int numberOfRulesMin;
	private int numberOfRulesMax;
	private int numberOfTerminalsMin;
	private int numberOfTerminalsMax;
	private int resultNumberOfStatesMin;
	private int resultNumberOfStatesMax;
	private int resultNumberOfTransitionsMin; // -1, if is not set
	private int resultNumberOfTransitionsMax; // -1, if is not set
	private int resultSizeOfAlphabetMin; // -1, if is not set
	private int resultSizeOfAlphabetMax; // -1, if is not set
	private int resultNumberOfUnreachableStatesMin; // -1, if is not set
	private int resultNumberOfUnreachableStatesMax; // -1, if is not set

	private boolean isomorphism = false;
	private int isoPercent = 0;

	/**
	 * Constructor, which makes new backgroud thread. The thread generates the exercises for converting NFA to regular
	 * grammar
	 * 
	 * @param frame
	 *            ExerciseGeneratorGUI
	 * @param output
	 *            JTextAreas, where should be the result printed
	 * @param exercises
	 *            number of exercises
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
	 * @param epsilon
	 *            resulting grammar: the rule S -> epsilon required
	 * @param numberOfUnreachableStatesMin
	 *            number of unreachable sates min
	 * @param numberOfUnreachableStatesMax
	 *            number of unreachable states max
	 * @param resultNumberOfVariablesMin
	 *            resulting grammar: number of non-terminals min
	 * @param resultNumberOfVariablesMax
	 *            resulting grammar: number of non-terminals max
	 * @param resultNumberOfRulesMin
	 *            resulting grammar: number of rules min
	 * @param resultNumberOfRulesMax
	 *            resulting grammar: number of rules max
	 * @param resultNumberOfTerminalsMin
	 *            resulting grammar: number of terminals min
	 * @param resultNumberOfTerminalsMax
	 *            resulting grammar: number of terminals max
	 */
	public T2Generator(int exercises, int numberOfStatesMin, int numberOfStatesMax, int numberOfFinalStatesMin,
		int numberOfFinalStatesMax, int numberOfTransitionsMin, int numberOfTransitionsMax, int sizeOfAlphabetMin,
		int sizeOfAlphabetMax, int alphabet, int states, boolean epsilon, int numberOfUnreachableStatesMin,
		int numberOfUnreachableStatesMax, int resultNumberOfVariablesMin, int resultNumberOfVariablesMax,
		int resultNumberOfRulesMin, int resultNumberOfRulesMax, int resultNumberOfTerminalsMin,
		int resultNumberOfTerminalsMax, boolean firstStateIsFinal, boolean isomorphism, int isoPercent)
	{

		this.firstStateIsFinal = firstStateIsFinal;
		this.operation = 0;
		this.numberOfStatesMin = numberOfStatesMin;
		this.numberOfStatesMax = numberOfStatesMax;
		this.numberOfFinalStatesMin = numberOfFinalStatesMin;
		this.numberOfFinalStatesMax = numberOfFinalStatesMax;
		this.numberOfTransitionsMin = numberOfTransitionsMin;
		this.numberOfTransitionsMax = numberOfTransitionsMax;
		this.sizeOfAlphabetMin = sizeOfAlphabetMin;
		this.sizeOfAlphabetMax = sizeOfAlphabetMax;
		this.alphabet = alphabet;
		this.states = states;

		this.resultNumberOfVariablesMin = resultNumberOfVariablesMin;
		this.resultNumberOfVariablesMax = resultNumberOfVariablesMax;
		this.resultNumberOfRulesMin = resultNumberOfRulesMin;
		this.resultNumberOfRulesMax = resultNumberOfRulesMax;
		this.resultNumberOfTerminalsMin = resultNumberOfTerminalsMin;
		this.resultNumberOfTerminalsMax = resultNumberOfTerminalsMax;

		this.epsilon = epsilon;

		this.exercises = exercises;

		this.isomorphism = isomorphism;
		this.isoPercent = isoPercent;
	}

	/**
	 * Constructor, which makes new backgroud thread. The thread generates the exercises for converting regular grammar
	 * to NFA
	 * 
	 * @param numberOfVariablesMin
	 *            number of non-terminals min
	 * @param numberOfVariablesMax
	 *            number of non-terminals max
	 * @param numberOfRulesMin
	 *            number of rules min
	 * @param numberOfRulesMax
	 *            number of rules max
	 * @param numberOfTerminalsMin
	 *            number of terminals min
	 * @param numberOfTerminalsMax
	 *            number of terminals max
	 * @param terminals
	 *            type of the terminals: 0 -- a, b, c (lower case); 1 -- A, B, C (upper case); 2 -- 1, 2, 3 (numbers); 3
	 *            -- x, y, z (lower case end of rhe alphabet); 4 -- X, Y, Z (upper case end of the alphabet); 5 -- t0,
	 *            t1, t2 (letter 't' with index); 6 -- p0, p1, p2 (letter 'p' with index); 7 -- I, II, III, IV (roman
	 *            numbers); 8 -- i, ii, iii, iv (roman numbers lower case); 9 -- 1, 10, 11, 100 (binary numbers)
	 * @param variables
	 *            type of the non-terminals: 0 -- a, b, c (lower case); 1 -- A, B, C (upper case); 2 -- 1, 2, 3
	 *            (numbers); 3 -- x, y, z (lower case end of rhe alphabet); 4 -- X, Y, Z (upper case end of the
	 *            alphabet); 5 -- q0, q1, q2 (letter 'q' with index); 6 -- s0, s1, s2 (letter 's' with index); 7 -- I,
	 *            II, III, IV (roman numbers); 8 -- i, ii, iii, iv (roman numbers lower case); 9 -- 1, 10, 11, 100
	 *            (binary numbers)
	 * @param epsilon
	 *            the rule S -> epsilon required
	 * @param resultNumberOfStatesMin
	 *            number of states min
	 * @param resultNumberOfStatesMax
	 *            number of states max
	 * @param resultNumberOfTransitionsMin
	 *            number of transitions min
	 * @param resultNumberOfTransitionsMax
	 *            number of transitions max
	 * @param resultSizeOfAlphabetMin
	 *            size of alphabet min
	 * @param resultSizeOfAlphabetMax
	 *            size of alphabet max
	 * @param resultNumberOfUnreachableStatesMin
	 *            number of unreachable states min
	 * @param resultNumberOfUnreachableStatesMax
	 *            number of unreachable states max
	 */
	public T2Generator(int exercises, int numberOfVariablesMin, int numberOfVariablesMax, int numberOfRulesMin,
		int numberOfRulesMax, int numberOfTerminalsMin, int numberOfTerminalsMax, int terminals, int variables,
		boolean epsilon, int resultNumberOfStatesMin, int resultNumberOfStatesMax, int resultNumberOfTransitionsMin,
		int resultNumberOfTransitionsMax, int resultSizeOfAlphabetMin, int resultSizeOfAlphabetMax,
		int resultNumberOfUnreachableStatesMin, int resultNumberOfUnreachableStatesMax, int minLoops, int maxLoops, boolean isomorphism, int isoPercent)
	{

		this.operation = 1;
		this.numberOfVariablesMin = numberOfVariablesMin;
		this.numberOfVariablesMax = numberOfVariablesMax;
		this.numberOfRulesMin = numberOfRulesMin;
		this.numberOfRulesMax = numberOfRulesMax;
		this.numberOfTerminalsMin = numberOfTerminalsMin;
		this.numberOfTerminalsMax = numberOfTerminalsMax;

		this.variables = variables;
		this.epsilon = epsilon;

		this.numberOfStatesMin = numberOfVariablesMin;
		this.numberOfStatesMax = numberOfVariablesMax;

		this.numberOfFinalStatesMin = 1;
		this.numberOfFinalStatesMax = 3;
		this.numberOfTransitionsMin = numberOfRulesMin;
		this.numberOfTransitionsMax = numberOfRulesMax;
		this.sizeOfAlphabetMin = numberOfTerminalsMin;
		this.sizeOfAlphabetMax = numberOfTerminalsMax;
		this.alphabet = terminals;
		this.states = variables;

		this.resultNumberOfStatesMin = resultNumberOfStatesMin;
		this.resultNumberOfStatesMax = resultNumberOfStatesMax;
		this.resultNumberOfTransitionsMin = resultNumberOfTransitionsMin;
		this.resultNumberOfTransitionsMax = resultNumberOfTransitionsMax;
		this.resultSizeOfAlphabetMin = resultSizeOfAlphabetMin;
		this.resultSizeOfAlphabetMax = resultSizeOfAlphabetMax;
		this.resultNumberOfUnreachableStatesMin = resultNumberOfUnreachableStatesMin;
		this.resultNumberOfUnreachableStatesMax = resultNumberOfUnreachableStatesMax;

		this.minLoops = minLoops;
		this.maxLoops = maxLoops;
		this.exercises = exercises;

		this.isomorphism = isomorphism;
		this.isoPercent = isoPercent;
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
		// calculate
		for (int i = 0; i < exercises; i++)
		{
			if (operation == 0)
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
				while (!isSuitable(a) && FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE);
				if (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
				{
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
					// Automaton and the row with text "Result"

					sbLatex.append(a.toLaTeX() + "\\noindent \\textbf{Řešení. }\n");
					sbLatexEN.append(a.toLaTeX() + "\\noindent \\textbf{Solution. }\n");
					sbPlainText.append(a.toString() + "\n\nŘešení:\n\n");
					sbPlainTextEN.append(a.toString() + "\n\nSolution:\n\n");
					sbIS.append("<M>" + a.toLaTeX().replace("$", "") + "</M>\n");
					sbISEN.append("<M>" + a.toLaTeX().replace("$", "") + "</M>\n");

					// result
					Map<String, String> generatedResult = generateResults(a);
					sbLatexEN.append(generatedResult.get("latexEN") + "\\bigskip \n\n");
					sbPlainText.append(generatedResult.get("plainText") + "\n\n");
					sbIS.append(generatedResult.get("IS"));
					sbLatex.append(generatedResult.get("latex") + "\\bigskip \n\n");
					sbPlainTextEN.append(generatedResult.get("plainTextEN") + "\n\n");
					sbISEN.append(generatedResult.get("ISEN"));

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
			// Grammar to automaton
			else if (operation == 1)
			{

				RegularGrammar g = null;
				// generates automaton with the given parameters

				g = callFunctionGenerateGrammar();

				if (g == null)
				{
					break;
				}

				if (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
				{
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

					RegularGrammarModificator.modifyVariables(g, variables);
					sbLatex.append("Gramatika " + g.toLaTeX().replace("where", "kde").replace("Grammar", "Gramatika")
						+ "\n\n\\subsection*{Řešení}\n");
					sbLatexEN.append("Grammar " + g.toLaTeX() + "\n\n\\subsection*{Result}\n");
					sbPlainText.append(g.toString().replace("Grammar", "Gramatika").replace("where", "kde")
						+ "\n\nŘešení:\n\n");
					sbPlainTextEN.append(g.toString() + "\n\nResult:\n\n");
					sbIS.append("Gramatika <M>"
						+ g.toLaTeX().replace("$", "").replace("where", "</M> kde").replace("\\[", "<M>")
							.replace("\\]", "</M>") + "\n");
					sbISEN.append("Grammar <M>"
						+ g.toLaTeX().replace("$", "").replace("where", "</M> where").replace("\\[", "<M>")
							.replace("\\]", "</M>") + "\n");

					// result
					Map<String, String> generatedResult = generateResults(g);
					sbLatexEN.append(generatedResult.get("latexEN") + "\\bigskip \n\n");
					sbPlainText.append(generatedResult.get("plainText") + "\n\n");
					sbIS.append(generatedResult.get("IS"));
					sbLatex.append(generatedResult.get("latex") + "\\bigskip \n\n");
					sbPlainTextEN.append(generatedResult.get("plainTextEN") + "\n\n");
					sbISEN.append(generatedResult.get("ISEN"));

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
				else
				{
					throw new UnsupportedOperationException("Given operation is not supported");

				}
			}

		}
		// the end of some kinds of exercises is set
		sbLatex.append("\n\\end{document}\n");
		sbLatexEN.append("\n\\end{document}\n");
		// ready exercises are put into the map result
		result.put("latex", sbLatex.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainText", sbPlainText.toString().replace("transition function", "přechodová funkce"));
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("IS", sbIS.toString());
		result.put("ISEN", sbISEN.toString());
	}

	private RegularGrammar callFunctionGenerateGrammar()
	{
		RegularGrammar g = null;
		do
		{
			try
			{
				g = RegularGrammarGenerator.generateGrammar(numberOfRulesMin, numberOfRulesMax, numberOfTerminalsMin,
					numberOfTerminalsMax, numberOfVariablesMin, numberOfVariablesMax, epsilon, minLoops, maxLoops);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		while (!((g.getNonterminals().size() >= numberOfVariablesMin)
			&& (g.getNonterminals().size() <= numberOfVariablesMax)
			&& (g.getTerminals().size() >= numberOfTerminalsMin) && (g.getTerminals().size() <= numberOfTerminalsMax)
			&& (g.getRulesCount() >= numberOfRulesMin) && (g.getRulesCount() <= numberOfRulesMax))
			&& (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE));
		if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		{
			return null;
		}
		return g;
	}

	private Automaton callFunctionGenerateAutomaton()
	{
		Automaton a = null;
		do
		{
			try
			{
				a = FiniteAutomataGenerator.generateFA(sizeOfAlphabetMin, sizeOfAlphabetMax, numberOfStatesMin,
					numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax, false, 0, 0,
					numberOfTransitionsMin, numberOfTransitionsMax, 0, 0, false);
				//
				// a = AutomatonGenerator.generateAutomaton(numberOfStatesMin, numberOfStatesMax,
				// numberOfFinalStatesMin,
				// numberOfFinalStatesMax, numberOfTransitionsMin, numberOfTransitionsMax, sizeOfAlphabetMin,
				// sizeOfAlphabetMax, numberOfUnreachableStatesMin, numberOfUnreachableStatesMax, false, false, true);
				if (a == null)
				{
					return a;
				}
				break;

			}
			catch (Exception ex)
			{
				javax.swing.JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		while (!((a.getStates().size() >= numberOfStatesMin) && (a.getStates().size() <= numberOfStatesMax)
			&& (a.getNumberOfTransitions() >= numberOfTransitionsMin)
			&& (a.getNumberOfTransitions() <= numberOfTransitionsMax) && (a.getAlphabet().size() >= sizeOfAlphabetMin)
			&& (a.getAlphabet().size() <= sizeOfAlphabetMax) && (firstStateIsFinal && a.getFinalStates().contains(
			a.getStartState())))
			&& (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE));
		if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		{
			return null;
		}
		return a;
	}

	private Map<String, String> generateResults(Automaton a)
	{
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();

		RegularGrammar g;
		sbIS.append("\n");

		// FA -> regular grammar

		sbIS.append(a.toIS("NFA", "GRA", isomorphism, isoPercent));
		g = RegularGrammarConverter.convertAutomatonToRegularGrammar(a);
		sbLatexEN.append("Grammar " + g.toLaTeX() + "\n");
		sbPlainTextEN.append(g.toString() + "\n");
		sbLatex.append("Gramatika " + g.toLaTeX().replace("where", "kde").replace("Grammar", "Gramatika") + "\n");
		sbPlainText.append(g.toString().replace("where", "kde").replace("Grammar", "Gramatika") + "\n");

		Map<String, String> result = new HashMap<String, String>();
		result.put("latex", sbLatex.toString());
		result.put("plainText",
			sbPlainText.toString().replace("transition function", "přechodová funkce").replace("Automaton", "Automat")
				.replace("Grammar", "Gramatika"));
		result.put("IS", sbIS.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("ISEN", sbIS.toString());
		return result;
	}

	private Map<String, String> generateResults(RegularGrammar g)
	{
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		Automaton b = null;

		sbIS.append("\n");

		try
		{
			b = GrammarTransformator.convertGrammarToAutomaton(g);
		}
		catch (NoSuchVariableException | NoSuchStateException e)
		{
			e.printStackTrace();
		}
		String from = "NFA";
		sbLatexEN.append(" The NFA without $\\varepsilon$-transitions:\n\n");
		sbPlainTextEN.append("The NFA without epsilon-transitions:\n");
		sbLatex.append(" NFA bez $\\varepsilon$-kroků:\n\n");
		sbPlainText.append("NFA bez epsilon-kroků:\n");
		sbLatexEN.append(b.toLaTeX() + "\n");
		sbPlainTextEN.append(b.toString() + "\n");
		sbLatex.append(b.toLaTeX() + "\n");
		sbPlainText.append(b.toString() + "\n");
		sbIS.append(b.toIS(from, from, isomorphism, isoPercent));

		Map<String, String> result = new HashMap<String, String>();
		result.put("latex", sbLatex.toString());
		result.put("plainText",
			sbPlainText.toString().replace("transition function", "přechodová funkce").replace("Automaton", "Automat")
				.replace("Grammar", "Gramatika"));
		result.put("IS", sbIS.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("ISEN", sbIS.toString());
		return result;
	}

	private boolean isSuitable(Automaton a)
	{
		RegularGrammar g = RegularGrammarConverter.convertAutomatonToRegularGrammar(a);
		try
		{
			if (g.getRulesFromTheVariable(g.getStartVariable()).isEmpty())
			{
				return false;
			}
		}
		catch (NoSuchVariableException e)
		{
			return false;
		}
		if (operation == 1)
		{
			return isSuitable(g);
		}
		if (a.getAlphabet().size() != g.getTerminals().size())
		{
			return false;
		}
		int i = g.getNumberOfRules();
		if (this.resultNumberOfRulesMin > -1 && this.resultNumberOfRulesMax > -1)
		{
			if (i < this.resultNumberOfRulesMin || i > this.resultNumberOfRulesMax)
			{
				return false;
			}
		}
		i = g.getTerminals().size();
		if (this.resultNumberOfTerminalsMin > -1 && this.resultNumberOfTerminalsMax > -1)
		{
			if (i < this.resultNumberOfTerminalsMin || i > this.resultNumberOfTerminalsMax)
			{
				return false;
			}
		}
		i = g.getVariables().size();
		if (this.resultNumberOfVariablesMin > -1 && this.resultNumberOfVariablesMax > -1)
		{
			if (i < this.resultNumberOfVariablesMin || i > this.resultNumberOfVariablesMax)
			{
				return false;
			}
		}
		if (epsilon & !g.containsEpsilon())
		{
			return false;
		}
		return true;
	}

	private boolean isSuitable(RegularGrammar g)
	{
		Automaton a = RegularGrammarConverter.convertRegularGrammarToAutomaton(g);
		if (g.getNumberOfRules() < numberOfRulesMin || g.getNumberOfRules() > numberOfRulesMax)
		{
			return false;
		}
		if (g.getTerminals().size() > numberOfTerminalsMax || g.getTerminals().size() < numberOfTerminalsMin)
		{
			return false;
		}
		if (g.getVariables().size() < numberOfVariablesMin || g.getVariables().size() > numberOfVariablesMax)
		{
			return false;
		}
		if (a.getAlphabet().size() != g.getTerminals().size())
		{
			return false;
		}
		if (epsilon && !g.containsEpsilon())
		{
			return false;
		}
		int i = a.getAlphabet().size();
		if (this.resultSizeOfAlphabetMin > -1 && this.resultSizeOfAlphabetMax > -1)
		{
			if (i < this.resultSizeOfAlphabetMin || i > this.resultSizeOfAlphabetMax)
			{
				return false;
			}
		}
		i = a.getStates().size();
		if (this.resultNumberOfStatesMin > -1 && this.resultNumberOfStatesMax > -1)
		{
			if (i < this.resultNumberOfStatesMin || i > this.resultNumberOfStatesMax)
			{
				return false;
			}
		}
		i = a.getNumberOfTransitions();
		if (this.resultNumberOfTransitionsMin > -1 && this.resultNumberOfTransitionsMax > -1)
		{
			if (i < this.resultNumberOfTransitionsMin || i > this.resultNumberOfTransitionsMax)
			{
				return false;
			}
		}
		i = a.getNumberOfUnreachableStates();
		if (this.resultNumberOfUnreachableStatesMin > -1 && this.resultNumberOfUnreachableStatesMax > -1)
		{
			if (i < this.resultNumberOfUnreachableStatesMin || i > this.resultNumberOfUnreachableStatesMax)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * sets the task and return it as the map with keys: "latex" "latexEN" "plainText" "plainTextEN" "IS" "ISEN"
	 * 
	 * @return task as the map: "latex" -> task in latex as the string... "latexEN" -> task in latex in English
	 *         "plainText" -> task in plain text "plainTextEN" -> task in plain text in English "IS" -> taks in form to
	 *         the IS "ISEN" -> task in form to the IS in English
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
		sbIS.append("<p>Najděte jazykově ekvivalentní");
		sbISEN.append("<p>Find a language equivalent");
		sbLatexEN.append(" Find a language equivalent");
		sbLatex.append(" Najděte jazykově ekvivalentní");
		sbPlainText.append("Najděte jazykově ekvivalentní");
		sbPlainTextEN.append("Find a language equivalent");
		switch (operation)
		{
			case 0:
				sbLatexEN
					.append(" regular grammar to the NFA without $\\varepsilon$-transitions given with the table:\n\n");
				sbLatex
					.append(" regulární gramatiku pro NFA bez $\\varepsilon$-kroků, který je zadaný následující tabulkou:"
						+ "\n\n");
				sbPlainText
					.append(" regulární gramatiku pro NFA bez epsilon-kroků, který je zadaný následující tabulkou:"
						+ "\n\n");
				sbPlainTextEN
					.append(" regular grammar to the NFA without epsilon-transitions given with the table:\n\n");
				sbIS.append(" regulární gramatiku pro NFA  bez <M>\\varepsilon</M>-kroků zadaný tabulkou:</p>\n\n");
				sbISEN
					.append(" regular grammar to the NFA without <M>\\varepsilon</M>-transitions given with the table:</p>\n\n");
				break;
			case 1:
				sbLatex.append(" NFA bez $\\varepsilon$-kroků k zadané regulární gramatice:\n\n");
				sbLatexEN.append(" NFA without $\\varepsilon$-transitions to the given regular grammar:\n\n");
				sbPlainText.append(" NFA bez epsilon-kroků k dané regulární gramatice:\n\n");
				sbPlainTextEN.append(" NFA without epsilon-transitions to the given regular grammar:\n\n");
				sbIS.append(" NFA  bez <M>\\varepsilon</M>-kroků k dané regulární gramatice:</p>\n\n");
				sbISEN.append(" NFA without <M>\\varepsilon</M>-transitions to the given regular grammar:</p>\n\n");
				break;

		}
		result.put("latex", sbLatex.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainText", sbPlainText.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("IS", sbIS.toString());
		result.put("ISEN", sbISEN.toString());
		return result;
	}

}
