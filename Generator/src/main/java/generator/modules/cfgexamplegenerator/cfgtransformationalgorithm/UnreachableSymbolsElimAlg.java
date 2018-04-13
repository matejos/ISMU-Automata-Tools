/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UnreachableSymbolsElimAlg extends AbstractCFGTransformationAlgorithm
{

	private static final Form[] req = {};
	private static final Form[] sure = { Form.NO_UNREACHABLE };
	public static final int deleteCostMultiplier = 1;
	public static final int cycleCostMultiplier = 1;
	public static final UnreachableSymbolsElimAlg INSTANCE = new UnreachableSymbolsElimAlg();

	public static UnreachableSymbolsElimAlg getInstance()
	{
		return INSTANCE;
	}

	private UnreachableSymbolsElimAlg()
	{
		super(req, sure);
	}

	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
	{
		nullTest(grammar);
		int cycleIterations = 0;
		int removed = 0;
		boolean diff1 = false;
		boolean diff2 = false;
		Set<Symbol> reachable = new HashSet<Symbol>();
		Set<NonTerminal> newReached = new HashSet<NonTerminal>();
		Set<Symbol> toRemove = new HashSet<Symbol>();
		Set<NonTerminal> helpSet = new HashSet<NonTerminal>();
		Set<NonTerminal> exchangeSet = new HashSet<NonTerminal>();

		reachable.add(grammar.getStartSymbol());
		newReached.add(grammar.getStartSymbol());

		while (!newReached.isEmpty())
		{
			cycleIterations++;
			reachable.addAll(newReached);
			for (NonTerminal n : newReached)
			{
				for (ContextFreeRule r : grammar.allRulesWithSymbolOnLeft(n))
				{
					for (Symbol s : r.getSymbolsOnRightSide())
					{
						if (!reachable.contains(s))
						{
							reachable.add(s);
							if (s.isNonTerminal())
							{
								helpSet.add((NonTerminal) s);
							}
						}
					}
				}
			}
			exchangeSet = helpSet;
			helpSet = newReached;
			newReached = exchangeSet;
			helpSet.clear();
		}

		toRemove.addAll(grammar.getAllSymbols());
		toRemove.removeAll(reachable);
		removed = toRemove.size();

		int numberOfRulesBefore = grammar.getRules().size();
		for (Symbol s : toRemove)
		{
			grammar.deleteSymbolAutoDeleteRules(s);
			if (s.isNonTerminal())
			{
				diff1 = true;
			}
			else if (s.isTerminal())
			{
				diff2 = true;
			}
		}

		int diff = 0;
		if (numberOfRulesBefore > grammar.getRules().size())
		{
			if (diff1)
			{
				diff++;
			}
			if (diff2)
			{
				diff++;
			}
		}

		List<NonTerminal> newOrder = new SetList<NonTerminal>();
		for (NonTerminal n : order)
		{
			if (grammar.nonTerminalExists(n) && !grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				newOrder.add(n);
			}
		}

		RunResult result = new RunResult(grammar, diff, newOrder);
		result.setMetric(Metric.NON_TERMINALS_DELETED, removed);
		result.setMetric(Metric.CYCLE_COUNT, cycleIterations);
		result.setMetric(Metric.DIFF, diff);

		return result;

	}
}
