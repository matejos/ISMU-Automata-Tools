/*
 * The class makes exercises for synchronous parallel composition
 */

package generator.modules.reglang;

import generator.common.GeneratingLogic;
import generator.common.GeneratorWorker;
import generator.communication.dto.ExampleDTO;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.reglang.automaton.Automaton;
import generator.modules.reglang.automaton.AutomatonComposition;
import generator.modules.reglang.automaton.AutomatonModificator;
import generator.modules.reglang.automaton.FiniteAutomataGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The class T0Generator stands for the background thread, which generates exercises for synchronous parallel
 * composition
 * 
 * @author Jana Kadlecova, Jiri Uhlir
 */
public class T0Generator extends GeneratorWorker implements GeneratingLogic
{

	private int alphabet;
	private int sizeOfAlphabetMin;
	private int sizeOfAlphabetMax;
	private int a1NumberOfStatesMin;
	private int a1NumberOfStatesMax;
	private int a1NumberOfFinalStatesMin;
	private int a1NumberOfFinalStatesMax;
	private int a1NumberOfTransitionsMin;
	private int a1NumberOfTransitionsMax;
	private int a1States;
	private boolean a1TotalTransitionFunction;
	private boolean a1Complement;
	private int a2NumberOfStatesMin;
	private int a2NumberOfStatesMax;
	private int a2NumberOfFinalStatesMin;
	private int a2NumberOfFinalStatesMax;
	private int a2NumberOfTransitionsMin;
	private int a2NumberOfTransitionsMax;
	private int a2States;
	private boolean a2TotalTransitionFunction;
	private boolean a2Complement;
	// result attributes, -1 if are not set
	private int resultStatesMin;
	private int resultStatesMax;
	private int resultTransitionsMin;
	private int resultTransitionsMax;
	private boolean resultMinOneFinalState;

	private boolean isomorphism;

	private int exercises;
	private int operation;

	/**
	 * Constructor, which makes new backgroud thread. The thread generates the exercises for synchronous parallel
	 * composition
	 * 
	 * @param exercises
	 *            number of exercises
	 * @param operation
	 *            0 -- union, 1 -- intersection, 2 -- difference
	 * @param alphabet
	 *            type of the alphabet: 0 -- a, b, c (lower case); 1 -- A, B, C (upper case); 2 -- 1, 2, 3 (numbers); 3
	 *            -- x, y, z (lower case end of rhe alphabet); 4 -- X, Y, Z (upper case end of the alphabet); 5 -- t0,
	 *            t1, t2 (letter 't' with index); 6 -- p0, p1, p2 (letter 'p' with index); 7 -- I, II, III, IV (roman
	 *            numbers); 8 -- i, ii, iii, iv (roman numbers lower case); 9 -- 1, 10, 11, 100 (binary numbers)
	 * @param sizeOfAlphabetMin
	 *            size of alphabet min
	 * @param sizeOfAlphabetMax
	 *            size of alphabet max
	 * @param a1NumberOfStatesMin
	 *            automaton 1: number of states min
	 * @param a1NumberOfStatesMax
	 *            automaton 1: number of states max
	 * @param a1NumberOfFinalStatesMin
	 *            automaton 1: number of final states min
	 * @param a1NumberOfFinalStatesMax
	 *            automaton 1: number of final states max
	 * @param a1NumberOfTransitionsMin
	 *            automaton 1: number of transitions min
	 * @param a1NumberOfTransitionsMax
	 *            automaton 1: number of transitions max
	 * @param a1States
	 *            type of states : 0 -- a,b,c (lower case); 1 -- A,B,C (upper case); 2 -- 1,2,3 (numbers); 3 -- x,y,z
	 *            (lower case end of rhe alphabet); 4 -- X,Y,Z (upper case end of the alphabet); 5 -- q0, q1, q2 (letter
	 *            'q' with index); 6 -- s0, s1, s2 (letter 's' with index)
	 * @param a1TotalTransitionFunction
	 *            if true, the first automaton has total transition function
	 * @param a1Complement
	 *            the complement of the first automaton is required
	 * @param a2NumberOfStatesMin
	 *            automaton 2: number of states min
	 * @param a2NumberOfStatesMax
	 *            automaton 2: number of states max
	 * @param a2NumberOfFinalStatesMin
	 *            automaton 2: number of final states min
	 * @param a2NumberOfFinalStatesMax
	 *            automaton 2: number of final states max
	 * @param a2NumberOfTransitionsMin
	 *            automaton 2: number of transitions min
	 * @param a2NumberOfTransitionsMax
	 *            automaton 2: number of transitions min
	 * @param a2States
	 *            type of states : 0 -- a,b,c (lower case); 1 -- A,B,C (upper case); 2 -- 1,2,3 (numbers); 3 -- x,y,z
	 *            (lower case end of rhe alphabet); 4 -- X,Y,Z (upper case end of the alphabet); 5 -- q0, q1, q2 (letter
	 *            'q' with index); 6 -- s0, s1, s2 (letter 's' with index);
	 * @param a2TotalTransitionFunction
	 *            if true, the second automaton has total transition function
	 * @param a2Complement
	 *            the complement of the second automaton is required
	 * @param outputStatesMin
	 *            resulting automaton: number of states min
	 * @param outputStatesMax
	 *            resulting automaton: number of states max
	 * @param outputTransitionsMin
	 *            resulting automaton: number of transitions min
	 * @param outputTransitionsMax
	 *            resulting automaton: number of transitions max
	 * @param outputMinOneFinalState
	 *            the resulting automaton must habe minimal one final state
	 */
	public T0Generator(int exercises, int operation, int alphabet, int sizeOfAlphabetMin, int sizeOfAlphabetMax,
		int a1NumberOfStatesMin, int a1NumberOfStatesMax, int a1NumberOfFinalStatesMin, int a1NumberOfFinalStatesMax,
		int a1NumberOfTransitionsMin, int a1NumberOfTransitionsMax, int a1States, boolean a1TotalTransitionFunction,
		boolean a1Complement, int a2NumberOfStatesMin, int a2NumberOfStatesMax, int a2NumberOfFinalStatesMin,
		int a2NumberOfFinalStatesMax, int a2NumberOfTransitionsMin, int a2NumberOfTransitionsMax, int a2States,
		boolean a2TotalTransitionFunction, boolean a2Complement, int outputStatesMin, int outputStatesMax,
		int outputTransitionsMin, int outputTransitionsMax, boolean outputMinOneFinalState, boolean isomorphism)
	{

		// initializing atributes for generation the first automaton
		this.a1NumberOfStatesMin = a1NumberOfStatesMin;
		this.a1NumberOfStatesMax = a1NumberOfStatesMax;
		this.a1NumberOfFinalStatesMin = a1NumberOfFinalStatesMin;
		this.a1NumberOfFinalStatesMax = a1NumberOfFinalStatesMax;
		this.a1NumberOfTransitionsMin = a1NumberOfTransitionsMin;
		this.a1NumberOfTransitionsMax = a1NumberOfTransitionsMax;
		this.a1States = a1States;
		this.a1TotalTransitionFunction = a1TotalTransitionFunction;
		this.a1Complement = a1Complement;
		// initializing atributes for generating the second automaton
		this.a2NumberOfStatesMin = a2NumberOfStatesMin;
		this.a2NumberOfStatesMax = a2NumberOfStatesMax;
		this.a2NumberOfFinalStatesMin = a2NumberOfFinalStatesMin;
		this.a2NumberOfFinalStatesMax = a2NumberOfFinalStatesMax;
		this.a2NumberOfTransitionsMin = a2NumberOfTransitionsMin;
		this.a2NumberOfTransitionsMax = a2NumberOfTransitionsMax;
		this.a2States = a2States;
		this.a2TotalTransitionFunction = a2TotalTransitionFunction;
		this.a2Complement = a2Complement;
		// other atributes
		this.operation = operation;
		this.alphabet = alphabet;
		this.sizeOfAlphabetMin = sizeOfAlphabetMin;
		this.sizeOfAlphabetMax = sizeOfAlphabetMax;

		this.exercises = exercises;
		// this.readyExercises = 0;

		// output
		this.resultStatesMin = outputStatesMin;
		this.resultStatesMax = outputStatesMax;
		this.resultTransitionsMin = outputTransitionsMin;
		this.resultTransitionsMax = outputTransitionsMax;
		this.resultMinOneFinalState = outputMinOneFinalState;

		this.isomorphism = isomorphism;
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
		// the text with setting of examples
		Map<String, String> task = setTask();
		for (int i = 0; i < exercises; i++)
		{
			Automaton a1 = null;
			Automaton a2 = null;
			// generates automaton with the given parameters
			do
			{
				a1 = callFunctionGenerateAutomaton(a1NumberOfStatesMin, a1NumberOfStatesMax, a1NumberOfFinalStatesMin,
					a1NumberOfFinalStatesMax, a1NumberOfTransitionsMin, a1NumberOfTransitionsMax, sizeOfAlphabetMin,
					sizeOfAlphabetMax, a1TotalTransitionFunction);
				a2 = callFunctionGenerateAutomaton(a2NumberOfStatesMin, a2NumberOfStatesMax, a2NumberOfFinalStatesMin,
					a2NumberOfFinalStatesMax, a2NumberOfTransitionsMin, a2NumberOfTransitionsMax, a1.getAlphabet()
						.size(), a1.getAlphabet().size(), a2TotalTransitionFunction);

				AutomatonModificator.modifyAlphabet(a1, alphabet);
				AutomatonModificator.modifyAlphabet(a2, alphabet);

			}
			while (!isSuitable(a1, a2) && FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE);
			if (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
			{
				sbLatex = new StringBuilder();
				sbPlainText = new StringBuilder();
				sbIS = new StringBuilder();
				sbLatexEN = new StringBuilder();
				sbPlainTextEN = new StringBuilder();
				sbISEN = new StringBuilder();
				// divide exercises

				// sets the task
				sbLatex.append(task.get("latex"));
				sbPlainText.append(task.get("plainText"));
				sbIS.append(task.get("IS"));
				sbLatexEN.append(task.get("latexEN"));
				sbPlainTextEN.append(task.get("plainTextEN"));
				sbISEN.append(task.get("ISEN"));
				// changes the names of the states
				AutomatonModificator.modifyStates(a1, a1States);
				AutomatonModificator.modifyStates(a2, a2States);
				a1.setName("A");
				a2.setName("B");
				// Automaton and the row with text "Result"
				String english = "The automaton ";
				String czech = "Automat ";
				sbLatex.append("\\noindent\n" + czech + a1.getName() + ":\n\n" + a1.toLaTeX() + "\n");
				sbLatex.append("\\noindent\n" + czech + a2.getName() + ":\n\n" + a2.toLaTeX()
					+ "\\noindent \\textbf{Řešení. }\n");
				sbLatexEN.append("\\noindent\n" + english + a1.getName() + ":\n\n" + a1.toLaTeX() + "\n");
				sbLatexEN.append("\\noindent\n" + english + a2.getName() + ":\n\n" + a2.toLaTeX()
					+ "\\noindent \\textbf{Solution. }\n");
				sbPlainText.append(czech + a1.getName() + ":\n" + a1.toString() + "\n" + czech + a2.getName() + ":\n"
					+ a2.toString() + "\n\nŘešení:\n\n");
				sbPlainTextEN.append(english + a1.getName() + ":\n" + a1.toString() + "\n" + english + a2.getName()
					+ ":\n" + a2.toString() + "\n\nSolution:\n\n");
				sbIS.append(czech + a1.getName() + ":\n\n<M>" + a1.toLaTeX().replace("$", "") + "</M>\n\n" + czech
					+ a2.getName() + ":\n\n<M>" + a2.toLaTeX().replace("$", "") + "</M>\n\n");
				sbISEN.append(english + a1.getName() + ":\n\n<M>" + a1.toLaTeX().replace("$", "") + "</M>\n\n"
					+ english + a2.getName() + ":\n\n<M>" + a2.toLaTeX().replace("$", "") + "</M>\n\n");
				// result
				Map<String, String> generatedResult = generateResults(a1, a2);
				sbLatexEN.append(generatedResult.get("latexEN") + "\\bigskip \n\n");
				sbPlainText.append(generatedResult.get("plainText") + "\n\n");
				sbIS.append(generatedResult.get("IS"));
				sbLatex.append(generatedResult.get("latex") + "\\bigskip \n\n");
				sbPlainTextEN.append(generatedResult.get("plainTextEN") + "\n\n");
				sbISEN.append(generatedResult.get("ISEN"));
				// if (i != exercises - 1)
				// {
				// sbIS.append("--\n");
				// sbISEN.append("--\n");
				// }
				// ++;
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
		result.put("plainText", sbPlainText.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("IS", sbIS.toString());
		result.put("ISEN", sbISEN.toString());
	}

	private Automaton callFunctionGenerateAutomaton(int numberOfStatesMin, int numberOfStatesMax,
		int numberOfFinalStatesMin, int numberOfFinalStatesMax, int numberOfTransitionsMin, int numberOfTransitionsMax,
		int sizeOfAlphabetMin, int sizeOfAlphabetMax, boolean totalTransitionFunction)
	{

		Automaton a = null;
		do
		{
			try
			{
				a = FiniteAutomataGenerator.generateFA(sizeOfAlphabetMin, sizeOfAlphabetMax, numberOfStatesMin,
					numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax, totalTransitionFunction, 0, 0,
					numberOfTransitionsMin, numberOfTransitionsMax, 0, 0, true);
				// a = AutomatonGenerator.generateAutomaton(numberOfStatesMin, numberOfStatesMax,
				// numberOfFinalStatesMin,
				// numberOfFinalStatesMax, numberOfTransitionsMin, numberOfTransitionsMax, sizeOfAlphabetMin,
				// sizeOfAlphabetMax, 0, 0, true, false, totalTransitionFunction);
				if (a == null)
				{
					return null;
				}
			}
			catch (Exception e)
			{
				javax.swing.JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
				return null;
			}
		}

		while (!((a.getStates().size() >= numberOfStatesMin) && (a.getStates().size() <= numberOfStatesMax)
			&& (totalTransitionFunction || a.getNumberOfTransitions() >= numberOfTransitionsMin)
			&& (totalTransitionFunction || a.getNumberOfTransitions() <= numberOfTransitionsMax)
			&& (a.getAlphabet().size() >= sizeOfAlphabetMin) && (a.getAlphabet().size() <= sizeOfAlphabetMax))
			&& (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE));
		if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		{
			return null;
		}
		return a;
	}

	private Map<String, String> generateResults(Automaton a1, Automaton a2)
	{
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		Automaton a = null;
		a1.setName("A");
		a2.setName("B");
		String s1 = (a1Complement ? "co-L(" + a1.getName() + ")" : "L(" + a1.getName() + ")");
		String s2 = (a2Complement ? "co-L(" + a2.getName() + ")" : "L(" + a2.getName() + ")");
		String s1L = (a1Complement ? "\\mathit{co}$-$L(" + a1.getName() + ")" : "L(" + a1.getName() + ")");
		String s2L = (a2Complement ? "\\mathit{co}$-$L(" + a2.getName() + ")" : "L(" + a2.getName() + ")");
		if (!a1.isTransitionFunctionTotal())
		{
			a1.makeTransitionFunctionTotal();
			String czech = "Automat " + a1.getName() + " s totální přechodovou funkcí:";
			String english = "The automaton " + a1.getName() + " with the total transition function:";
			sbLatex.append("\\noindent\n" + czech + "\n\n" + a1.toLaTeX() + "\n");
			sbLatexEN.append("\\noindent\n" + english + "\n\n" + a1.toLaTeX() + "\n");
			sbPlainTextEN.append(english + "\n" + a1.toString() + "\n");
			sbPlainText.append(czech + "\n" + a1.toString().replace("transition function", "přechodová funkce") + "\n");
		}
		if (!a2.isTransitionFunctionTotal())
		{
			a2.makeTransitionFunctionTotal();
			String czech = "Automat " + a2.getName() + " s totální přechodovou funkcí:";
			String english = "The automaton " + a2.getName() + " with the total transition function:";
			sbLatex.append("\\noindent\n" + czech + "\n\n" + a2.toLaTeX() + "\n");
			sbLatexEN.append("\\noindent\n" + english + "\n\n" + a2.toLaTeX() + "\n");
			sbPlainTextEN.append(english + "\n" + a2.toString() + "\n");
			sbPlainText.append(czech + "\n" + a2.toString().replace("transition function", "přechodová funkce") + "\n");
		}
		if (a1Complement)
		{
			a1.setName(a1.getName() + "'");
			a1 = AutomatonComposition.complement(a1);
			String czech = "Automat akceptující jazyk ";
			String english = "The automaton accepting the language ";
			sbLatex.append("\\noindent\n" + czech + "$" + s1L + "$:" + "\n\n" + a1.toLaTeX() + "\n");
			sbLatexEN.append("\\noindent\n" + english + "$" + s1L + "$:" + "\n\n" + a1.toLaTeX() + "\n");
			sbPlainTextEN.append(english + s1 + ":" + "\n" + a1.toString() + "\n");
			sbPlainText.append(czech + s1 + ":" + "\n"
				+ a1.toString().replace("transition function", "přechodová funkce") + "\n");
		}
		if (a2Complement)
		{
			a2.setName(a2.getName() + "'");
			a2 = AutomatonComposition.complement(a2);
			String czech = "Automat akceptující jazyk ";
			String english = "The automaton accepting the language ";
			sbLatex.append("\\noindent\n" + czech + "$" + s2L + "$:" + "\n\n" + a2.toLaTeX() + "\n");
			sbLatexEN.append("\\noindent\n" + english + "$" + s2L + "$:" + "\n\n" + a2.toLaTeX() + "\n");
			sbPlainTextEN.append(english + s2 + ":" + "\n" + a2.toString() + "\n");
			sbPlainText.append(czech + s2 + ":" + "\n"
				+ a2.toString().replace("transition function", "přechodová funkce") + "\n");
		}
		String english = "The automaton M accepting the language ";
		String czech = "Automat M rozpoznávající jazyk ";
		sbLatexEN.append("\\noindent\n" + english);
		sbPlainText.append(czech);
		sbLatex.append("\\noindent\n" + czech);
		sbPlainTextEN.append(english);
		if (operation == 0)
		{ // union
			a = AutomatonComposition.union(a1, a2);
			sbLatexEN.append("$" + s1L + " \\cup " + s2L + "$:\n\n");
			sbPlainText.append(s1 + " sjednoceno " + s2 + ":\n");
			sbLatex.append("$" + s1L + " \\cup " + s2L + "$:\n\n");
			sbPlainTextEN.append(s1 + " union " + s2 + ":\n");
		}
		if (operation == 1)
		{ // intersection
			a = AutomatonComposition.intersection(a1, a2);
			sbLatexEN.append("$" + s1L + " \\cap " + s2L + "$:\n\n");
			sbPlainText.append(s1 + " průnik " + s2 + ":\n");
			sbLatex.append("$" + s1L + " \\cap " + s2L + "$:\n\n");
			sbPlainTextEN.append(s1 + " intersection " + s2 + ":\n");
		}
		if (operation == 2)
		{
			a = AutomatonComposition.difference(a1, a2);
			sbLatexEN.append("$" + s1L + " - " + s2L + "$:\n\n");
			sbPlainText.append(s1 + " - " + s2 + ":\n");
			sbLatex.append("$" + s1L + " - " + s2L + "$:\n\n");
			sbPlainTextEN.append(s1 + " - " + s2 + ":\n");
		}
		sbLatexEN.append(a.toLaTeX() + "\n");
		sbPlainTextEN.append(a.toString() + "\n");
		sbLatex.append(a.toLaTeX() + "\n");
		sbPlainText.append(a.toString().replace("transition function", "přechodová funkce") + "\n");
		AutomatonModificator.modifyStates(a, a.getStatesType());
		sbIS.append(a.toIS("DFA", "DFA", isomorphism));
		Map<String, String> result = new HashMap<String, String>();
		result.put("latex", sbLatex.toString());
		result.put("plainText", sbPlainText.toString());
		result.put("IS", sbIS.toString());
		result.put("latexEN", sbLatexEN.toString());
		result.put("plainTextEN", sbPlainTextEN.toString());
		result.put("ISEN", sbIS.toString());

		return result;
	}

	private boolean isSuitable(Automaton automaton1, Automaton automaton2)
	{
		Automaton a;
		Automaton a1 = new Automaton(automaton1);
		Automaton a2 = new Automaton(automaton2);
		if (a1.isTransitionFunctionTotal() && !a1TotalTransitionFunction)
		{
			return false;
		}
		if (a2.isTransitionFunctionTotal() && !a2TotalTransitionFunction)
		{
			return false;
		}
		a1.makeTransitionFunctionTotal();
		a2.makeTransitionFunctionTotal();
		if (a1Complement)
		{
			a1 = AutomatonComposition.complement(a1);
		}
		if (a2Complement)
		{
			a2 = AutomatonComposition.complement(a2);
		}
		if (!a1.getAlphabet().equals(a2.getAlphabet()))
		{
			return false;
		}
		switch (operation)
		{
			case 0:
				a = AutomatonComposition.union(a1, a2);
				break;
			case 1:
				a = AutomatonComposition.intersection(a1, a2);
				break;
			default:
				a = AutomatonComposition.difference(a1, a2);
		}
		int statesCount = a.getStates().size();
		if (resultStatesMax > -1)
		{
			if (statesCount < resultStatesMin || statesCount > resultStatesMax)
			{
				return false;
			}
		}
		int transitionsCount = a.getNumberOfTransitions();
		if (resultTransitionsMax > -1)
		{
			if (transitionsCount < resultTransitionsMin || transitionsCount > resultTransitionsMax)
			{
				return false;
			}
		}
		if (resultMinOneFinalState)
		{
			if (a.getFinalStates().size() < 1)
			{
				return false;
			}
		}
		return true;
	}

	private Map<String, String> setTask()
	{
		Map<String, String> task = new HashMap<String, String>();
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();
		StringBuilder sbIS = new StringBuilder();
		StringBuilder sbISEN = new StringBuilder();
		String czech = "Najděte deterministický automat bez nedosažitelných " + "stavů s totální přechodovou funkcí, "
			+ System.getProperty("line.separator") + "který akceptuje jazyk ";
		String english = "Find a deterministic automaton without unreachable "
			+ "states and with the total transition function, which accepts " + "the lanquaqe ";
		sbIS.append("<p>" + czech);
		sbISEN.append("<p>" + english);
		sbLatexEN.append(english);
		sbLatex.append(czech);
		sbPlainText.append(czech);
		sbPlainTextEN.append(english);

		String s1 = (a1Complement ? "co-L(A)" : "L(A)");
		String s1L = (a1Complement ? "\\mathit{co}$-$L(A)" : "L(A)");
		String s2 = (a2Complement ? "co-L(B)" : "L(B)");
		String s2L = (a2Complement ? "\\mathit{co}$-$L(B)" : "L(B)");
		String s1I = (a1Complement ? "\\mathit{co}</M>-<M>L(A)" : "L(A)");
		String s2I = (a2Complement ? "\\mathit{co}</M>-<M>L(B)" : "L(B)");
		sbLatexEN.append("$" + s1L);
		sbLatex.append("$" + s1L);
		sbPlainText.append(s1);
		sbPlainTextEN.append(s1);
		sbIS.append("<M>" + s1I);
		sbISEN.append("<M>" + s1I);
		switch (operation)
		{
			case 0:
				sbLatex.append(" \\cup ");
				sbLatexEN.append(" \\cup ");
				sbPlainText.append(" sjednoceno ");
				sbPlainTextEN.append(" union ");
				sbIS.append(" \\cup ");
				sbISEN.append(" \\cup ");
				break;
			case 1:
				sbLatex.append(" \\cap ");
				sbLatexEN.append(" \\cap ");
				sbPlainText.append(" průnik ");
				sbPlainTextEN.append(" intersection ");
				sbIS.append(" \\cap ");
				sbISEN.append(" \\cap ");
				break;
			case 2:
				sbLatex.append(" - ");
				sbLatexEN.append("-");
				sbPlainText.append(" - ");
				sbPlainTextEN.append(" - ");
				sbIS.append(" - ");
				sbISEN.append(" - ");
				break;
		}
		sbLatex.append(s2L + "$ .\n\n");
		sbLatexEN.append(s2L + "$ .\n\n");
		sbPlainText.append(s2 + ".\n\n");
		sbPlainTextEN.append(s2 + ".\n\n");
		sbIS.append(s2I + "</M>.</p>\n");
		sbISEN.append(s2I + "</M>.</p>\n");
		task.put("latex", sbLatex.toString());
		task.put("latexEN", sbLatexEN.toString());
		task.put("plainText", sbPlainText.toString());
		task.put("plainTextEN", sbPlainTextEN.toString());
		task.put("IS", sbIS.toString());
		task.put("ISEN", sbISEN.toString());
		return task;
	}

}
