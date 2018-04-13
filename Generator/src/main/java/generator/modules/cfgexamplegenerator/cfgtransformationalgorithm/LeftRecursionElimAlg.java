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
import java.util.ArrayList;
import java.util.Arrays;
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
public class LeftRecursionElimAlg extends AbstractCFGTransformationAlgorithm
{

	// private static final Form[] req = {Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.NOT_CYCLIC};
	private static final Form[] req = { Form.NO_EPSILON, Form.NOT_CYCLIC };
	// private static final Form[] sure = {Form.NO_NONGENERATING, Form.NO_EPSILON, Form.NOT_CYCLIC,
	// Form.NO_LEFT_RECURSION};
	private static final Form[] sure = { Form.NO_EPSILON, Form.NOT_CYCLIC, Form.NO_LEFT_RECURSION };
	public static final LeftRecursionElimAlg INSTANCE = new LeftRecursionElimAlg();

	public static LeftRecursionElimAlg getInstance()
	{
		return INSTANCE;
	}

	private LeftRecursionElimAlg()
	{
		super(req, sure);
	}

	private RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, NonTerminal[] order)
		throws TooManyRulesException
	{
		nullTest(grammar);
		nullTest(order);

		//
		// ContextFreeGrammar<ContextFreeRule> grammarBefore = grammar.clone();
		//

		int addedRules = 0;
		int deletedRules = 0;
		int cycleCount = 0;
		int addedNonTerminals = 0;
		boolean diff1 = false;// if |N' \ N| > 0
		boolean diff2 = false;// if there is a "cycle" in grammar of length > 1
		boolean diff3 = false;// if number of rules deleted during algorithm is at least 5
		boolean diff4 = false;// if number of rules added during algorithm is at least 25
		boolean diff5 = false;// if |P'| - |P| >= 40

		List<NonTerminal> newOrder = new SetList<NonTerminal>();

		Symbol[] newRightSide;
		Symbol[] newRightSide1;
		Symbol[] newRightSide2;
		int copyIndex;
		NonTerminal newISymbol;
		for (NonTerminal n : order)
		{
			if (n == null)
			{
				throw new IllegalStateException("here");
			}
		}

		if (hasCycle(grammar))
		{
			diff2 = true;
		}

		// order change
		int p = 0;
		List<NonTerminal> sorted = getSortedOrder(grammar, order);
		for (NonTerminal n : sorted)
		{
			order[p] = n;
			p++;
		}

		Set<ContextFreeRule> helpSet = new HashSet<ContextFreeRule>();
		for (int i = 0; i < order.length; i++)
		{

			for (int j = 0; j < i; j++)
			{
				helpSet.clear();
				helpSet.addAll(grammar.allRulesWithSymbolOnLeft(order[i]));
				for (ContextFreeRule ir : helpSet)
				{
					if (ir.getFirstRightSymbol().equals(order[j]))
					{
						cycleCount++;
						for (ContextFreeRule jr : grammar.allRulesWithSymbolOnLeft(order[j]))
						{
							copyIndex = 0;
							newRightSide = new Symbol[jr.getRightSymbolsCount() + ir.getRightSymbolsCount() - 1];
							for (int l = 0; l < jr.getRightSymbolsCount(); l++)
							{
								newRightSide[copyIndex] = jr.getRightSideSymbolAt(l + 1);
								copyIndex++;
							}
							for (int l = 1; l < ir.getRightSymbolsCount(); l++)
							{
								newRightSide[copyIndex] = ir.getRightSideSymbolAt(l + 1);
								copyIndex++;
							}
							grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(order[i], newRightSide));
							addedRules++;
						}
						grammar.deleteRuleAutoDeleteSymbols(ir);
						deletedRules++;
					}
				}
			}

			newISymbol = null;
			helpSet.clear();
			helpSet.addAll(grammar.allRulesWithSymbolOnLeft(order[i]));
			for (ContextFreeRule r : helpSet)
			{
				if (r.getFirstRightSymbol().equals(order[i]))
				{
					// if (r.getRightSymbolsCount() == 1) {
					// throw new IllegalArgumentException("grammar must not be cyclic");
					// }
					if (newISymbol == null)
					{
						newISymbol = SymbolManager.getNewN(grammar, order[i]);
						newOrder.add(newISymbol);
						addedNonTerminals++;
					}

					newRightSide1 = new Symbol[r.getRightSymbolsCount()];
					newRightSide2 = new Symbol[r.getRightSymbolsCount() - 1];
					for (int k = 1; k < r.getRightSymbolsCount(); k++)
					{
						newRightSide1[k - 1] = r.getRightSideSymbolAt(k + 1);
						newRightSide2[k - 1] = r.getRightSideSymbolAt(k + 1);
					}

					newRightSide1[r.getRightSymbolsCount() - 1] = newISymbol;
					grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(newISymbol, newRightSide1));
					grammar.addRuleAutoAddSymbols(new SimpleContextFreeRule(newISymbol, newRightSide2));
					addedRules++;
					addedRules++;
					grammar.deleteRuleAutoDeleteSymbols(r);
					deletedRules++;
				}
			}
			if (newISymbol != null)
			{
				helpSet.clear();
				for (ContextFreeRule r : grammar.allRulesWithSymbolOnLeft(order[i]))
				{
					// if(!r.getFirstRightSymbol().equals(order[i])) such condition here would be useless because all
					// rules that do not fulfill it
					// has been deleted in previous for cycle
					newRightSide = new Symbol[r.getRightSymbolsCount() + 1];
					for (int l = 0; l < r.getRightSymbolsCount(); l++)
					{
						newRightSide[l] = r.getRightSideSymbolAt(l + 1);
					}
					newRightSide[r.getRightSymbolsCount()] = newISymbol;
					helpSet.add(new SimpleContextFreeRule(order[i], newRightSide));
					addedRules++;
				}
				grammar.addRulesAutoAddSymbols(helpSet);
			}
			newOrder.add(order[i]);
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

		if (addedNonTerminals > 0)
		{
			diff1 = true;
		}
		if (deletedRules >= 5)
		{
			diff3 = true;
		}
		if (addedRules >= 25)
		{
			diff4 = true;
		}
		if (addedRules + deletedRules >= 50)
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
		result.setMetric(Metric.RULES_ADDED, addedRules);
		result.setMetric(Metric.RULES_DELETED, deletedRules);
		result.setMetric(Metric.NON_TERMINALS_ADDED, addedNonTerminals);
		result.setMetric(Metric.CYCLE_COUNT, cycleCount);
		result.setMetric(Metric.DIFF, diff);

		return result;
	}

	private static Set<NonTerminal> getElemSCC(Set<Set<NonTerminal>> set, Map<NonTerminal, Integer> nonTsOrder)
	{
		nullTest(set);
		if (set.isEmpty())
		{
			throw new RuntimeException("This set doesn't contain any element");
		}
		Set<NonTerminal> elem = null;
		for (Set<NonTerminal> comp : set)
		{
			if (elem == null)
			{
				elem = comp;
			}
			else
			{
				if (nonTsOrder.get(getElemNonT(elem, nonTsOrder)) > nonTsOrder.get(getElemNonT(comp, nonTsOrder)))
				{
					elem = comp;
				}
			}
		}
		return elem;
	}

	// each NonT in 'set' has to be contained in 'nonTsOrder'
	private static NonTerminal getElemNonT(Set<NonTerminal> set, Map<NonTerminal, Integer> nonTsOrder)
	{
		nullTest(set);
		if (set.isEmpty())
		{
			throw new RuntimeException("This set doesn't contain any element");
		}

		NonTerminal elem = null;
		for (NonTerminal nonT : set)
		{
			if (elem == null)
			{
				elem = nonT;
			}
			else
			{
				if (nonTsOrder.get(elem) > nonTsOrder.get(nonT))
				{
					elem = nonT;
				}
			}
		}
		return elem;
	}

	// this method changes parameter 'predecessors'
	private static List<NonTerminal> sortedCompNonTerminals(Set<NonTerminal> component,
		Map<NonTerminal, Set<NonTerminal>> successors, Map<NonTerminal, Set<NonTerminal>> predecessors,
		Map<NonTerminal, Integer> nonTsOrder)
	{
		Set<NonTerminal> comp = new HashSet<NonTerminal>(component);
		List<NonTerminal> sorted = new ArrayList<NonTerminal>();
		Set<NonTerminal> nonTsWithNoIncomEdges = new HashSet<NonTerminal>();
		for (NonTerminal nonT : comp)
		{
			if (predecessors.get(nonT).isEmpty())
			{
				nonTsWithNoIncomEdges.add(nonT);
			}
		}

		NonTerminal nonTToBeAdded;
		while (!comp.isEmpty())
		{
			if (nonTsWithNoIncomEdges.isEmpty())
			{
				nonTToBeAdded = getElemNonT(comp, nonTsOrder);
			}
			else
			{
				nonTToBeAdded = getElemNonT(nonTsWithNoIncomEdges, nonTsOrder);
			}
			comp.remove(nonTToBeAdded);
			nonTsWithNoIncomEdges.remove(nonTToBeAdded);
			sorted.add(nonTToBeAdded);
			for (NonTerminal successor : successors.get(nonTToBeAdded))
			{
				predecessors.get(successor).remove(nonTToBeAdded);
				if (comp.contains(successor) && predecessors.get(successor).isEmpty())
				{
					nonTsWithNoIncomEdges.add(successor);
				}
			}
		}
		return sorted;
	}

	public static List<NonTerminal> getSortedOrder(ContextFreeGrammar<ContextFreeRule> grammar, NonTerminal[] order)
	{
		nullTest(grammar);
		nullTest(order);
		for (NonTerminal n : order)
		{
			if (!grammar.getNonTerminals().contains(n))
			{
				throw new IllegalArgumentException("There is a nonterminal in the order which is not in the grammar");
			}
		}

		// hladanie cyklov (silne suvislych komponent)
		Map<NonTerminal, Set<NonTerminal>> derivableNonTerminals = grammar.getMapOfDerivableNonTerminals(grammar
			.getRules());
		Map<NonTerminal, Set<NonTerminal>> componentAssigning = new HashMap<NonTerminal, Set<NonTerminal>>();
		// componentAssigning - pre kazdy neterminal X (kluc) obsahuje silne suvislu komponentu, v ktorej sa X nachadza,
		// t. j. mnozinu vsetkych neterminalov Y t.z. X ->* Y... a Y ->* X...
		Set<Set<NonTerminal>> components = new HashSet<Set<NonTerminal>>();
		Set<NonTerminal> scc;
		for (NonTerminal n : order)
		{
			if (!componentAssigning.containsKey(n))
			{
				scc = new HashSet<NonTerminal>();
				scc.add(n);
				if (derivableNonTerminals.get(n).contains(n))
				{
					for (NonTerminal r : derivableNonTerminals.get(n))
					{
						if (derivableNonTerminals.get(r).contains(n))
						{
							scc.add(r);
						}
					}
				}
				for (NonTerminal nonT : scc)
				{
					componentAssigning.put(nonT, scc);
				}
				components.add(scc);
			}
		}

		// tvorba grafu - smycky neuvazujeme
		Map<NonTerminal, Set<NonTerminal>> successors = new HashMap<NonTerminal, Set<NonTerminal>>();
		Map<NonTerminal, Set<NonTerminal>> predecessors = new HashMap<NonTerminal, Set<NonTerminal>>();
		// successors - pre kazdy neterminal X (kluc) obsahuje neterminal Y, prave ak ex. pravidlo X -> Y... a X != Y
		// predecessors - pre kazdy neterminal Y (kluc) obsahuje neterminal X, prave ak ex. pravidlo X -> Y... a X != Y
		Map<Set<NonTerminal>, Set<Set<NonTerminal>>> succSCC = new HashMap<Set<NonTerminal>, Set<Set<NonTerminal>>>();
		Map<Set<NonTerminal>, Set<Set<NonTerminal>>> predSCC = new HashMap<Set<NonTerminal>, Set<Set<NonTerminal>>>();

		for (NonTerminal n : order)
		{
			successors.put(n, new HashSet<NonTerminal>());
			predecessors.put(n, new HashSet<NonTerminal>());
		}
		for (Set<NonTerminal> c : components)
		{
			succSCC.put(c, new HashSet<Set<NonTerminal>>());
			predSCC.put(c, new HashSet<Set<NonTerminal>>());
		}

		Set<NonTerminal> orderSet = new HashSet<NonTerminal>();
		orderSet.addAll(Arrays.asList(order));
		for (NonTerminal n : order)
		{
			for (ContextFreeRule r : grammar.allRulesWithSymbolOnLeft(n))
			{
				if (r.getFirstRightSymbol().isNonTerminal() && !r.getFirstRightSymbol().equals(n))
				{
					// smycky neuvazujeme
					if (!orderSet.contains((NonTerminal) r.getFirstRightSymbol()))
					{
						// ak pre prvy symbol na PS neexistuje prepisovacie pravidlo (nie je v 'order'), je ignorovany,
						// pretoze tato metoda ma usporiadat len neterminaly v 'order'
						// throw new IllegalArgumentException("symbol on right side of rule which is not in order");
					}
					else
					{
						successors.get(n).add((NonTerminal) r.getFirstRightSymbol());
						predecessors.get((NonTerminal) r.getFirstRightSymbol()).add(n);
						if (!componentAssigning.get(n).contains((NonTerminal) r.getFirstRightSymbol()))
						{
							// ak hrana nie je medzi vrcholmi tej istej SCC
							succSCC.get(componentAssigning.get(n)).add(
								componentAssigning.get((NonTerminal) r.getFirstRightSymbol()));
							predSCC.get(componentAssigning.get((NonTerminal) r.getFirstRightSymbol())).add(
								componentAssigning.get(n));
						}
					}
				}
			}
		}

		// topologicke usporiadanie silne suvislych komponent
		List<Set<NonTerminal>> sortedSCC = new ArrayList<Set<NonTerminal>>();
		Set<Set<NonTerminal>> compsWithNoIncomEdges = new HashSet<Set<NonTerminal>>();
		// for (Set<NonTerminal> comp : predSCC.keySet()) {
		for (Set<NonTerminal> comp : components)
		{
			if (predSCC.get(comp).isEmpty())
			{
				compsWithNoIncomEdges.add(comp);
			}
		}

		Map<NonTerminal, Integer> nonTsOrder = new HashMap<NonTerminal, Integer>();
		for (int i = 0; i < order.length; i++)
		{
			nonTsOrder.put(order[i], i);
		}
		Set<NonTerminal> elemComp;
		while (!compsWithNoIncomEdges.isEmpty())
		{
			elemComp = getElemSCC(compsWithNoIncomEdges, nonTsOrder);
			compsWithNoIncomEdges.remove(elemComp);
			sortedSCC.add(elemComp);
			for (Set<NonTerminal> successor : succSCC.get(elemComp))
			{
				predSCC.get(successor).remove(elemComp);
				if (predSCC.get(successor).isEmpty())
				{
					compsWithNoIncomEdges.add(successor);
				}
			}
		}

		// tato vynimka pojde casom prec
		if (sortedSCC.size() != components.size())
		{
			throw new IllegalStateException(
				"Size of list of sorted components does not equal to the size of all components");
		}

		// usporiadanie neterminalov
		List<NonTerminal> sorted = new ArrayList<NonTerminal>();
		for (Set<NonTerminal> comp : sortedSCC)
		{
			sorted.addAll(sortedCompNonTerminals(comp, successors, predecessors, nonTsOrder));
		}

		// tato vynimka pojde casom prec
		if (order.length != sorted.size())
		{
			throw new IllegalStateException("Size of old order does not equals to the size of new order");
		}

		return sorted;
	}

	/*
	 * Returns true iff there are nonterminals A, B such that A =>^+ B..., B =>^+ A..., A != B
	 */
	private boolean hasCycle(ContextFreeGrammar<ContextFreeRule> grammar)
	{
		boolean cycleDetected = false;
		Map<NonTerminal, Set<NonTerminal>> derivableNonTerminals = grammar.getMapOfDerivableNonTerminals(grammar
			.getRules());
		for (NonTerminal n : derivableNonTerminals.keySet())
		{
			if (derivableNonTerminals.get(n).contains(n))
			{
				for (NonTerminal r : derivableNonTerminals.get(n))
				{
					if (!r.equals(n) && derivableNonTerminals.get(r).contains(n))
					{
						cycleDetected = true;
						break;
					}
				}
			}
		}
		return cycleDetected;
	}

	@Override
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
