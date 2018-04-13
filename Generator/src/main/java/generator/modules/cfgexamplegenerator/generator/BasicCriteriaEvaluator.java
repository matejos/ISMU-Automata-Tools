/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.CFGTransformationAlgorithm;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Metric;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.NongeneratingSymbolsElimAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.RequirementsNotFulfilledException;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.RunResult;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.UnreachableSymbolsElimAlg;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BasicCriteriaEvaluator implements CriteriaEvaluator
{

	private AlgorithmType[] toRunThrough;
	// private Set<SimpleCriteria> criter;
	private Map<AlgorithmType, Set<SimpleCriteria>> criterForForm = new HashMap<AlgorithmType, Set<SimpleCriteria>>();
	private List<GrammarOrderAndAlg> result = null;

	private Set<SimpleCriteria> lowerNotFulfilled = new HashSet<SimpleCriteria>();
	private Set<SimpleCriteria> upperNotFulfilled = new HashSet<SimpleCriteria>();
	private Set<String> allGeneratedGrammars;
	private boolean shouldContinue;

	public BasicCriteriaEvaluator(AlgorithmType[] toRunThrough, Set<SimpleCriteria> criter,
		Set<String> allGeneratedGrammars)
	{
		if (toRunThrough == null)
		{
			throw new NullPointerException("toRunThrough");
		}
		if (criter == null)
		{
			throw new NullPointerException("criter");
		}
		if (allGeneratedGrammars == null)
		{
			throw new NullPointerException("allGeneratedGrammars");
		}
		if (toRunThrough.length < 1)
		{
			throw new IllegalArgumentException(
				"toRunThroug must have at least one element otherwise there is no algorithm to apply");
		}
		AlgorithmType[] toAtribut = new AlgorithmType[toRunThrough.length + 1];
		toAtribut[0] = AlgorithmType.INPUT;
		int i = 1;
		for (AlgorithmType al : toRunThrough)
		{
			toAtribut[i] = al;
			i++;
		}
		toRunThrough = toAtribut;

		for (AlgorithmType g : toRunThrough)
		{
			if (!criterForForm.containsKey(g))
			{
				criterForForm.put(g, new HashSet<SimpleCriteria>());
			}
			for (SimpleCriteria c : criter)
			{
				if (c.getForm() == g)
				{
					criterForForm.get(g).add(c);
				}
			}
		}

		this.toRunThrough = toRunThrough;
		// this.criter = criter;
		this.allGeneratedGrammars = allGeneratedGrammars;
	}

	// prezenie gramatiku algoritmami a ak uspeje (splni obmedzenia), tak do "result" ulozi vysledok
	// "prepareResult(...)" s rovnakymi argumentami

	public boolean evaluate(final ContextFreeGrammar<ContextFreeRule> grammarToEvaluate,
		final Set<Form> grammarAttributes, final List<NonTerminal> nonTerminalsOrder)
		throws RequirementsNotFulfilledException
	{
		if (grammarToEvaluate == null)
		{
			throw new NullPointerException("grammarToEvaluate");
		}
		if (grammarAttributes == null)
		{
			throw new NullPointerException("grammarAttributes");
		}

		lowerNotFulfilled.clear();
		upperNotFulfilled.clear();

		ContextFreeGrammar<ContextFreeRule> resultGrammar = grammarToEvaluate.clone();
		Set<Form> resultAttributes = new HashSet<Form>();
		resultAttributes.addAll(grammarAttributes);
		List<NonTerminal> resultOrder = new ArrayList<NonTerminal>();
		resultOrder.addAll(nonTerminalsOrder);
		CFGTransformationAlgorithm toGet;
		RunResult<ContextFreeGrammar<ContextFreeRule>> runRes = null;
		Set<SimpleCriteria> lowerBad = new HashSet<SimpleCriteria>();

		boolean first = true;

		int i = 0;
		boolean stop = false;
		boolean succeeded = true;
		AlgorithmType g = null;
		while (i < toRunThrough.length && !stop)
		{
			g = toRunThrough[i];
			i++;

			toGet = g.getAlgorithm();
			if (!first)
			{
				try
				{
					runRes = toGet.process(resultGrammar, resultOrder, resultAttributes);
				}
				catch (TooManyRulesException ex)
				{
					shouldContinue = false;
					lowerNotFulfilled.clear();
					upperNotFulfilled.clear();
					return false;
				}
				resultAttributes.addAll(toGet.sureResultAttributes());
				// some attributes assured before alg. may not be sure now
				// therefore correctness depends on the order of algorithms
				resultOrder = runRes.getNewOrder();
				resultGrammar = runRes.getTransformedGrammar();
			}
			else
			{
				first = false;
			}

			for (SimpleCriteria c : criterForForm.get(g))
			{
				switch (c.getType())
				{
					case NONTERMINALS_COUNT:
						Set<NonTerminal> nonTerInGrm = new HashSet<NonTerminal>(resultGrammar.getNonTerminals());
						for (NonTerminal n : resultGrammar.getNonTerminals())
						{
							if (resultGrammar.allRulesWithSymbol(n) == null
								|| resultGrammar.allRulesWithSymbol(n).isEmpty())
							{
								nonTerInGrm.remove(n);
							}
						}
						c.setValue(nonTerInGrm.size());
						break;
					case RULES_COUNT:
						c.setValue(resultGrammar.getRules().size());
						break;
					case TERMINALS_COUNT:
						c.setValue(resultGrammar.getTerminals().size());
						break;
					case COST_OF_ALGORITHM:
						if (runRes == null)
						{
							throw new IllegalStateException("runRes is null => there is no cost of algorithm to set");
						}
						c.setValue(runRes.getCost());
						break;
					case CYCLES_COUNT:
						if (runRes == null)
						{
							throw new IllegalStateException(
								"runRes is null => there is no cycle count of algorithm to set");
						}
						c.setValue(runRes.getMetric(Metric.CYCLE_COUNT));
						break;
					case UNREACHABLE_SYMBOLS:
						try
						{
							ContextFreeGrammar helpGrammar = UnreachableSymbolsElimAlg.INSTANCE.process(resultGrammar,
								resultOrder, resultAttributes).getTransformedGrammar();
							if (helpGrammar.getRules().size() != resultGrammar.getRules().size())
							{
								c.setValue(1);
							}
							else
							{
								c.setValue(0);
							}
						}
						catch (TooManyRulesException ex)
						{
							return false;
						}
						break;
					case NONGENERATING_SYMBOLS:
						try
						{
							ContextFreeGrammar helpGrammar = NongeneratingSymbolsElimAlg.INSTANCE.process(
								resultGrammar, resultOrder, resultAttributes).getTransformedGrammar();
							if (helpGrammar.getRules().size() != resultGrammar.getRules().size())
							{
								c.setValue(1);
							}
							else
							{
								c.setValue(0);
							}
						}
						catch (TooManyRulesException ex)
						{
							return false;
						}
						break;
					case USELESS_NO_PUNISH:
						try
						{
							Set<Form> inputAttributes = new HashSet<Form>();
							inputAttributes.addAll(grammarAttributes);
							ContextFreeGrammar helpGrammar = grammarToEvaluate.clone();
							RunResult result1 = UnreachableSymbolsElimAlg.INSTANCE.process(helpGrammar,
								nonTerminalsOrder, inputAttributes);
							inputAttributes.addAll(UnreachableSymbolsElimAlg.INSTANCE.sureResultAttributes());
							helpGrammar = result1.getTransformedGrammar();
							RunResult result2 = NongeneratingSymbolsElimAlg.INSTANCE.process(helpGrammar,
								result1.getNewOrder(), inputAttributes);
							helpGrammar = result2.getTransformedGrammar();

							if (helpGrammar.getRules().size() == resultGrammar.getRules().size())
							{
								c.setValue(1);
							}
							else
							{
								c.setValue(0);
							}
						}
						catch (TooManyRulesException ex)
						{
							return false;
						}
						break;
					case NOT_SAME:
						if (allGeneratedGrammars.contains(resultGrammar.toString()))
						{
							c.setValue(0);
						}
						else
						{
							c.setValue(1);
						}
						break;
					case MORE_RULES_THEN_ALLOWED:
						c.setValue(resultGrammar.getRules().size());
						break;
					default:
						throw new IllegalStateException("there should not be any other criteria types");
				}
			}

			for (SimpleCriteria c : criterForForm.get(g))
			{
				stop = stop || (c.isCritical() && !c.isIsFulfilled());
				succeeded = succeeded && c.isIsFulfilled();
				if (!c.isIsFulfilled() && !c.isCritical())
				{
					lowerBad.add(c);
				}
			}

		}

		if (!succeeded)
		{
			for (SimpleCriteria c : criterForForm.get(g))
			{
				if (!c.isIsFulfilled() && c.isCritical())
				{
					upperNotFulfilled.add(c);
				}
			}
			lowerNotFulfilled.addAll(lowerBad);
			this.result = null;
		}
		else
		{
			this.result = prepareResult(grammarToEvaluate, grammarAttributes, nonTerminalsOrder);
		}

		shouldContinue = upperNotFulfilled.isEmpty();

		return succeeded;
	}

	public List<GrammarOrderAndAlg> getResult()
	{
		if (result == null)
		{
			throw new RuntimeException("result should be retrieved only when last call to evaluate returned true");
		}
		return result;
	}

	protected List<GrammarOrderAndAlg> prepareResult(final ContextFreeGrammar<ContextFreeRule> grammarToEvaluate,
		final Set<Form> grammarAttributes, final List<NonTerminal> nonTerminalsOrder)
		throws RequirementsNotFulfilledException
	{
		Set<Form> resultAttributes = new HashSet<Form>(grammarAttributes);
		RunResult<ContextFreeGrammar<ContextFreeRule>> runRes;
		List<GrammarOrderAndAlg> prepResult = new ArrayList<GrammarOrderAndAlg>();
		ContextFreeGrammar<ContextFreeRule> actualGrammar = grammarToEvaluate;
		List<NonTerminal> actualOrder = new ArrayList<NonTerminal>(nonTerminalsOrder);

		prepResult.add(new GrammarOrderAndAlg(grammarToEvaluate.clone(), nonTerminalsOrder, AlgorithmType.INPUT));
		boolean first = true;
		for (AlgorithmType al : toRunThrough)
		{
			if (!first)
			{
				CFGTransformationAlgorithm toGet = al.getAlgorithm();
				try
				{
					runRes = toGet.process(actualGrammar, actualOrder, resultAttributes);
				}
				catch (RequirementsNotFulfilledException ex)
				{
					Logger.getLogger(BasicCriteriaEvaluator.class.getName()).log(Level.SEVERE, null, ex);
					throw new RuntimeException("Requirements should be fulfilled if they were fulfilled first time", ex);
				}
				catch (TooManyRulesException ex)
				{
					throw new RuntimeException("Rules should not exceed bound if they didn't during evaluation");
				}
				resultAttributes.addAll(toGet.sureResultAttributes());
				actualOrder = runRes.getNewOrder();
				actualGrammar = runRes.getTransformedGrammar();
				prepResult.add(new GrammarOrderAndAlg(actualGrammar.clone(), actualOrder, al));
			}
			else
			{
				first = false;
			}
		}

		return prepResult;
	}

	public Set<SimpleCriteria> getLowerNotFulfilled()
	{
		return Collections.unmodifiableSet(lowerNotFulfilled);
	}

	public Set<SimpleCriteria> getUpperNotFulfilled()
	{
		return Collections.unmodifiableSet(upperNotFulfilled);
	}

	public boolean shouldTryToAddMoreRules()
	{
		return shouldContinue;
	}

}
