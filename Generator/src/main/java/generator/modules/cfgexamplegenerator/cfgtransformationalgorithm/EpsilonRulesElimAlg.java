/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.generator.SymbolManager;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SimpleContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
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
public class EpsilonRulesElimAlg extends AbstractCFGTransformationAlgorithm
{

	private static final Form[] req = {};
	private static final Form[] sure = { Form.NO_EPSILON };
	public static final EpsilonRulesElimAlg INSTANCE = new EpsilonRulesElimAlg();

	public static EpsilonRulesElimAlg getInstance()
	{
		return INSTANCE;
	}

	private EpsilonRulesElimAlg()
	{
		super(req, sure);
	}

	@Override
	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
		throws TooManyRulesException
	{
		nullTest(grammar);

		NongeneratingSymbolsElimAlg.ResultWithNSet result = NongeneratingSymbolsElimAlg.getInstance().processAlgorithm(
			grammar, grammar.allEpsilonRules(), new NongeneratingSymbolsElimAlg.AddCondition()
			{

				public boolean preventAddition(Symbol s, Set<NonTerminal> alreadyAdded)
				{
					return s.isTerminal() || (s.isNonTerminal() && !alreadyAdded.contains(s));
				}
			});

		int addedAndDelRulesCount = 0;
		boolean diff1 = false;// if there is an eps-production
		boolean diff2 = false;// if there is A such that (A =>* eps) && (A -> eps !\in P)
		boolean diff3 = false;// if there is A -> X1X2...Xn, n > 1, Xi \in (N or T) && there is "i" such that Xi =>* eps
		boolean diff4 = false;// the same as diff3, just there has to be i != j such that Xi, Xj \in N_eps
		boolean diff5 = false;// if there is A -> X1...Xn, n > 1, for all i: Xi =>* eps
		Set<ContextFreeRule> toDelete = new HashSet<ContextFreeRule>();
		toDelete.addAll(grammar.allEpsilonRules());
		if (toDelete.size() > 0)
		{
			diff1 = true;
		}
		int counter;
		for (ContextFreeRule rule : grammar.getRules())
		{
			counter = 0;
			for (Symbol s : rule.getSymbolsOnRightSide())
			{
				if (s.isNonTerminal() && result.getReachedIn().containsKey((NonTerminal) s))
				{
					counter++;
				}
			}
			if (counter > 1)
			{
				diff4 = true;
				if (counter == rule.getRightSymbolsCount())
				{
					diff5 = true;
					break;
				}
			}
		}
		addedAndDelRulesCount += toDelete.size();
		grammar.deleteRulesAutoDeleteSymbols(toDelete);
		Symbol[] newRightSide;
		Set<ContextFreeRule> toProcess = new HashSet<ContextFreeRule>();
		Set<ContextFreeRule> variants = new HashSet<ContextFreeRule>();
		Set<ContextFreeRule> newVariants = new HashSet<ContextFreeRule>();
		boolean found;
		int j;

		for (NonTerminal n : result.getReachedIn().keySet())
		{
			toProcess.addAll(grammar.allRulesWithSymbolOnRight(n));
			for (ContextFreeRule r : toProcess)
			{
				variants.clear();
				variants.add(r);
				newVariants.clear();
				for (int i = r.getRightSymbolsCount(); i > 0; i--)
				{
					if (r.getRightSideSymbolAt(i).equals(n))
					{
						for (ContextFreeRule r2 : variants)
						{
							newRightSide = new Symbol[r2.getRightSymbolsCount() - 1];
							j = 1;
							found = false;
							for (Symbol s : r2.getSymbolsOnRightSide())
							{
								if (j == i && !found)
								{
									found = true;
								}
								else
								{
									newRightSide[j - 1] = s;
									j++;
								}
							}
							if (newRightSide.length > 0)
							{
								newVariants.add(new SimpleContextFreeRule(r2.getSymbolOnLeftSide(), newRightSide));
								diff3 = true;
							}
						}
						variants.addAll(newVariants);
						newVariants.clear();
					}
				}
				grammar.addRulesAutoAddSymbols(variants);
				addedAndDelRulesCount = addedAndDelRulesCount + variants.size();
			}
		}

		List<NonTerminal> newOrder = new SetList<NonTerminal>();
		if (result.getReachedIn().containsKey(grammar.getStartSymbol()))
		{
			NonTerminal newS = SymbolManager.getNewN(grammar, grammar.getStartSymbol());
			Symbol[] right1 = { grammar.getStartSymbol() };
			ContextFreeRule r1 = new SimpleContextFreeRule(newS, right1);
			Symbol[] right2 = { Symbol.EPSILON };
			ContextFreeRule r2 = new SimpleContextFreeRule(newS, right2);
			grammar.setStartSymbol(newS);
			grammar.addRuleAutoAddSymbols(r1);
			grammar.addRuleAutoAddSymbols(r2);
			newOrder.add(newS);
		}

		for (NonTerminal n : order)
		{
			if (grammar.nonTerminalExists(n) && !grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				newOrder.add(n);
			}
		}

		for (Integer steps : result.getReachedIn().values())
		{
			if (steps > 0)
			{
				diff2 = true;
				break;
			}
		}
		int diff = 0;
		if (diff1)
		{
			diff++;
		}
		if (diff2)
		{
			diff++;
		}
		if (diff3)
		{
			diff++;
		}
		if (diff4)
		{
			diff++;
		}
		if (diff5)
		{
			diff++;
		}

		RunResult finalResult = new RunResult(grammar, diff, newOrder);
		finalResult.setMetric(Metric.CYCLE_COUNT, result.getMetric(Metric.CYCLE_COUNT));
		finalResult.setMetric(Metric.RULES_DELETED, toDelete.size());
		finalResult.setMetric(Metric.RULES_ADDED, addedAndDelRulesCount - toDelete.size());
		finalResult.setMetric(Metric.DIFF, diff);
		return finalResult;
	}
}
