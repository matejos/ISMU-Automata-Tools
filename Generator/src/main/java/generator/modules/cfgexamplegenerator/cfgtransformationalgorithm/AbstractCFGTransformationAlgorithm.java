/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech Republik
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractCFGTransformationAlgorithm implements
	CFGTransformationAlgorithm<ContextFreeGrammar<ContextFreeRule>>
{

	private Set<Form> lastCheckMissing = new HashSet<Form>();
	private Set<Form> requirements = new HashSet<Form>();
	private Set<Form> sureResultAttributes = new HashSet<Form>();

	protected AbstractCFGTransformationAlgorithm(Form[] requirements, Form[] sureResultAttributes)
	{
		nullTest(requirements);
		nullTest(sureResultAttributes);

		for (Form f : requirements)
		{
			this.requirements.add(f);
		}
		for (Form f : sureResultAttributes)
		{
			this.sureResultAttributes.add(f);
		}
	}

	protected static void nullTest(Object obj)
	{
		if (obj == null)
		{
			throw new NullPointerException("obj");
		}
	}

	public RunResult<ContextFreeGrammar<ContextFreeRule>> process(ContextFreeGrammar<ContextFreeRule> original,
		List<NonTerminal> order, Set<Form> attributes) throws RequirementsNotFulfilledException, TooManyRulesException
	{
		nullTest(original);
		nullTest(attributes);
		nullTest(order);

		Set<NonTerminal> helpSet = new HashSet<NonTerminal>();

		for (NonTerminal n : original.getNonTerminals())
		{
			if (!original.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				helpSet.add(n);
			}
		}

		Set<NonTerminal> needless = new HashSet<NonTerminal>();
		for (NonTerminal n : order)
		{
			if (!helpSet.remove(n))
			{
				needless.add(n);
				// throw new InternalError("order contains nonterminal " + n + " which is not nonterminal of grammar " +
				// original + ". " +
				// "This happen during execution of algorithm " + this);
			}
		}
		order.removeAll(needless);
		if (!helpSet.isEmpty())
		{
			throw new InternalError("there is nonterminal in grammar " + original + " which is not contained in order.");
		}
		if (!isCorrectForm(attributes, original))
		{
			String missing = "";
			for (Form f : lastCheckMissingRequirements())
			{
				missing = missing + f.toString() + ", ";
			}
			throw new RequirementsNotFulfilledException("These requirements not fulfilled: " + missing);
		}
		original = original.clone();
		RunResult<ContextFreeGrammar<ContextFreeRule>> result = processAlgorithm(original, order);
		return result;
	}

	public boolean isCorrectForm(Set<Form> attributes, ContextFreeGrammar<ContextFreeRule> gram)
	{
		nullTest(attributes);
		boolean result = true;
		lastCheckMissing = new HashSet<Form>();
		for (Form f : getRequirements())
		{
			if (!attributes.contains(f))
			{
				lastCheckMissing.add(f);
				result = false;
			}
		}
		if (result == false)
		{
			return result;
		}
		if (doSimpleCheckings(gram).isEmpty())
		{
			return result;
		}
		else
		{
			String message = "";
			for (Form f : doSimpleCheckings(gram))
			{
				message = message + ", " + f.toString();
			}
			throw new IllegalStateException("grammar: \n" + gram + "\ndeclares to fulfill all"
				+ " requirements but it does not. The requirements that should be filled are:\n" + message);
		}
	}

	public Set<Form> lastCheckMissingRequirements()
	{
		return Collections.unmodifiableSet(lastCheckMissing);
	}

	public Set<Form> sureResultAttributes()
	{
		return Collections.unmodifiableSet(sureResultAttributes);
	}

	protected abstract RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> gramar, List<NonTerminal> order)
		throws TooManyRulesException;

	protected Set<Form> doSimpleCheckings(ContextFreeGrammar<ContextFreeRule> grammar)
	{
		nullTest(grammar);
		Set<Form> result = new HashSet<Form>();
		if (requirements.contains(Form.NO_EPSILON))
		{
			for (ContextFreeRule r : grammar.allEpsilonRules())
			{
				if (!r.getSymbolOnLeftSide().equals(grammar.getStartSymbol()))
				{
					result.add(Form.NO_EPSILON);
				}
			}
		}
		if (requirements.contains(Form.NO_SIMPLE_RULES) && grammar.hasSimpleRules())
		{
			result.add(Form.NO_SIMPLE_RULES);
		}
		return result;
	}

	public Set<Form> getRequirements()
	{
		return Collections.unmodifiableSet(requirements);
	}

}
