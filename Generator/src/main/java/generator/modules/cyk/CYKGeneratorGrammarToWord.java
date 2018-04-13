/*
 * The class makes exercises for synchronous parallel composition
 */

package generator.modules.cyk;

import generator.common.GeneratingLogic;
import generator.common.GeneratorWorker;
import generator.communication.dto.ExampleDTO;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class T0Generator stands for the background thread, which generates exercises for synchronous parallel
 * composition
 * 
 * @author Jana Kadlecova, Jiri Uhlir
 */
public class CYKGeneratorGrammarToWord extends GeneratorWorker implements GeneratingLogic
{

	int minTerminals = 1;
	int maxTerminals = 5;
	int minNonterminals = 1;
	int maxNonterminals = 6;
	int minWordLength = 4;
	int maxWordLength = 10;
	int minEmptySets = 0;
	int maxEmptySets = 100;
	int minDifferentSets = 0;
	int maxDifferentSets = 100;
	private int exercises;
	int selectedAlphabet;
	private Grammar grammar;
	List<String> alphabet = new LinkedList<String>();
	List<String> words;

	// result attributes, -1 if are not set
	public CYKGeneratorGrammarToWord(int numberOfExamplesPerThread, int minTerminals, int maxTerminals,
		int minNonterminals, int maxNonterminals, int minWordLength, int maxWordLength, int minEmptySets,
		int maxEmptySets, int minDifferentSets, int maxDifferentSets, int selectedAlphabet, List<String> words,
		Grammar grammar)
	{
		this.exercises = numberOfExamplesPerThread;
		this.minDifferentSets = minDifferentSets;
		this.maxDifferentSets = maxDifferentSets;
		this.minEmptySets = minEmptySets;
		this.maxEmptySets = maxEmptySets;
		this.minTerminals = minTerminals;
		this.maxTerminals = maxTerminals;
		this.minNonterminals = minNonterminals;
		this.maxNonterminals = maxNonterminals;
		this.minWordLength = minWordLength;
		this.maxWordLength = maxWordLength;
		this.selectedAlphabet = selectedAlphabet;

		this.words = words;
		this.grammar = grammar;
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
		Set<Integer> usedIndexes = new HashSet<Integer>();
		for (int i = 0; i < exercises; i++)
		{
			String[] wordStringChars = null;
			CYKTable cykTable = null;
			String word = null;

			int k = -1;
			do
			{
				k++;
				if (usedIndexes.contains(k))
					continue;

				if (k < words.size())
				{
					word = words.get(k);
				}
				else
				{
					break;
				}

				char[] wordChars = word.toCharArray();
				wordStringChars = new String[wordChars.length];
				int wordLength = wordChars.length;

				for (int j = 0; j < wordLength; j++)
				{
					wordStringChars[j] = String.valueOf(wordChars[j]);
				}

				// generates automaton with the given parameters

				cykTable = CYKAlgorithm.executeAlgorithm(word, grammar);

			}
			while (isSuitable(cykTable) || k >= words.size());
			usedIndexes.add(k);

			if (i >= words.size())
			{
				// no more words to process
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
				// divide exercises

				// sets the task
				sbLatex.append(task.get("latex"));
				sbPlainText.append(task.get("plainText"));
				sbIS.append(task.get("IS"));
				sbLatexEN.append(task.get("latexEN"));
				sbPlainTextEN.append(task.get("plainTextEN"));
				sbISEN.append(task.get("ISEN"));
				// changes the names of the states
				// AutomatonModificator.modifyStates(a1, a1States);
				// AutomatonModificator.modifyStates(a2, a2States);
				// a1.setName("A");
				// a2.setName("B");
				// Automaton and the row with text "Result"
				sbPlainText.append(grammar);
				sbPlainText.append(word);
				sbPlainText.append(CYKAlgorithm.printCYKTablePlain(cykTable.getCykTable(), wordStringChars, grammar));
				// sbLatex.append("\\noindent\n" + czech + a1.getName() + ":\n\n" + a1.toLaTeX() + "\n");
				// sbLatex.append("\\noindent\n" + czech + a2.getName() + ":\n\n" + a2.toLaTeX()
				// + "\n\n\\subsection*{Řešení}\n");
				// sbLatexEN.append("\\noindent\n" + english + a1.getName() + ":\n\n" + a1.toLaTeX() + "\n");
				// sbLatexEN.append("\\noindent\n" + english + a2.getName() + ":\n\n" + a2.toLaTeX()
				// + "\n\n\\subsection*{Solution}\n");
				// sbPlainText.append(czech + a1.toString().replace("transition function", "přechodová funkce") + "\n"
				// + czech + a2.toString().replace("transition function", "přechodová funkce") + "\n\nŘešení:\n\n");
				// sbPlainTextEN.append(english + a1.toString() + "\n" + english + a2.toString() + "\n\nSolution:\n\n");
				// sbIS.append(czech + a1.getName() + ":\n\n<M>" + a1.toLaTeX().replace("$", "") + "</M>\n\n" + czech
				// + a2.getName() + ":\n\n<M>" + a2.toLaTeX().replace("$", "") + "</M>\n\n");
				// sbISEN.append(english + a1.getName() + ":\n\n<M>" + a1.toLaTeX().replace("$", "") + "</M>\n\n"
				// + english + a2.getName() + ":\n\n<M>" + a2.toLaTeX().replace("$", "") + "</M>\n\n");
				// // result
				// Map<String, String> generatedResult = generateResults(a1, a2);
				// sbLatexEN.append(generatedResult.get("latexEN") + "\n\n");
				// sbPlainText.append(generatedResult.get("plainText") + "\n\n");
				// sbIS.append(generatedResult.get("IS"));
				// sbLatex.append(generatedResult.get("latex") + "\n\n");
				// sbPlainTextEN.append(generatedResult.get("plainTextEN") + "\n\n");
				// sbISEN.append(generatedResult.get("ISEN"));
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
					moduleToCoreCommunication.updateProgress(i, exercises);
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

	private boolean isSuitable(CYKTable table)
	{
		if (table.getDifferentSets() >= minDifferentSets && table.getDifferentSets() <= maxDifferentSets
			&& table.getEmptySets() >= minEmptySets && table.getEmptySets() <= maxEmptySets)
			return true;
		return false;
	}

	private Map<String, String> setTask()
	{
		Map<String, String> task = new HashMap<String, String>();
		StringBuilder sbLatex = new StringBuilder();
		StringBuilder sbLatexEN = new StringBuilder();
		StringBuilder sbPlainText = new StringBuilder();
		StringBuilder sbPlainTextEN = new StringBuilder();

		String czech = "Proveďte deterministickou syntaktickou analýzu pro zadanou gramatiku G a slovo w.";
		String english = "Do deterministic syntactic analysis for given grammar G and word w.";
		sbLatexEN.append("\n\\noindent\n" + english);
		sbLatex.append("\n\\noindent\n" + czech);
		sbPlainText.append(czech);
		sbPlainTextEN.append(english);

		task.put("latex", sbLatex.toString());
		task.put("latexEN", sbLatexEN.toString());
		task.put("plainText", sbPlainText.toString());
		task.put("plainTextEN", sbPlainTextEN.toString());

		return task;
	}

}
