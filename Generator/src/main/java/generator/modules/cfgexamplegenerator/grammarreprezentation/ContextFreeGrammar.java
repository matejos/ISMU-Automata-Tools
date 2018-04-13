/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import java.util.Map;
import java.util.Set;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
public interface ContextFreeGrammar<T extends ContextFreeRule> extends Cloneable
{

	public void setStartSymbol(NonTerminal newStartSymbol);
	public NonTerminal getStartSymbol();
	public void addRuleAutoAddSymbols(T newRule) throws TooManyRulesException;
	public void addRulesAutoAddSymbols(Iterable<T> newRules) throws TooManyRulesException;
	public boolean deleteRuleAutoDeleteSymbols(T rule);
	public int deleteRulesAutoDeleteSymbols(Iterable<T> rules);
	public boolean ruleExists(T candidate);
	public Set<T> getRules();
	public void addNonTerminal(NonTerminal newNonterminal);
	public boolean deleteNonTeminalAutoDeleteRules(NonTerminal nonTerminal);
	public boolean nonTerminalExists(NonTerminal candidate);
	public Set<NonTerminal> getNonTerminals();
	public void addTerminal(Terminal newTerminal);
	public Set<Terminal> getTerminals();
	public boolean deleteTerminalAutoDeleteRules(Terminal terminal);
	public boolean terminalExists(Terminal candidate);
	public void addSymbol(Symbol symbol);
	public Set<Symbol> getAllSymbols();
	public boolean deleteSymbolAutoDeleteRules(Symbol symbol);
	public boolean SymbolExists(Symbol candidate);

	public Set<T> allRulesWithSymbol(Symbol s);
	public Set<T> allRulesWithSymbolOnRight(Symbol s);
	public Set<T> allRulesWithSymbolOnLeft(NonTerminal s);
	// public Set<T> allRulesWithSymbolOnRightAt(Symbol s, int possition);
	// public Set<T> allRulesWithAsFirstOnRight(Symbol s);

	public Set<T> allEpsilonRules();
	public Set<T> allSimpleRules();
	public Set<T> allTerminalRules();

	public boolean hasEpsilonRules();
	public boolean hasSimpleRules();
	public boolean hasTeminalRules();

	/**
	 * Returns <tt>true</tt> if this grammar contains no epsilon-rules or it contains only one epsilon-rule S -> eps and
	 * S does not occur on right side of any rule.
	 * 
	 * @return <tt>true</tt> if this grammar fulfills constraints on form NO_EPSILON
	 */
	public boolean isOfFormNoEpsilonRules();

	/**
	 * Returns all 2-tuples (A, N_A) such that N_A = {B \in N | A ->^+ B... using specified set of rules}, where "..."
	 * stands for anything in (alphabet)*.
	 * 
	 * @param rules
	 *            the set of rules using which derivation is done
	 * @return map containing all 2-tuples (A, N_A) such that N_A = {B \in N | A ->^+ B...}, where "..." \in (alphabet)*
	 *         and derivation is possible only using rules in the specified set ("rules")
	 */
	public Map<NonTerminal, Set<NonTerminal>> getMapOfDerivableNonTerminals(Set<T> rules);

	/**
	 * Returns <tt>true</tt> if simple rules of this grammar create a cycle (A ->^+ A, A\in N). If the grammar is of
	 * form NO_EPSILON, this method return <tt>true</tt> iff it is cyclic.
	 * 
	 * @return <tt>true</tt> if simple rules of this grammar create a cycle
	 */
	public boolean hasCyclesFromSimpleRules();

	public ContextFreeGrammar<T> clone();
	@Override
	public boolean equals(Object obj);
	@Override
	public int hashCode();
	@Override
	public String toString();
}
