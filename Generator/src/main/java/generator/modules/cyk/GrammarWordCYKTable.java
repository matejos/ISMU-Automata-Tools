
package generator.modules.cyk;

import java.util.List;
import java.util.Set;

public class GrammarWordCYKTable
{

	private String word;
	private Grammar grammar;
	private List<List<Set<String>>> cykTable;

	public GrammarWordCYKTable()
	{

	}

	public GrammarWordCYKTable(String word, Grammar grammar, List<List<Set<String>>> cykTable)
	{
		super();
		this.word = word;
		this.grammar = grammar;
		this.cykTable = cykTable;
	}

	public String getWord()
	{
		return word;
	}
	public void setWord(String word)
	{
		this.word = word;
	}
	public Grammar getGrammar()
	{
		return grammar;
	}
	public void setGrammar(Grammar grammar)
	{
		this.grammar = grammar;
	}
	public List<List<Set<String>>> getCykTable()
	{
		return cykTable;
	}
	public void setCykTable(List<List<Set<String>>> cykTable)
	{
		this.cykTable = cykTable;
	}

}
