/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import java.util.List;

/**
 * @author drasto
 */
public class GrammarOrderAndAlg
{

	private ContextFreeGrammar<ContextFreeRule> grammar;
	private List<NonTerminal> order;
	private AlgorithmType alg;

	public GrammarOrderAndAlg(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order, AlgorithmType alg)
	{
		if (grammar == null)
		{
			throw new NullPointerException("grammar");
		}
		if (order == null)
		{
			throw new NullPointerException("order");
		}
		if (alg == null)
		{
			throw new NullPointerException("alg");
		}
		this.alg = alg;
		this.grammar = grammar;
		this.order = order;
	}

	public ContextFreeGrammar<ContextFreeRule> getGrammar()
	{
		return grammar;
	}

	public List<NonTerminal> getOrder()
	{
		return order;
	}

	public AlgorithmType getAlg()
	{
		return alg;
	}

	@Override
	public String toString()
	{
		return "GrammarOrderAndAlg [grammar=" + grammar + ", order=" + order + ", alg=" + alg + "]";
	}

}
