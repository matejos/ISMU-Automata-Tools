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
public class GNFCreationAlg extends AbstractCFGTransformationAlgorithm
{

	// private static final Form[] req = {Form.NO_EPSILON, Form.NO_LEFT_RECURSION};
	private static final Form[] req = { Form.NO_EPSILON, Form.NO_LEFT_RECURSION };
	private static final Form[] sure = { Form.NO_EPSILON, Form.GNF };
	public static final GNFCreationAlg INSTANCE = new GNFCreationAlg();

	public static GNFCreationAlg getInstance()
	{
		return INSTANCE;
	}

	private GNFCreationAlg()
	{
		super(req, sure);
	}

	// changes "replacements"
	protected static NonTerminal getNewRightSideNonTerminal(ContextFreeGrammar<ContextFreeRule> grammar,
		Map<Terminal, NonTerminal> replacements, Symbol original)
	{
		nullTest(original);

		NonTerminal result;
		if (original.isTerminal())
		{
			if (!replacements.containsKey((Terminal) original))
			{
				result = SymbolManager.getNewN(grammar, (Terminal) original);
				replacements.put((Terminal) original, result);
			}
			else
			{
				result = replacements.get((Terminal) original);
			}
		}
		else if (original.isNonTerminal())
		{
			result = (NonTerminal) original;
		}
		else
		{
			throw new InternalError(
				"symbol "
					+ original
					+ " in grammar that is input of GNF Creation algoritm is symbol which is neither terminal nor nonterminal");
		}
		return result;
	}

	private static int addRulesFromReplacements(ContextFreeGrammar<ContextFreeRule> grammar,
		Map<Terminal, NonTerminal> replacements) throws TooManyRulesException
	{
		int result = 0;
		Symbol[] newRightSide;
		for (Terminal t : replacements.keySet())
		{
			newRightSide = new Symbol[1];
			newRightSide[0] = t;
			grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(replacements.get(t), newRightSide));
			result++;
		}
		return result;
	}

	public RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, NonTerminal[] order)
		throws TooManyRulesException
	{
		nullTest(grammar);
		nullTest(order);

		int added = 0;
		int deleted = 0;
		// int substituted = 0;//
		int cycleCount = 0;
		int nonTerAdded = 0;
		int replacementsCount;
		boolean diff1 = false;// if |N' \ N| > 0
		boolean diff2 = false;// if there is a rule A -> B..., where ... \in alphabet*
		boolean diff3 = false;// if number of rules added and deleted during algorithm is at least 15
		boolean diff4 = false;// if number of rules added and deleted during algorithm is at least 35
		boolean diff5 = false;// if number of rules added and deleted during algorithm is at least 55

		int p = 0;

		/*
		 * //docasne spravene takto, pretoze vznika problem, ked je prvy symbol na pravej strane //pravidla neterminal,
		 * ktory nie je v "order" (neexistuje pren pravidlo) NonTerminal[] newOrd = new
		 * NonTerminal[grammar.getNonTerminals().size()]; System.arraycopy(order, 0, newOrd, 0, order.length); Set s =
		 * new HashSet<NonTerminal>(Arrays.asList(order)); int ii = order.length - 1; for (NonTerminal n :
		 * grammar.getNonTerminals()) { if (!s.contains(n)) { ii++; newOrd[ii] = n; } } List<NonTerminal> sorted =
		 * LeftRecursionElimAlg.getSortedOrder(grammar, newOrd); for (NonTerminal n : sorted) { if (s.contains(n)) {
		 * order[p] = n; p++; } }
		 */

		List<NonTerminal> sorted = LeftRecursionElimAlg.getSortedOrder(grammar, order);
		for (NonTerminal n : sorted)
		{
			order[p] = n;
			p++;
		}

		Set<NonTerminal> inCorrectForm = new HashSet<NonTerminal>();
		List<NonTerminal> newOrder = new SetList<NonTerminal>();
		boolean replace;

		// if order is empty array then inCorrect form will not be used anyway
		if (order.length > 0)
		{
			inCorrectForm.add(order[order.length - 1]);
		}
		Set<ContextFreeRule> toAdd = new HashSet<ContextFreeRule>();

		Symbol[] newRightSide;
		int copyIndex;
		Map<Terminal, NonTerminal> replacemets = new HashMap<Terminal, NonTerminal>();
		Set<ContextFreeRule> helpRuleSet = new HashSet<ContextFreeRule>();

		// if order is empty then prevent index out of bounds exception and don't run cycle
		if (order.length > 0)
		{
			for (int i = order.length - 1; i >= 0; i--)
			{
				helpRuleSet.clear();
				helpRuleSet.addAll(grammar.allRulesWithSymbolOnLeft(order[i]));
				if (!helpRuleSet.isEmpty())
				{
					newOrder.add(0, order[i]);
				}
				for (ContextFreeRule ir : helpRuleSet)
				{
					if ((ir.getFirstRightSymbol()).isNonTerminal())
					{
						for (ContextFreeRule jr : grammar.allRulesWithSymbolOnLeft((NonTerminal) ir
							.getFirstRightSymbol()))
						{
							cycleCount++;
							copyIndex = 1;
							newRightSide = new Symbol[jr.getRightSymbolsCount() + ir.getRightSymbolsCount() - 1];
							newRightSide[0] = jr.getFirstRightSymbol();
							for (int k = 1; k < jr.getRightSymbolsCount(); k++)
							{
								newRightSide[copyIndex] = getNewRightSideNonTerminal(grammar, replacemets,
									jr.getRightSideSymbolAt(k + 1));
								copyIndex++;
							}
							for (int k = 1; k < ir.getRightSymbolsCount(); k++)
							{
								newRightSide[copyIndex] = getNewRightSideNonTerminal(grammar, replacemets,
									ir.getRightSideSymbolAt(k + 1));
								copyIndex++;
							}
							toAdd.add(new SimpleContextFreeRule(order[i], newRightSide));
							added++;
						}
						grammar.addRulesAutoAddSymbols(toAdd);
						toAdd.clear();
						grammar.deleteRuleAutoDeleteSymbols(ir);
						deleted++;
					}
					else
					{
						replace = false;
						newRightSide = new Symbol[ir.getRightSymbolsCount()];
						newRightSide[0] = ir.getRightSideSymbolAt(1);
						for (int k = 2; k <= ir.getRightSymbolsCount(); k++)
						{
							if (ir.getRightSideSymbolAt(k).isTerminal())
							{
								replace = true;
							}
							newRightSide[k - 1] = getNewRightSideNonTerminal(grammar, replacemets,
								ir.getRightSideSymbolAt(k));
						}
						if (replace)
						{
							grammar.deleteRuleAutoDeleteSymbols(ir);
							grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(ir.getSymbolOnLeftSide(),
								newRightSide));
							// substituted++;
						}
					}
				}
			}
		}
		Set<NonTerminal> todel = new HashSet<NonTerminal>();
		for (NonTerminal n : newOrder)
		{
			if (!grammar.nonTerminalExists(n) || grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				todel.add(n);
			}
		}
		newOrder.removeAll(todel);

		// add new nonterminals to order
		for (Terminal t : replacemets.keySet())
		{
			newOrder.add(replacemets.get(t));
		}

		replacementsCount = addRulesFromReplacements(grammar, replacemets);
		nonTerAdded += replacementsCount;
		added += replacementsCount;
		if (nonTerAdded > 0)
		{
			diff1 = true;
		}
		if (deleted > 0)
		{
			diff2 = true;
		}
		if (added + deleted >= 15)
		{
			diff3 = true;
		}
		if (added + deleted >= 35)
		{
			diff4 = true;
		}
		if (added + deleted >= 55)
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
		result.setMetric(Metric.CYCLE_COUNT, cycleCount);
		added = added + replacementsCount;
		result.setMetric(Metric.RULES_ADDED, added);
		result.setMetric(Metric.RULES_DELETED, deleted);
		result.setMetric(Metric.NON_TERMINALS_ADDED, nonTerAdded);
		result.setMetric(Metric.DIFF, diff);

		return result;
	}

	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
		throws TooManyRulesException
	{
		nullTest(grammar);

		NonTerminal[] order2 = new NonTerminal[order.size()];
		int i = 0;
		for (NonTerminal n : order)
		{
			order2[i] = n;
			i++;
		}

		return processAlgorithm(grammar, order2);
	}
}
