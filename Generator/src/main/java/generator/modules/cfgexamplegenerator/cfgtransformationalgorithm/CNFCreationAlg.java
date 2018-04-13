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
import generator.modules.cfgexamplegenerator.grammarreprezentation.Terminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.ArrayList;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CNFCreationAlg extends AbstractCFGTransformationAlgorithm
{

	// private static final Form[] req = {Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON,
	// Form.NO_SIMPLE_RULES};
	private static final Form[] req = { Form.NO_EPSILON, Form.NO_SIMPLE_RULES };
	private static final Form[] sure = { Form.NO_EPSILON, Form.CNF };
	private static final char SYMBOL_DIVIDER = '-';
	public static final CNFCreationAlg INSTANCE = new CNFCreationAlg();

	public static CNFCreationAlg getInstance()
	{
		return INSTANCE;
	}

	private CNFCreationAlg()
	{
		super(req, sure);
	}

	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
		throws TooManyRulesException
	{
		nullTest(grammar);

		int addedRul = 0;
		int addNonTer = 0;
		int delRules = 0;
		// int cycles = 0;
		int replacementsCount = 0;
		boolean diff1 = false;// if there is A -> X1...Xn, n > 1, there is "i": Xi is terminal
		boolean diff2 = false;// if there is A -> X1...Xn, n > 2
		boolean diff3 = false;// if |N' \ N| >= 5
		boolean diff4 = false;// if |N' \ N| >= 10
		boolean diff5 = false;// if |N' \ N| >= 15

		Set<ContextFreeRule> allRules = new HashSet<ContextFreeRule>();
		List<NonTerminal> newOrder = new SetList<NonTerminal>();
		Map<String, NonTerminal> addedRules = new HashMap<String, NonTerminal>();
		Map<Terminal, NonTerminal> addedNonTerminals = new HashMap<Terminal, NonTerminal>();
		List<Terminal> findOrder = new ArrayList<Terminal>();
		NonTerminal[] newRightSide;
		NonTerminal[] rightSide;
		NonTerminal[] rightSideHelp;
		String key;
		NonTerminal leftSide;
		boolean end;

		for (NonTerminal n : order)
		{
			allRules.clear();
			allRules.addAll(grammar.allRulesWithSymbolOnLeft(n));
			newOrder.add(n);
			for (ContextFreeRule r : allRules)
			{
				if (r.getRightSymbolsCount() == 2)
				{
					if (r.getFirstRightSymbol().isTerminal() || r.getRightSideSymbolAt(2).isTerminal())
					{
						newRightSide = new NonTerminal[2];
						if (r.getFirstRightSymbol().isTerminal()
							&& !addedNonTerminals.containsKey((Terminal) r.getFirstRightSymbol()))
						{
							findOrder.add((Terminal) r.getFirstRightSymbol());
						}
						newRightSide[0] = GNFCreationAlg.getNewRightSideNonTerminal(grammar, addedNonTerminals,
							r.getFirstRightSymbol());
						newOrder.add(newRightSide[0]);
						if (r.getRightSideSymbolAt(2).isTerminal()
							&& !addedNonTerminals.containsKey((Terminal) r.getRightSideSymbolAt(2)))
						{
							findOrder.add((Terminal) r.getRightSideSymbolAt(2));
						}
						newRightSide[1] = GNFCreationAlg.getNewRightSideNonTerminal(grammar, addedNonTerminals,
							r.getRightSideSymbolAt(2));
						newOrder.add(newRightSide[1]);
						grammar.deleteRuleAutoDeleteSymbols(r);
						delRules++;
						grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(r.getSymbolOnLeftSide(), newRightSide));
						addedRul++;
						// cycles++;
					}
				}
				else if (r.getRightSymbolsCount() > 2)
				{
					key = "";
					rightSide = new NonTerminal[r.getRightSymbolsCount()];

					for (int i = 1; i <= r.getRightSymbolsCount(); i++)
					{
						if (r.getRightSideSymbolAt(i).isTerminal()
							&& !addedNonTerminals.containsKey(r.getRightSideSymbolAt(i)))
						{
							findOrder.add((Terminal) r.getRightSideSymbolAt(i));
						}
						rightSide[i - 1] = GNFCreationAlg.getNewRightSideNonTerminal(grammar, addedNonTerminals,
							r.getRightSideSymbolAt(i));
						newOrder.add(rightSide[i - 1]);
						key = key + SYMBOL_DIVIDER + rightSide[i - 1].toString();
					}

					key = key.substring(key.indexOf(SYMBOL_DIVIDER) + 1);

					leftSide = r.getSymbolOnLeftSide();
					end = false;
					while (!end)
					{
						// cycles++;
						if (!addedRules.containsKey(key))
						{
							newRightSide = new NonTerminal[2];
							newRightSide[0] = rightSide[0];
							rightSideHelp = new NonTerminal[rightSide.length - 1];
							for (int k = 1; k < rightSide.length; k++)
							{
								rightSideHelp[k - 1] = rightSide[k];
							}
							newRightSide[1] = SymbolManager.getNewN(grammar, rightSideHelp);
							addNonTer++;

							grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(leftSide, newRightSide));
							newOrder.add(leftSide);

							addedRul++;

							addedRules.put(key, newRightSide[1]);
							key = key.substring(key.indexOf(SYMBOL_DIVIDER) + 1);

							if (rightSideHelp.length <= 2)
							{
								grammar
									.addRuleAutoAddSymbols(new SimpleContextFreeRule(newRightSide[1], rightSideHelp));
								newOrder.add(newRightSide[1]);
								addedRul++;
								end = true;
							}

							leftSide = newRightSide[1];
							rightSide = rightSideHelp;
						}
						else
						{
							newRightSide = new NonTerminal[2];
							newRightSide[0] = rightSide[0];
							newRightSide[1] = addedRules.get(key);
							grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(leftSide, newRightSide));
							newOrder.add(leftSide);
							addedRul++;
							end = true;
						}
					}
					grammar.deleteRuleAutoDeleteSymbols(r);
					delRules++;
				}
				else if (!r.isTerminalRule())
				{
					throw new InternalError(
						"rule "
							+ r
							+ " is from grammar with no epsilon and no simple rules, it is not terminal rule and right side"
							+ "is of length 1 or less");
				}
			}
		}

		Symbol[] newRS;
		for (Terminal t : findOrder)
		{
			newRS = new Symbol[1];
			newRS[0] = t;
			grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(addedNonTerminals.get(t), newRS));
			replacementsCount++;
		}
		if (replacementsCount > 0)
		{
			diff1 = true;
		}
		if (addNonTer > 0)
		{
			diff2 = true;
		}
		addNonTer = addNonTer + replacementsCount;
		addedRul = addedRul + replacementsCount;
		// if ((newOrder.size() - order.size()) >= 5) {
		if (addNonTer >= 5)
		{
			diff3 = true;
		}
		if (addNonTer >= 10)
		{
			diff4 = true;
		}
		if (addNonTer >= 15)
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
		}
		if (diff > 1)
		{
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
		result.setMetric(Metric.CYCLE_COUNT, delRules);
		result.setMetric(Metric.NON_TERMINALS_ADDED, addNonTer);
		result.setMetric(Metric.RULES_ADDED, addedRul);
		result.setMetric(Metric.RULES_DELETED, delRules);
		result.setMetric(Metric.DIFF, diff);

		return result;
	}
}
