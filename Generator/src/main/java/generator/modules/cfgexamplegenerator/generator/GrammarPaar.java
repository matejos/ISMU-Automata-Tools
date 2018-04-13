/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import java.util.List;

/**
 * @author drasto
 */
@SuppressWarnings({ "rawtypes" })
public class GrammarPaar<T extends ContextFreeGrammar>
{

	private T input;
	private T output;
	private List<NonTerminal> inputOrder;
	private List<NonTerminal> outputOrder;

	public GrammarPaar(T input, List<NonTerminal> inputOrder, T output, List<NonTerminal> outputOrder)
	{
		if (input == null)
		{
			throw new NullPointerException("input");
		}
		if (output == null)
		{
			throw new NullPointerException("output");
		}
		if (inputOrder == null)
		{
			throw new NullPointerException("inputOrder");
		}
		if (outputOrder == null)
		{
			throw new NullPointerException("outputOrder");
		}

		this.input = input;
		this.output = output;
		this.inputOrder = inputOrder;
		this.outputOrder = outputOrder;
	}

	public T getInput()
	{
		return input;
	}

	public T getOutput()
	{
		return output;
	}

	public List<NonTerminal> getInputOrder()
	{
		return inputOrder;
	}

	public List<NonTerminal> getOutputOrder()
	{
		return outputOrder;
	}
}
