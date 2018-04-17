/*
 * The class makes exercises for equivalence of FAs and REs
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
import generator.modules.reglang.regularExpression.RegularExpressionConverter;
import generator.modules.reglang.regularExpression.RegularExpressionGenerator;
import generator.modules.reglang.regularExpression.RegularExpressionNode;
import generator.modules.reglang.regularExpression.RegularExpressionStatistics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The class T3Generator stands for the backgroud thread, which generates the exercises for equivalence of finite
 * automatons and regular expressions
 * 
 * @author Jana Kadlecova
 */
public class T3Generator extends GeneratorWorker implements GeneratingLogic
{

	// general atributes
	private int exercises;

	private int operation; // 0 or 1

	// attributes for operation 0, 1
	private int numberOfStatesMin;
	private int numberOfStatesMax;
	private int numberOfUnreachableStatesMin;
	private int numberOfUnreachableStatesMax;
	private int numberOfFinalStatesMin;
	private int numberOfFinalStatesMax;
	private int numberOfTransitionsMin;
	private int numberOfTransitionsMax;
	private int sizeOfAlphabetMin;
	private int sizeOfAlphabetMax;
	private int alphabet;
	private int states;

	private int unionMin;
	private int unionMax;
	private int concatMin;
	private int concatMax;
	private int iterMin;
	private int iterMax;
	private int epsMin;
	private int epsMax;
	private int emptySetMin;
	private int emptySetMax;

	private boolean hasEps;

	private boolean isomorphism = false;
	private int isoPercent = 0;

	/**
	 * Constructor, which makes new backgroud thread. The thread generates the exercises for converting DFA -> regular
	 * expression
	 * 
	 * @param frame
	 *            ExerciseGeneratorGUI
	 * @param output
	 *            JTextAreas, where should be the result printed
	 * @param exercises
	 *            number of exercises
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
	 * @param sizeOfAlphabetMin
	 *            size of alphabet min
	 * @param sizeOfAlphabetMax
	 *            size of alphabet max
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
	 * @param resultSizeOfAlphabetMin
	 *            resulting RE: size of alphabet min
	 * @param resultSizeOfAlphabetMax
	 *            resulting RE:size of alphabet max
	 * @param resultLengthMin
	 *            resulting RE: length of the regular expression min
	 * @param resultLengthMax
	 *            resulting RE: length of the regular expression min
	 */
	public T3Generator(int exercises, int alphabet, int states, int sizeOfAlphabetMin, int sizeOfAlphabetMax,
		int numberOfStatesMin, int numberOfStatesMax, int numberOfUnreachableMin, int numberOfUnreachableMax,
		int numberOfFinalStatesMin, int numberOfFinalStatesMax, int numberOfTransitionsMin, int numberOfTransitionsMax,
		int resultSizeOfAlphabetMin, int resultSizeOfAlphabetMax, int resultLengthMin, int resultLengthMax,
		int unionMin, int unionMax, int concatMin, int concatMax, int iterMin, int iterMax, boolean hasEps)
	{

		this.operation = 0;

		this.exercises = exercises;

		this.numberOfStatesMin = numberOfStatesMin;
		this.numberOfStatesMax = numberOfStatesMax;
		this.numberOfUnreachableStatesMin = numberOfUnreachableMin;
		this.numberOfUnreachableStatesMax = numberOfUnreachableMax;
		this.numberOfFinalStatesMin = numberOfFinalStatesMin;
		this.numberOfFinalStatesMax = numberOfFinalStatesMax;
		this.numberOfTransitionsMin = numberOfTransitionsMin;
		this.numberOfTransitionsMax = numberOfTransitionsMax;
		this.sizeOfAlphabetMin = sizeOfAlphabetMin;
		this.sizeOfAlphabetMax = sizeOfAlphabetMax;
		this.alphabet = alphabet;
		this.states = states;

		this.unionMin = unionMin;
		this.unionMax = unionMax;
		this.concatMin = concatMin;
		this.concatMax = concatMax;
		this.iterMin = iterMin;
		this.iterMax = iterMax;

		this.hasEps = hasEps;
	}

	// RE->EFA
	public T3Generator(int exercises, int sizeOfAlphabetMin, int sizeOfAlphabetMax, int alphabet, int unionMin,
		int unionMax, int concatMin, int concatMax, int iterMin, int iterMax, int epsMin, int epsMax, int emptySetMin,
		int emptySetMax, boolean isomorphism, int isoPercent)
	{

		this.operation = 1;
		this.exercises = exercises;
		this.sizeOfAlphabetMin = sizeOfAlphabetMin;
		this.sizeOfAlphabetMax = sizeOfAlphabetMax;
		this.alphabet = alphabet;
		this.unionMin = unionMin;
		this.unionMax = unionMax;
		this.concatMin = concatMin;
		this.concatMax = concatMax;
		this.iterMin = iterMin;
		this.iterMax = iterMax;
		this.epsMin = epsMin;
		this.epsMax = epsMax;
		this.emptySetMin = emptySetMin;
		this.emptySetMax = emptySetMax;

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
		if (operation == 0)
		{
			for (int i = 0; i < exercises; i++)
			{
				Automaton a = null;
				RegularExpressionNode re = null;
				do
				{
					a = callFunctionGenerateAutomaton();
					AutomatonModificator.modifyAlphabet(a, alphabet);
					re = RegularExpressionConverter.convertAutomatonToRegularExpression(a);

					if (a == null)
					{
						break;
					}
				}
				while (!isSuitable(a, re) && FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE);
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
					// changes the names of the states
					AutomatonModificator.modifyStates(a, states);
					try
					{
						// Automaton and the row with text "Result"

						sbLatex.append(a.toLaTeX() + "\\noindent \\textbf{Řešení. }\n");
						sbLatexEN.append(a.toLaTeX() + "\\noindent \\textbf{Solution. }\n");
						sbPlainText.append(a.toString().replace("transition function", "přechodová funkce")
							+ "\n\nŘešení:\n\n");
						sbPlainTextEN.append(a.toString() + "\n\nSolution:\n\n");
						sbLatexEN.append("\\noindent\nThe regular expression:\n\n");
						sbPlainTextEN.append("The regular expression:\n");
						sbLatex.append("\\noindent\nRegulární výraz:\n\n");
						sbPlainText.append("Regulární výraz:\n");
						sbIS.append("<M>" + a.toLaTeX().replace("$", "") + "</M>\n");
						sbISEN.append("<M>" + a.toLaTeX().replace("$", "") + "</M>\n");

						// result
						try
						{
							Map<String, String> generatedResult = generateResultsFromRE(re);

							sbLatexEN.append(generatedResult.get("latexEN") + "\\bigskip \n\n");
							sbPlainText.append(generatedResult.get("plainText") + "\n\n");
							sbIS.append(generatedResult.get("IS"));
							sbLatex.append(generatedResult.get("latex") + "\\bigskip \n\n");
							sbPlainTextEN.append(generatedResult.get("plainTextEN") + "\n\n");
							sbISEN.append(generatedResult.get("ISEN"));
						}
						catch (Exception e)
						{
							System.err.println("some exc in t3gen");
						}

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					// if (k != exercises - 1)
					// {
					// sbIS.append("--\n");
					// sbISEN.append("--\n");
					// }
					//
					// k++;
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
		}
		// RE -> EFA
		else if (operation == 1)
		{

			for (int i = 0; i < exercises; i++)
			{
				RegularExpressionNode regularExpression = null;
				regularExpression = callFunctionGenerateRegex();
				if (regularExpression == null)
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
					// changes the names of the states

					try
					{
						// Automaton and the row with text "Result"

						RegularExpressionNode re = regularExpression;
						String rePlain = re.toPlainTextWithEpsConcat();
						sbPlainText.append(rePlain + "\n\nŘešení:\n\n");
						sbPlainTextEN.append(rePlain + "\n\nSolution:\n\n");
						String reLatex = "\\begin{center} $" + re.toLatexWithEpsConcat() + "$ \\end{center}";
						sbLatex.append(reLatex + "\\noindent \\textbf{Řešení. }");
						sbLatexEN.append(reLatex + "\\noindent \\textbf{Solution. }");
						sbLatexEN.append("\\noindent\nThe NFA (may contain $\\varepsilon$-transitions):\n\n");
						sbPlainTextEN.append("The NFA (may contain epsilon-transitions):\n");
						sbLatex.append("\\noindent\nNFA (může obsahovat $\\varepsilon$-kroky):\n\n");
						sbPlainText.append("NFA (může obsahovat epsilon-kroky):\n");
						sbIS.append(re.toISText());
						sbISEN.append(re.toISText());

						// result

						Map<String, String> generatedResult = generateResults(regularExpression);

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

		}
		// the end of some kinds of exercises is set
		sbLatex.append("\n\\end{document}\n");
		sbLatexEN.append("\n\\end{document}\n");
		// ready exercises are put into the map result
		result.put("plainText", sbPlainText.toString());
		result.put("latex", sbLatex.toString());
		result.put("IS", sbIS.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("ISEN", sbISEN.toString());

	}

	private Map<String, String> generateResults(RegularExpressionNode regularExpression)
	{
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();

		sbIS.append("\n");
		// DFA -> regular expression

		Automaton b = RegularExpressionConverter.convertRegularExpressionToAutomaton(regularExpression);
		AutomatonModificator.modifyStates(b, 5);
		sbLatexEN.append(b.toLaTeX() + "\n");
		sbPlainTextEN.append(b.toString() + "\n");
		sbLatex.append(b.toLaTeX() + "\n");
		sbPlainText.append(b.toString() + "\n");
		if (b.containsEpsilonTransitions())
		{
			sbIS.append(b.toIS("EFA", "EFA", isomorphism, isoPercent));
		}
		else
		{
			sbIS.append(b.toIS("NFA", "NFA", isomorphism, isoPercent));
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

	private RegularExpressionNode callFunctionGenerateRegex()
	{
		RegularExpressionNode regularExpression = null;
		
		try
		{
			regularExpression = RegularExpressionGenerator.generateRegularExpression(sizeOfAlphabetMin,
				sizeOfAlphabetMax, unionMin, unionMax, concatMin, concatMax, iterMin, iterMax, emptySetMin,
				emptySetMax, epsMin, epsMax, alphabet);

			if (regularExpression == null)
			{
				return regularExpression;
			}
		}
		catch (Exception ex)
		{
			javax.swing.JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Warning",
				JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		{
			return null;
		}
		return regularExpression;
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
					numberOfTransitionsMin, numberOfTransitionsMax, numberOfUnreachableStatesMin,
					numberOfUnreachableStatesMax, true);

				if (a == null)
				{
					return a;
				}
			}
			catch (Exception ex)
			{
				javax.swing.JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}
		while (!((a.getStates().size() >= numberOfStatesMin + numberOfUnreachableStatesMin)
			&& (a.getStates().size() <= numberOfStatesMax + numberOfUnreachableStatesMax)
			&& (a.getNumberOfTransitions() >= numberOfTransitionsMin)
			&& (a.getNumberOfTransitions() <= numberOfTransitionsMax) && (a.getAlphabet().size() >= sizeOfAlphabetMin) && (a
			.getAlphabet().size() <= sizeOfAlphabetMax)) && (!this.isCancelled()));
		if (this.isCancelled())
		{
			return null;
		}
		return a;
	}

	private Map<String, String> generateResultsFromRE(RegularExpressionNode re)
	{
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();

		sbIS.append("\n");
		// DFA -> regular expression

		String plainText = re.toPlainText();
		String latexText = re.toLatexText();
		sbPlainText.append(plainText + "\n");
		sbLatex.append("\\begin{center} $" + latexText + "$ \\end{center}" + "\n");
		sbPlainTextEN.append(plainText + "\n");
		sbLatexEN.append("\\begin{center} $" + latexText + "$ \\end{center} \n");
		sbIS.append(re.toIS("REG") + "\n");

		Map<String, String> result = new HashMap<String, String>();
		result.put("latex", sbLatex.toString());
		result.put("plainText", sbPlainText.toString());
		result.put("IS", sbIS.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("ISEN", sbIS.toString());
		return result;
	}

	private boolean isSuitable(Automaton automaton, RegularExpressionNode regex)
	{
		RegularExpressionStatistics stats = regex.getStatistics();
		if (stats.isHasEps() == hasEps && stats.getConcatenationCount() <= concatMax
			&& stats.getConcatenationCount() >= concatMin && stats.getIterationCount() <= iterMax
			&& stats.getIterationCount() >= iterMin && stats.getUnionCount() <= unionMax
			&& stats.getUnionCount() >= unionMin)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private Map<String, String> setTask()
	{
		Map<String, String> result = new HashMap<String, String>();
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		StringBuilder sbISEN = new StringBuilder();
		String czech = "Najděte jazykově ekvivalentní ";
		String english = "Find a language equivalent ";
		String english2 = "";
		String czech2 = "";
		switch (operation)
		{
			case 0:
				czech2 = "regulární výraz pro deterministický konečný automat, který je zadaný následující tabulkou:";
				english2 = "regular expression to the deterministic finite automaton given with the table:";
				break;
			case 1:
				czech2 = "NFA (může obsahovat epsilon-kroky) k zadanému regulárnímu výrazu:";
				english2 = "NFA (may contain epsilon-transitions) to the given regular expression:";
				break;
		}
		sbPlainText.append(czech + czech2 + "\n\n");
		sbLatex.append(czech + czech2.replace("epsilon", "$\\varepsilon$") + "\n\n");
		sbIS.append("<p>" + czech + czech2.replace("epsilon", "<M>\\varepsilon</M>") + "</p>\n\n");
		sbPlainTextEN.append(english + english2 + "\n\n");
		sbLatexEN.append(english + english2.replace("epsilon", "$\\varepsilon$") + "\n\n");
		sbISEN.append("<p>" + english + english2.replace("epsilon", "<M>\\varepsilon</M>") + "</p>\n\n");
		result.put("latex", sbLatex.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainText", sbPlainText.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("IS", sbIS.toString());
		result.put("ISEN", sbISEN.toString());
		return result;
	}

}
