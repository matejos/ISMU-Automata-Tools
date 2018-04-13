/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SimpleContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Terminal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author drasto, bafco
 */
@SuppressWarnings("unchecked")
public class OneRuleGen
{

	private static final int nanoSecondsToChangeRandom = 100;
	private static Random generator = new Random();
	private static long created = System.nanoTime();
	private boolean noSimple;
	private List<Terminal> onlyOneRightTerminals = new ArrayList<Terminal>();
	private List<NonTerminal> onlyOneRightNonTerminals = new ArrayList<NonTerminal>();
	private Symbol eps = null;
	// private GeneratorSettings settings;

	public OneRuleGen(boolean noSimpleRules, List<Symbol> allowedOnlyOneRight, GeneratorSettings settings)
	{
		this.noSimple = noSimpleRules;
		if (allowedOnlyOneRight == null)
		{
			throw new NullPointerException("allowedOnlyOneRight");
		}
		if (allowedOnlyOneRight.size() <= 0)
		{
			throw new IllegalArgumentException("allowedOnlyOneRight.size() must be > 0");
		}
		if (settings == null)
		{
			throw new NullPointerException("settings");
		}

		for (Symbol s : allowedOnlyOneRight)
		{
			if (s.isTerminal())
			{
				onlyOneRightTerminals.add((Terminal) s);
			}
			else if (s.isNonTerminal())
			{
				onlyOneRightNonTerminals.add((NonTerminal) s);
			}
			else if (s.isEpsilon())
			{
				eps = s;
			}
			else
			{
				throw new RuntimeException("Symbol " + s
					+ " is not terminal neither nonterminal nor epsilon. What the hell is it ?");
			}
		}
		if (onlyOneRightTerminals.isEmpty())
		{
			throw new IllegalArgumentException("allowedOnlyOneRight didn't contain any terminal");
		}

		// this.settings = settings;
	}

	
	public ContextFreeRule makeRule(int length, List<NonTerminal> allowedLeft, List<Symbol>... allowedRight)
	{
		if (length < 1)
		{
			throw new IllegalArgumentException("lenght must be greater than zero");
		}
		if (allowedLeft == null)
		{
			throw new NullPointerException("allowedLeft");
		}
		if (allowedRight == null)
		{
			throw new NullPointerException("allowedRight");
		}
		if (allowedRight.length < 1)
		{
			throw new IllegalArgumentException("allowedRight size must be greater then zero");
		}
		if (allowedRight[0].isEmpty())
		{
			throw new IllegalArgumentException("allowedRight[0] cannot be empty");
		}
		if (allowedLeft.isEmpty())
		{
			throw new IllegalArgumentException("allowedLeft size must be greater then zero");
		}

		if (System.nanoTime() > created + nanoSecondsToChangeRandom)
		{
			generator = new Random();
			created = System.nanoTime();
		}

		NonTerminal left = allowedLeft.get(generator.nextInt(allowedLeft.size()));
		Symbol[] right = new Symbol[length];
		Symbol chosen;
		if (length == 1 && eps != null && generator.nextInt(3) < 1)
		{
			right[0] = eps;
			return new SimpleContextFreeRule(left, right);
		}
		else
		{
			int index;
			for (int i = 0; i < length; i++)
			{
				if (allowedRight.length < i + 1)
				{
					index = allowedRight.length - 1;
				}
				else
				{
					index = i;
				}
				chosen = allowedRight[index].get(generator.nextInt(allowedRight[index].size()));
				if (chosen.isEpsilon() && length > 1)
				{
					throw new IllegalArgumentException("length is longer than 1 and epsilon is in List allowedRight");
				}
				if (length == 1 && noSimple && chosen.isNonTerminal())
				{
					throw new IllegalArgumentException(
						"length is 1 noSimple is true and List allowedRight contains NonTerminal " + chosen);
				}
				right[i] = chosen;
			}
		}

		return new SimpleContextFreeRule(left, right);
	}

	public ContextFreeRule makeRule(int length, NonTerminal left, List<Symbol>... allowedRight)
	{
		if (left == null)
		{
			throw new NullPointerException("left");
		}
		List<NonTerminal> leftC = new ArrayList<NonTerminal>();
		leftC.add(left);
		return makeRule(length, leftC, allowedRight);
	}

	public boolean isNoSimple()
	{
		return noSimple;
	}

}
