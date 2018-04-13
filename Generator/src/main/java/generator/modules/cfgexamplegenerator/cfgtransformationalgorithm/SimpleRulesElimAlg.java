/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SimpleContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimpleRulesElimAlg extends AbstractCFGTransformationAlgorithm
{

	private static final Form[] req = { Form.NO_EPSILON };
	private static final Form[] sure = { Form.NO_EPSILON, Form.NO_SIMPLE_RULES, Form.NOT_CYCLIC };
	public static final SimpleRulesElimAlg INSTANCE = new SimpleRulesElimAlg();

	public static SimpleRulesElimAlg getInstance()
	{
		return INSTANCE;
	}

	private SimpleRulesElimAlg()
	{
		super(req, sure);
	}

	@Override
	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
		throws TooManyRulesException
	{
		nullTest(grammar);

		int deleted = 0;
		int added = 0;
		int iterations = 0;
		boolean diff1 = false;// if there is a unit production in the set of rules
		boolean diff2 = false;// if there is A, B, C such that A -> B, B -> C, |{A, B, C}| = 3
		boolean diff3 = false;// if number of rules added and deleted during algorithm is at least 10
		boolean diff4 = false;// if number of rules added and deleted during algorithm is at least 15
		boolean diff5 = false;// if number of rules added and deleted during algorithm is at least 20

		Set<ContextFreeRule> simpleRules = new HashSet<ContextFreeRule>(grammar.allSimpleRules());
		if (!simpleRules.isEmpty())
		{
			diff1 = true;
		}
		Map<NonTerminal, Set<NonTerminal>> derivableNonTerminals = grammar.getMapOfDerivableNonTerminals(simpleRules);
		// difficulty counting
		for (NonTerminal n : order)
		{
			derivableNonTerminals.get(n).remove(n);
			for (ContextFreeRule r : grammar.allRulesWithSymbolOnLeft(n))
			{
				if (r.isSimpleRule())
				{
					derivableNonTerminals.get(n).remove((NonTerminal) r.getFirstRightSymbol());
				}
			}
			if (!derivableNonTerminals.get(n).isEmpty())
			{
				diff2 = true;
				break;
			}
		}//

		derivableNonTerminals = grammar.getMapOfDerivableNonTerminals(simpleRules);
		deleted = simpleRules.size();
		grammar.deleteRulesAutoDeleteSymbols(simpleRules);
		int rulesSizeBeforeAdding = grammar.getRules().size();

		for (NonTerminal n : derivableNonTerminals.keySet())
		{
			derivableNonTerminals.get(n).remove(n);
			if (!derivableNonTerminals.get(n).isEmpty())
			{
				iterations++;
				for (NonTerminal right : derivableNonTerminals.get(n))
				{
					for (ContextFreeRule r : grammar.allRulesWithSymbolOnLeft(right))
					{
						grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(n, r.getSymbolsOnRightSide()));
					}
				}
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

		added = grammar.getRules().size() - rulesSizeBeforeAdding;

		if (added + deleted >= 10)
		{
			diff3 = true;
		}
		if (added + deleted >= 15)
		{
			diff4 = true;
		}
		if (added + deleted >= 20)
		{
			diff5 = true;
		}
		int diff = 0;
		if (diff1)
		{
			diff++;
		}
		if (diff2)
		{
			diff++;
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
		}

		RunResult result = new RunResult(grammar, diff, newOrder);
		result.setMetric(Metric.CYCLE_COUNT, iterations);
		result.setMetric(Metric.RULES_ADDED, added);
		result.setMetric(Metric.RULES_DELETED, deleted);
		result.setMetric(Metric.DIFF, diff);

		return result;

	}
}
