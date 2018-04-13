/*
 * The class makes exercises for synchronous parallel composition
 */

package generator.modules.cyk;

import generator.common.GeneratingLogic;
import generator.common.GeneratorWorker;
import generator.common.tools.CommonUtils;
import generator.communication.dto.ExampleDTO;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The class T0Generator stands for the background thread, which generates exercises for synchronous parallel
 * composition
 * 
 * @author Jana Kadlecova, Jiri Uhlir
 */
public class CYKGeneralGenerator extends GeneratorWorker implements GeneratingLogic
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
	boolean generatable = true;

	List<String> alphabet = new LinkedList<String>();
	List<String> words;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public CYKGeneralGenerator(int numberOfExamplesPerThread, int minTerminals, int maxTerminals, int minNonterminals,
		int maxNonterminals, int minWordLength, int maxWordLength, int minEmptySets, int maxEmptySets,
		int minDifferentSets, int maxDifferentSets, int selectedAlphabet, boolean generatable)
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
		this.generatable = generatable;

		Random rand = new Random();

		int alphabetCount = CommonUtils.randInt(minTerminals, maxTerminals, rand);

		if (selectedAlphabet == 0)
		{
			for (int i = 0; i < alphabetCount; i++)
			{
				alphabet.add(String.valueOf((char) ('a' + i)));
			}
		}
		if (selectedAlphabet == 1)
		{
			for (int i = 0; i < alphabetCount; i++)
			{
				alphabet.add(String.valueOf((char) ('0' + i)));
			}
		}
	}

	public CYKGeneralGenerator(int numberOfExamplesPerThread, int minTerminals, int maxTerminals, int minNonterminals,
		int maxNonterminals, int minWordLength, int maxWordLength, int minEmptySets, int maxEmptySets,
		int minDifferentSets, int maxDifferentSets, int selectedAlphabet, List<String> words, boolean generatable)
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
		this.generatable = generatable;
		this.words = words;
	}

	public void executeGeneration()
	{
		Random rand = new Random();

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
			String word;
			if (words != null)
			{
				word = words.get(CommonUtils.randInt(1, words.size(), rand) - 1);
			}
			else
			{
				word = generateWord();
			}

			char[] wordChars = word.toCharArray();
			String[] wordStringChars = new String[wordChars.length];
			int wordLength = wordChars.length;

			for (int j = 0; j < wordLength; j++)
			{
				wordStringChars[j] = String.valueOf(wordChars[j]);
			}

			CYKTable cykTable = null;
			Grammar g = null;
			// generates automaton with the given parameters

			do
			{
				g = CYKHelper.generateGrammarInCNFBasedOnParameters(word, minNonterminals, maxNonterminals);
				if (!generatable)
				{
					if (words == null)
					{
						word = switchToRandomWordChars(word);
					}
					else
					{
						g = CYKHelper.removeRandomRuleFromGrammar(g);
					}
				}
				cykTable = CYKAlgorithm.executeAlgorithm(word, g);
			}
			while (!isSuitable(cykTable) && FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE);

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
				
				sbPlainText.append(g.toStringPlainCz() + LINE_SEPARATOR);
				sbPlainTextEN.append(g.toStringPlainEn() + LINE_SEPARATOR);
				sbPlainText.append("w = " + word + LINE_SEPARATOR + LINE_SEPARATOR);
				sbPlainTextEN.append("w = " + word + LINE_SEPARATOR + LINE_SEPARATOR);
				
				
				sbLatex.append(g.toLaTeX().replace("where", "kde") + LINE_SEPARATOR);
				sbLatexEN.append(g.toLaTeX() + LINE_SEPARATOR);
				sbLatex.append("\\begin{center}$w = " + word + "$\\end{center}" + LINE_SEPARATOR);
				sbLatexEN.append("\\begin{center}$w = " + word + "$\\end{center}" + LINE_SEPARATOR);
				sbPlainText.append("Řešení:" + LINE_SEPARATOR);
				sbPlainTextEN.append("Solution:" + LINE_SEPARATOR);
				String canGenerate = generatable ? "$" + word + " \\in L(\\mathcal{G})$" : "$" + word
					+ " \\not\\in L(\\mathcal{G})$";
				sbLatex.append("\\noindent \\textbf{Řešení. } " + canGenerate + " \\\\\\\\");
				sbLatexEN.append("\\noindent \\textbf{Solution. } " + canGenerate + " \\\\\\\\");
				String cykTablePlain = CYKAlgorithm.printCYKTablePlain(cykTable.getCykTable(), wordStringChars, g);
				String cykTableLatex = CYKAlgorithm.printCYKTableLaTeX(cykTable.getCykTable(), wordStringChars, g);
				sbPlainText.append(cykTablePlain);
				sbPlainTextEN.append(cykTablePlain);
				sbLatex.append(cykTableLatex);
				sbLatexEN.append(cykTableLatex);
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

	private String switchToRandomWordChars(String word)
	{
		char[] wordChars = word.toCharArray();
		Random rand = new Random();
		int index1 = CommonUtils.randInt(0, wordChars.length - 1, rand);
		int index2 = 0;
		while (index1 == index2)
		{
			index2 = CommonUtils.randInt(0, wordChars.length - 1, rand);
		}

		char temp = wordChars[index1];
		wordChars[index1] = wordChars[index2];
		wordChars[index2] = temp;

		return new String(wordChars);
	}

	private String generateWord()
	{
		Random rand = new Random();
		int wordLength = CommonUtils.randInt(minWordLength, maxWordLength, rand);
		String[] word = new String[wordLength];

		List<String> unusedChars = new LinkedList<String>(alphabet);
		while (unusedChars.size() > 0)
		{
			int selectedIndex = 0;
			do
			{
				selectedIndex = CommonUtils.randInt(1, wordLength, rand) - 1;
			}
			while (word[selectedIndex] != null);
			int selectedCharIndex = CommonUtils.randInt(1, unusedChars.size(), rand) - 1;
			String selectedChar = unusedChars.get(selectedCharIndex);
			word[selectedIndex] = selectedChar;
			unusedChars.remove(selectedCharIndex);
		}

		for (int i = 0; i < wordLength; i++)
		{
			if (word[i] == null)
			{
				int selectedCharIndex = CommonUtils.randInt(1, alphabet.size(), rand) - 1;
				word[i] = alphabet.get(selectedCharIndex);
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < wordLength; i++)
		{
			sb.append(word[i]);
		}
		return sb.toString();
	}

	private boolean isSuitable(CYKTable table)
	{
		if (table.getDifferentSets() >= minDifferentSets && table.getDifferentSets() <= maxDifferentSets
			&& table.getEmptySets() >= minEmptySets && table.getEmptySets() <= maxEmptySets
			&& table.isGeneratable() == generatable)
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
		String czechTex = "Proveďte deterministickou syntaktickou analýzu pro zadanou gramatiku $\\mathcal{G}$ a slovo $w$.";
		String english = "Do deterministic syntactic analysis for given grammar G and word w.";
		String englishTex = "Do deterministic syntactic analysis for given grammar $\\mathcal{G}$ and word $w$.";
		sbLatexEN.append(englishTex);
		sbLatex.append(czechTex);
		sbPlainText.append(czech);
		sbPlainTextEN.append(english);

		task.put("latex", sbLatex.toString());
		task.put("latexEN", sbLatexEN.toString());
		task.put("plainText", sbPlainText.toString() + LINE_SEPARATOR + LINE_SEPARATOR);
		task.put("plainTextEN", sbPlainTextEN.toString() + LINE_SEPARATOR + LINE_SEPARATOR);

		return task;
	}

}
