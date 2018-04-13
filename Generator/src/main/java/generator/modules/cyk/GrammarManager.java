
package generator.modules.cyk;

public interface GrammarManager
{
	// TODO javadoc
	/**
	 * Creates a new grammar instance for given initialNonTerminal
	 * 
	 * @param initialNonTerminal
	 * @return
	 */
	Grammar create(String initialNonTerminal);
	/**
	 * adds a rule to this grammar, extending terminals and nonterminals if needed
	 * 
	 * @param grammar
	 * @param leftSideNonTerminal
	 * @param rightSideString
	 * @return
	 */
	void addCFGRule(Grammar grammar, String leftSideNonTerminal, String rightSideString);

	void addTerminal(Grammar g, String terminal);

	void addNonterminal(Grammar g, String nonTerminal);
}
