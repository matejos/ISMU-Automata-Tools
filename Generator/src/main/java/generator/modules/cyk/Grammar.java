
package generator.modules.cyk;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import generator.modules.reglang.regularGrammar.NoSuchVariableException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Grammar
{
	private SortedSet<String> nonTerminals = new TreeSet<String>();
	private SortedSet<String> terminals = new TreeSet<String>();
	private SortedMap<String, Set<String>> rules = new TreeMap<String, Set<String>>();// new Comparator<String>()
	// {
	//
	// public int compare(String o1, String o2)
	// {
	// if (o1 != null && o1.equals("S") && !o2.equals("S")) return -1;
	// if (o2 != null && o2.equals("S")&& !o1.equals("S")) return 1;
	// return o1.compareToIgnoreCase(o2);
	// }
	// });
	private String initialNonTerminal;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public Grammar(String initialNonTerminal)
	{
		this.initialNonTerminal = initialNonTerminal;
	}
	public SortedSet<String> getNonTerminals()
	{
		return nonTerminals;
	}
	public void setNonTerminals(SortedSet<String> nonTerminals)
	{
		this.nonTerminals = nonTerminals;
	}
	public SortedSet<String> getTerminals()
	{
		return terminals;
	}
	public void setTerminals(SortedSet<String> terminals)
	{
		this.terminals = terminals;
	}
	public SortedMap<String, Set<String>> getRules()
	{
		return rules;
	}
	public void setRules(SortedMap<String, Set<String>> rules)
	{
		this.rules = rules;
	}
	public String getInitialNonTerminal()
	{
		return initialNonTerminal;
	}
	public void setInitialNonTerminal(String initialNonTerminal)
	{
		this.initialNonTerminal = initialNonTerminal;
	}
	@Override
	public String toString()
	{
		StringBuilder nonTerminalsStringBuilder = new StringBuilder();
		for (String nonTerminal : nonTerminals)
		{
			nonTerminalsStringBuilder.append(nonTerminal + ",");
		}
		nonTerminalsStringBuilder.setLength(nonTerminalsStringBuilder.length() - 1);
		StringBuilder terminalsStringBuilder = new StringBuilder();

		for (String terminal : terminals)
		{
			terminalsStringBuilder.append(terminal + ",");
		}
		terminalsStringBuilder.setLength(terminalsStringBuilder.length() - 1);

		StringBuilder rulesBuilder = new StringBuilder();
		int i = 1;
		rulesBuilder.append(" " + initialNonTerminal + " -> ");
		for (String rightHandRule : rules.get(initialNonTerminal))
		{
			rulesBuilder.append(rightHandRule + " | ");
		}
		rulesBuilder.setLength(rulesBuilder.length() - 3);
		if (i < rules.keySet().size())
		{
			rulesBuilder.append("," + LINE_SEPARATOR + "     ");
		}
		i++;
		for (String nonTerminal : rules.keySet())
		{
			if (nonTerminal.equals(initialNonTerminal))
				continue;
			rulesBuilder.append(" " + nonTerminal + " -> ");
			for (String rightHandRule : rules.get(nonTerminal))
			{
				rulesBuilder.append(rightHandRule + " | ");
			}
			rulesBuilder.setLength(rulesBuilder.length() - 3);
			if (i < rules.keySet().size())
			{
				rulesBuilder.append("," + LINE_SEPARATOR + "     ");
			}
			i++;
		}

		return "G = ({" + nonTerminalsStringBuilder.toString() + "},{" + terminalsStringBuilder.toString() + "},P,"
			+ initialNonTerminal + "), where " + LINE_SEPARATOR + "P = {" + rulesBuilder.toString() + " }"
			+ LINE_SEPARATOR;
	}

	public String toStringPlainEn()
	{
		return "Grammar " + toString();
	}

	public String toStringPlainCz()
	{
		return ("Gramatika " + toString()).replace("where", "kde");
	}

	public String toLaTeX()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("\\begin{center}$" + "\\mathcal{G}" + " = (\\{");
		StringBuilder nonTerminalsStringBuilder = new StringBuilder();
		for (String nonTerminal : nonTerminals)
		{
			nonTerminalsStringBuilder.append(nonTerminal + ",");
		}
		nonTerminalsStringBuilder.setLength(nonTerminalsStringBuilder.length() - 1);
		sb.append(nonTerminalsStringBuilder.toString() + "\\},\\{");
		StringBuilder terminalsStringBuilder = new StringBuilder();

		for (String terminal : terminals)
		{
			terminalsStringBuilder.append(terminal + ",");
		}
		terminalsStringBuilder.setLength(terminalsStringBuilder.length() - 1);
		sb.append(terminalsStringBuilder.toString() + "\\},P," + initialNonTerminal + "\\},$ where\\\\");
		StringBuilder rulesBuilder = new StringBuilder();
		rulesBuilder.append("\\[\n\\setlength{\\arraycolsep}{1.5pt}\n\\begin{array}{rlll}\nP = \\{ ");
		int i = 1;
		rulesBuilder.append(" " + initialNonTerminal + "& \\rightarrow & ");
		for (String rightHandRule : rules.get(initialNonTerminal))
		{
			rulesBuilder.append(rightHandRule + " \\mid ");
		}
		rulesBuilder.setLength(rulesBuilder.length() - 5);
		if (i < rules.keySet().size())
		{
			rulesBuilder.append("," + LINE_SEPARATOR + "\\\\     ");
		}
		i++;
		for (String nonTerminal : rules.keySet())
		{
			if (nonTerminal.equals(initialNonTerminal))
				continue;
			rulesBuilder.append(" " + nonTerminal + "& \\rightarrow & ");
			for (String rightHandRule : rules.get(nonTerminal))
			{
				rulesBuilder.append(rightHandRule + " \\mid ");
			}
			rulesBuilder.setLength(rulesBuilder.length() - 5);
			if (i < rules.keySet().size())
			{
				rulesBuilder.append("," + LINE_SEPARATOR + "\\\\     ");
			}
			i++;
		}

		rulesBuilder.replace(rulesBuilder.length() - 1, rulesBuilder.length(), "");
		rulesBuilder.append("\\} \\\\\n\\end{array}\n\\]");
		sb.append(rulesBuilder.toString() + "\\end{center}");

		return sb.toString() + LINE_SEPARATOR;
	}

}
