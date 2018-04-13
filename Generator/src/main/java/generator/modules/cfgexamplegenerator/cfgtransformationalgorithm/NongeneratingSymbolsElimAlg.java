/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings({ "rawtypes" })
public class NongeneratingSymbolsElimAlg extends AbstractCFGTransformationAlgorithm
{

	private static final Form[] req = {};
	private static final Form[] sure = { Form.NO_NONGENERATING };
	public static final NongeneratingSymbolsElimAlg INSTANCE = new NongeneratingSymbolsElimAlg();

	private NongeneratingSymbolsElimAlg()
	{
		super(req, sure);
	}

	public static NongeneratingSymbolsElimAlg getInstance()
	{
		return INSTANCE;
	}

	protected static class ResultWithNSet
	{

		private Map<NonTerminal, Integer> reachedIn;
		private ContextFreeGrammar<ContextFreeRule> transformedGrammar;
		private int diff;
		private Map<Metric, Integer> metrics = new HashMap<Metric, Integer>();

		public ResultWithNSet(ContextFreeGrammar<ContextFreeRule> grammar, int cost, Map<NonTerminal, Integer> reachedIn)
		{

			this.reachedIn = reachedIn;
			transformedGrammar = grammar;
			diff = cost;
		}

		public int getCost()
		{
			return diff;
		}

		public ContextFreeGrammar<ContextFreeRule> getTransformedGrammar()
		{
			return transformedGrammar;
		}

		public void setMetric(Metric metric, int value)
		{
			if (metric == null)
			{
				throw new NullPointerException("metric");
			}
			metrics.put(metric, value);
		}

		public int getMetric(Metric metric)
		{
			return metrics.get(metric);
		}

		public Map<NonTerminal, Integer> getReachedIn()
		{
			return reachedIn;
		}
	}

	protected interface AddCondition
	{

		boolean preventAddition(Symbol s, Set<NonTerminal> alreadyAdded);
	}

	protected ResultWithNSet processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar,
		Set<ContextFreeRule> startSet, AddCondition condition)
	{
		nullTest(grammar);
		nullTest(startSet);

		Set<Symbol> originalSymbols = new HashSet<Symbol>();
		originalSymbols.addAll(grammar.getAllSymbols());

		Map<NonTerminal, Integer> reachedIn = new HashMap<NonTerminal, Integer>();
		Set<NonTerminal> justTerminated = new HashSet<NonTerminal>();
		Set<NonTerminal> helpSet = new HashSet<NonTerminal>();
		Set<NonTerminal> exchangeSet = new HashSet<NonTerminal>();
		Set<Symbol> reachableTerminals = new HashSet<Symbol>();

		for (ContextFreeRule r : startSet)
		{
			reachedIn.put(r.getSymbolOnLeftSide(), 0);
			justTerminated.add(r.getSymbolOnLeftSide());
		}

		int iterationCount = 0;
		boolean canBeTerminated;
		while (!justTerminated.isEmpty())
		{
			iterationCount++;
			for (NonTerminal n : justTerminated)
			{
				for (ContextFreeRule r : grammar.allRulesWithSymbolOnRight(n))
				{
					if (!reachedIn.containsKey(r.getSymbolOnLeftSide()))
					{
						canBeTerminated = true;
						for (Symbol s : r.getSymbolsOnRightSide())
						{
							if (condition.preventAddition(s, reachedIn.keySet()))
							{
								canBeTerminated = false;
							}
							if (!s.isNonTerminal())
							{
								reachableTerminals.add(s);
							}
						}
						if (canBeTerminated)
						{
							helpSet.add(r.getSymbolOnLeftSide());
							reachedIn.put(r.getSymbolOnLeftSide(), iterationCount);
						}
					}
				}
			}

			exchangeSet = helpSet;
			helpSet = justTerminated;
			justTerminated = exchangeSet;
			helpSet.clear();
		}

		int resultPart = 0;
		for (NonTerminal n : reachedIn.keySet())
		{
			resultPart = resultPart + grammar.allRulesWithSymbolOnLeft(n).size() * reachedIn.get(n);
		}

		originalSymbols.removeAll(reachableTerminals);
		originalSymbols.removeAll(reachedIn.keySet());

		ResultWithNSet result = new ResultWithNSet(grammar, resultPart + originalSymbols.size() * iterationCount,
			reachedIn);
		result.setMetric(Metric.CYCLE_COUNT, iterationCount);
		return result;
	}

	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
	{
		nullTest(grammar);

		Set<ContextFreeRule> startSet = new HashSet<ContextFreeRule>();
		startSet.addAll(grammar.allTerminalRules());
		startSet.addAll(grammar.allEpsilonRules());

		ResultWithNSet result = processAlgorithm(grammar, startSet, new AddCondition()
		{

			public boolean preventAddition(Symbol s, Set<NonTerminal> alreadyAdded)
			{
				return s.isNonTerminal() && !alreadyAdded.contains(s);
			}
		});

		Set<NonTerminal> toRemove = new HashSet<NonTerminal>();
		toRemove.addAll(grammar.getNonTerminals());
		toRemove.removeAll(result.getReachedIn().keySet());

		int removed = 0;
		int numberOfRulesBefore = grammar.getRules().size();
		for (NonTerminal n : toRemove)
		{
			grammar.deleteNonTeminalAutoDeleteRules(n);
			removed++;
		}

		List<NonTerminal> newOrder = new SetList<NonTerminal>();
		for (NonTerminal n : order)
		{
			if (grammar.nonTerminalExists(n) && !grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				newOrder.add(n);
			}
		}

		int diff = 0;
		if (numberOfRulesBefore > grammar.getRules().size())
		{
			diff++;// diff1 - at least 1 nonterminal had to be removed from N
			for (Integer steps : result.getReachedIn().values())
			{
				if (steps > 0)
				{
					diff++;// diff2
					break;
				}
			}
		}

		RunResult<ContextFreeGrammar> runRes = new RunResult<ContextFreeGrammar>(grammar, diff, newOrder);
		runRes.setMetric(Metric.NON_TERMINALS_DELETED, removed);
		runRes.setMetric(Metric.CYCLE_COUNT, result.getMetric(Metric.CYCLE_COUNT));
		runRes.setMetric(Metric.DIFF, diff);

		return runRes;
	}
}
