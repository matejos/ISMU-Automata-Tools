/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractContextFreeGrammar<E extends ContextFreeRule> implements ContextFreeGrammar<E>
{

	private NonTerminal S; // startSymbol
	private Set<NonTerminal> N = new HashSet<NonTerminal>();
	private Set<Terminal> T = new HashSet<Terminal>();
	private Set<Symbol> sym = new HashSet<Symbol>();
	private Set<E> P = new HashSet<E>();
	private Set<E> epsilonRules = new HashSet<E>();
	private Set<E> terminalRules = new HashSet<E>();
	private Set<E> simpleRules = new HashSet<E>();
	private Map<Symbol, Set<E>> withThese = new HashMap<Symbol, Set<E>>();
	private Map<NonTerminal, Set<E>> withTheseOnLeft = new HashMap<NonTerminal, Set<E>>();
	private Map<Symbol, Set<E>> withTheseOnRight = new HashMap<Symbol, Set<E>>();
	// private Map<Symbol, Map<Integer, Set<E>>> withTheseOnRightAt = new HashMap<Symbol, Map<Integer, Set<E>>>();
	public static final int MAX_NUMBER_OF_RULES = 100;

	protected AbstractContextFreeGrammar()
	{
	}

	private void nullTest(Object obj)
	{
		if (obj == null)
		{
			throw new NullPointerException("obj");
		}
	}

	public Set<E> getRules()
	{
		return Collections.unmodifiableSet(P);
	}

	public Set<NonTerminal> getNonTerminals()
	{
		return Collections.unmodifiableSet(N);
	}

	public Set<Terminal> getTerminals()
	{
		return Collections.unmodifiableSet(T);
	}

	public Set<Symbol> getAllSymbols()
	{
		return Collections.unmodifiableSet(sym);
	}

	public Set<E> allRulesWithSymbol(Symbol s)
	{
		nullTest(s);
		if (withThese.containsKey(s))
		{
			return Collections.unmodifiableSet(withThese.get(s));
		}
		else
		{
			return new HashSet<E>();
		}
	}

	public Set<E> allRulesWithSymbolOnRight(Symbol s)
	{
		nullTest(s);
		if (withTheseOnRight.containsKey(s))
		{
			return Collections.unmodifiableSet(withTheseOnRight.get(s));
		}
		else
		{
			return new HashSet<E>();
		}
	}

	public Set<E> allRulesWithSymbolOnLeft(NonTerminal s)
	{
		nullTest(s);
		if (withTheseOnLeft.containsKey(s))
		{
			return Collections.unmodifiableSet(withTheseOnLeft.get(s));
		}
		else
		{
			return new HashSet<E>();
		}
	}

	/*
	 * public Set<E> allRulesWithSymbolOnRightAt(Symbol s, int position) { nullTest(s); if (position < 1) { throw new
	 * NullPointerException("position must be greater then zero"); } if (!withTheseOnRightAt.containsKey(s)) { return
	 * new TreeSet<E>(); } else { if (withTheseOnRightAt.get(s).containsKey(position)) { return
	 * Collections.unmodifiableSet(withTheseOnRightAt.get(s).get(position)); } else { return new HashSet<E>(); } } }
	 */

	/*
	 * public Set<E> allRulesWithAsFirstOnRight(Symbol s) { return
	 * Collections.unmodifiableSet(allRulesWithSymbolOnRightAt(s, 1)); }
	 */

	public Set<E> allEpsilonRules()
	{
		return Collections.unmodifiableSet(epsilonRules);
	}

	public Set<E> allSimpleRules()
	{
		return Collections.unmodifiableSet(simpleRules);
	}

	public Set<E> allTerminalRules()
	{
		return Collections.unmodifiableSet(terminalRules);
	}

	public void setStartSymbol(NonTerminal newStartSymbol)
	{
		nullTest(newStartSymbol);

		// ContextFreeRule[] backup = new ContextFreeRule[0];
		if (withThese.containsKey(newStartSymbol) && withThese.get(newStartSymbol) != null)
		{
			S = newStartSymbol;
		}
		else
		{
			addNonTerminal(newStartSymbol);
			S = newStartSymbol;
		}
	}

	public NonTerminal getStartSymbol()
	{
		return S;
	}

	public void addRuleAutoAddSymbols(E newRule) throws TooManyRulesException
	{
		nullTest(newRule);

		if (ruleExists(newRule))
		{
			return;
		}

		if (P.size() + 1 > MAX_NUMBER_OF_RULES)
		{
			throw new TooManyRulesException("You were trying to add " + (P.size() + 1)
				+ "-th rule to the grammar. Grammar can have max " + MAX_NUMBER_OF_RULES
				+ " rules. This bound is to prevent slow run of program or even JavaHeapOutOfMemory exception");
		}

		NonTerminal leftNT = newRule.getSymbolOnLeftSide();
		if (!N.contains(leftNT))
		{
			addNonTerminal(leftNT);
		}

		if (!withThese.containsKey(leftNT) || withThese.get(leftNT) == null)
		{
			withThese.put(leftNT, new HashSet<E>());
		}
		withThese.get(leftNT).add(newRule);

		if (!withTheseOnLeft.containsKey(leftNT) || withTheseOnLeft.get(leftNT) == null)
		{
			withTheseOnLeft.put(leftNT, new HashSet<E>());
		}
		withTheseOnLeft.get(leftNT).add(newRule);

		// int i = 1;
		for (Symbol s : newRule.getSymbolsOnRightSide())
		{
			if (!sym.contains(s))
			{
				addSymbol(s);
			}

			if (!withThese.containsKey(s) || withThese.get(s) == null)
			{
				withThese.put(s, new HashSet<E>());
			}
			withThese.get(s).add(newRule);

			if (!withTheseOnRight.containsKey(s) || withTheseOnRight.get(s) == null)
			{
				withTheseOnRight.put(s, new HashSet<E>());
			}
			withTheseOnRight.get(s).add(newRule);

			/*
			 * if (!withTheseOnRightAt.containsKey(s) || withTheseOnRightAt.get(s) == null) { withTheseOnRightAt.put(s,
			 * new HashMap<Integer, Set<E>>()); } if (!withTheseOnRightAt.get(s).containsKey(i) ||
			 * withTheseOnRightAt.get(s).get(i) == null) { withTheseOnRightAt.get(s).put(i, new HashSet<E>()); }
			 * withTheseOnRightAt.get(s).get(i).add(newRule);
			 */
			// i++;
		}

		if (newRule.isEpsilonRule())
		{
			epsilonRules.add(newRule);
		}
		if (newRule.isSimpleRule())
		{
			simpleRules.add(newRule);
		}
		if (newRule.isTerminalRule())
		{
			terminalRules.add(newRule);
		}
		P.add(newRule);
	}

	public void addRulesAutoAddSymbols(Iterable<E> newRules) throws TooManyRulesException
	{
		nullTest(newRules);
		for (E r : newRules)
		{
			addRuleAutoAddSymbols(r);
		}
	}

	public boolean deleteRuleAutoDeleteSymbols(E rule)
	{
		nullTest(rule);
		if (!P.contains(rule))
		{
			return false;
		}

		simpleRules.remove(rule);
		epsilonRules.remove(rule);
		terminalRules.remove(rule);
		P.remove(rule);

		NonTerminal left = rule.getSymbolOnLeftSide();
		if (withThese.containsKey(left))
		{
			withThese.get(left).remove(rule);
			withTheseOnLeft.get(left).remove(rule);
			if (withThese.get(left).isEmpty())
			{
				deleteNonTerminalWithNoRules(left);
			}
		}

		// int i = 1;
		for (Symbol s : rule.getSymbolsOnRightSide())
		{
			if (withThese.containsKey(s))
			{
				withThese.get(s).remove(rule);
				withTheseOnRight.get(s).remove(rule);
				/*
				 * try { withTheseOnRightAt.get(s).get(i).remove(rule); } catch (NullPointerException ex) { throw new
				 * IllegalStateException("withTheseOnRight was null at symbol " + s + " at position " + i +
				 * " during deleting rule " + rule, ex); }
				 */
				if (withThese.get(s).isEmpty())
				{
					if (s.isNonTerminal())
					{
						deleteNonTerminalWithNoRules((NonTerminal) s);
					}
					else if (s.isTerminal())
					{
						deleteTerminalWithNoRules((Terminal) s);
					}
					else if (s.isEpsilon())
					{
						deleteEpsilonWithNoRules(s);
					}
					else
					{
						throw new InternalError("Symbol " + s + " is not terminal, neither non-terminal, "
							+ "nor epsilon");
					}
				}
			}
			// i++;
		}

		return true;
	}

	public int deleteRulesAutoDeleteSymbols(Iterable<E> rules)
	{
		nullTest(rules);

		int result = 0;
		for (E r : rules)
		{
			if (deleteRuleAutoDeleteSymbols(r))
			{
				result++;
			}
		}

		return result;
	}

	public boolean ruleExists(E candidate)
	{
		nullTest(candidate);
		return P.contains(candidate);
	}

	public void addNonTerminal(NonTerminal newNonterminal)
	{
		nullTest(newNonterminal);

		sym.add(newNonterminal);
		N.add(newNonterminal);
	}

	private void deleteNonTerminalWithNoRules(NonTerminal nonTerminal)
	{
		withThese.remove(nonTerminal);
		withTheseOnLeft.remove(nonTerminal);
		withTheseOnRight.remove(nonTerminal);
		// withTheseOnRightAt.remove(nonTerminal);
		sym.remove(nonTerminal);
		N.remove(nonTerminal);
	}

	public boolean deleteNonTeminalAutoDeleteRules(NonTerminal nonTerminal)
	{
		nullTest(nonTerminal);

		boolean result = N.contains(nonTerminal);

		Set<E> backup = withThese.remove(nonTerminal);

		if (backup != null)
		{
			deleteRulesAutoDeleteSymbols(backup);
		}

		deleteNonTerminalWithNoRules(nonTerminal);
		return result;
	}

	public boolean nonTerminalExists(NonTerminal candidate)
	{
		nullTest(candidate);

		return N.contains(candidate);
	}

	public void addTerminal(Terminal newTerminal)
	{
		nullTest(newTerminal);

		sym.add(newTerminal);
		T.add(newTerminal);
	}

	private void deleteTerminalWithNoRules(Terminal terminal)
	{
		withThese.remove(terminal);
		withTheseOnRight.remove(terminal);
		// withTheseOnRightAt.remove(terminal);
		sym.remove(terminal);
		T.remove(terminal);
	}

	public boolean deleteTerminalAutoDeleteRules(Terminal terminal)
	{
		nullTest(terminal);

		boolean result = T.contains(terminal);

		Set<E> backup = withThese.remove(terminal);

		if (backup != null)
		{
			deleteRulesAutoDeleteSymbols(backup);
		}

		deleteTerminalWithNoRules(terminal);
		return result;
	}

	public boolean terminalExists(Terminal candidate)
	{
		nullTest(candidate);

		return T.contains(candidate);
	}

	public void addSymbol(Symbol symbol)
	{
		nullTest(symbol);

		if (symbol.isNonTerminal())
		{
			addNonTerminal((NonTerminal) symbol);
		}
		else if (symbol.isTerminal())
		{
			addTerminal((Terminal) symbol);
		}
		else if (symbol.isEpsilon())
		{
			sym.add(symbol);
		}
		else
		{
			throw new InternalError("Symbol " + symbol + " is not terminal, neither non-terminal, " + "nor epsilon");
		}
	}

	private void deleteEpsilonWithNoRules(Symbol symbol)
	{
		withThese.remove(symbol);
		withTheseOnRight.remove(symbol);
		// withTheseOnRightAt.remove(symbol);
		sym.remove(symbol);
	}

	public boolean deleteSymbolAutoDeleteRules(Symbol symbol)
	{
		nullTest(symbol);

		if (symbol.isNonTerminal())
		{
			return deleteNonTeminalAutoDeleteRules((NonTerminal) symbol);
		}
		else if (symbol.isTerminal())
		{
			return deleteTerminalAutoDeleteRules((Terminal) symbol);
		}
		else if (symbol.isEpsilon())
		{
			boolean result = sym.contains(symbol);

			Set<E> backup = withThese.remove(symbol);

			if (backup != null)
			{
				deleteRulesAutoDeleteSymbols(backup);
			}

			deleteEpsilonWithNoRules(symbol);
			return result;
		}
		else
		{
			throw new InternalError("Symbol " + symbol + " is not terminal, neither nonterminal, " + "nor epsilon");
		}
	}

	public boolean SymbolExists(Symbol candidate)
	{
		nullTest(candidate);

		return sym.contains(candidate);
	}

	public boolean hasEpsilonRules()
	{
		return !epsilonRules.isEmpty();
	}

	public boolean hasSimpleRules()
	{
		return !simpleRules.isEmpty();
	}

	public boolean hasTeminalRules()
	{
		return !terminalRules.isEmpty();
	}

	public boolean isOfFormNoEpsilonRules()
	{
		if (simpleRules.isEmpty())
		{
			return true;
		}
		if (epsilonRules.size() == 1)
		{
			for (ContextFreeRule rule : epsilonRules)
			{
				if (rule.getSymbolOnLeftSide().equals(S) && allRulesWithSymbolOnRight(S).isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	// rules should not contain any nonterminal that is not in N
	// better implementation would be without parameter 'rules' - it would be explicitly P
	// I was too lazy to do that (in SimpleRulesElimAlg it is called with parameter 'simplerules')
	public Map<NonTerminal, Set<NonTerminal>> getMapOfDerivableNonTerminals(Set<E> rules)
	{
		nullTest(rules);
		// derivableNonTerminals - 2-tuples (A, N_A), where N_A = {B\in N | A =>^+ B...} such that:
		// - word "B..." has to be derivable using only "rules" (parameter)
		Map<NonTerminal, Set<NonTerminal>> derivableNonTerminals = new HashMap<NonTerminal, Set<NonTerminal>>();

		for (NonTerminal n : N)
		{
			derivableNonTerminals.put(n, new HashSet<NonTerminal>());
		}

		// creating "graph" = initial map (deliberating only one step derivation (->))
		for (ContextFreeRule r : rules)
		{
			// ignore rules where first symbol on right side is not NonTerminal
			if (r.getFirstRightSymbol().isNonTerminal())
			{
				derivableNonTerminals.get(r.getSymbolOnLeftSide()).add((NonTerminal) r.getFirstRightSymbol());
			}
		}

		boolean cont = true;
		Set<NonTerminal> added = new HashSet<NonTerminal>();
		while (cont)
		{
			cont = false;
			for (NonTerminal n : derivableNonTerminals.keySet())
			{
				for (NonTerminal nOnRight : derivableNonTerminals.get(n))
				{
					if (derivableNonTerminals.keySet().contains(nOnRight))
					{
						added.addAll(derivableNonTerminals.get(nOnRight));
					}
				}
				cont = derivableNonTerminals.get(n).addAll(added) | cont;
				added.clear();
			}
		}

		return derivableNonTerminals;
	}

	// if this grammar is of form NO_EPSILON, i. e. (isOfFormNoEpsilonRules()),
	// this method returns true iff it is cyclic
	public boolean hasCyclesFromSimpleRules()
	{
		for (NonTerminal n : getMapOfDerivableNonTerminals(simpleRules).keySet())
		{
			if (getMapOfDerivableNonTerminals(simpleRules).get(n).contains(n))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public abstract ContextFreeGrammar<E> clone();

	@Override
	public String toString()
	{
		String result = "";
		SortedSet<NonTerminal> nonTer = new TreeSet<NonTerminal>();
		nonTer.addAll(N);

		boolean first = true;

		String line = "";
		for (NonTerminal n : nonTer)
		{
			SortedSet<ContextFreeRule> rul = new TreeSet<ContextFreeRule>(allRulesWithSymbolOnLeft(n));
			line = n + ContextFreeRule.ARROW;
			first = true;
			for (ContextFreeRule r : rul)
			{
				if (first)
				{
					line = line + r.rightSideToString();
					first = false;
				}
				else
				{
					line = line + ContextFreeRule.RIGHT_SIDE_SEPARATOR + r.rightSideToString();
				}
			}
			if (!rul.isEmpty())
			{
				result = result + line + "\n";
			}
		}
		return result;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		if (!(o instanceof ContextFreeGrammar))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}

		ContextFreeGrammar other = (ContextFreeGrammar) o;
		Set<ContextFreeRule> or = new HashSet<ContextFreeRule>();
		or.addAll(other.getRules());
		if (or.size() != P.size())
		{
			return false;
		}
		for (E e : P)
		{
			if (!or.remove(e))
			{
				return false;
			}
		}
		return or.isEmpty();
	}
}
