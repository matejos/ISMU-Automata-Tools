/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek all rights reserved
 */

public abstract class AbstractContextFreeRule implements ContextFreeRule
{

	private NonTerminal leftSide;
	private Symbol[] rightSide;
	private boolean isEpsilon = false;
	private boolean isSimple = false;
	private boolean isTerminal = false;
	private String rightSideString;
	private String wholeString;

	private void nullTest(Object o)
	{
		if (o == null)
		{
			throw new NullPointerException("o");
		}
	}

	protected AbstractContextFreeRule(NonTerminal left, Symbol[] right)
	{
		nullTest(left);
		nullTest(right);
		if (right.length == 0)
		{
			throw new IllegalArgumentException("Array right cannot be of zero length. Left side was: " + left);
		}
		int nonEpsilonFound = 0;
		for (Symbol r : right)
		{
			nullTest(r);
			if (!r.isEpsilon())
			{
				nonEpsilonFound++;
			}
		}
		Symbol[] rightCan;
		int pos = 0;
		if (nonEpsilonFound > 0)
		{
			rightCan = new Symbol[nonEpsilonFound];
			for (Symbol s : right)
			{
				if (!s.isEpsilon())
				{
					rightCan[pos] = s;
					pos++;
				}
			}
		}
		else
		{
			rightCan = new Symbol[1];
			if (!right[0].isEpsilon())
			{
				throw new InternalError("Array right is longer than 0, all symbols are epsilon && "
					+ "first symbol is not epsilon!");
			}
			rightCan[0] = right[0];
		}

		if (rightCan.length == 1)
		{
			if (rightCan[0].isEpsilon())
			{
				this.isEpsilon = true;
			}
			else if (rightCan[0].isNonTerminal())
			{
				this.isSimple = true;
			}
		}

		leftSide = left;
		rightSide = rightCan;

		isTerminal = true;
		for (Symbol s : rightSide)
		{
			isTerminal = isTerminal && !s.isNonTerminal();
		}

		rightSideString = rightSideToStringPrivate();
		wholeString = toStringPrivate();
	}

	public NonTerminal getSymbolOnLeftSide()
	{
		return leftSide;
	}

	public Symbol[] getSymbolsOnRightSide()
	{
		return rightSide.clone();
	}

	public Symbol getFirstRightSymbol()
	{
		return rightSide[0];
	}

	public Symbol getRightSideSymbolAt(int position)
	{
		if (position > rightSide.length)
		{
			throw new IllegalArgumentException("position must be less or equals to getRightSymbolsCount()");
		}
		return rightSide[position - 1];
	}

	public int getRightSymbolsCount()
	{
		return rightSide.length;
	}

	public String leftSideToString()
	{
		return leftSide.toString();
	}

	private String rightSideToStringPrivate()
	{
		String result = "";
		for (Symbol s : rightSide)
		{
			result = result + s.toString();
		}
		return result;
	}

	public boolean isEpsilonRule()
	{
		return isEpsilon;
	}

	public boolean isSimpleRule()
	{
		return isSimple;
	}

	public boolean isTerminalRule()
	{
		return isTerminal;
	}

	@Override
	public String toString()
	{
		return wholeString;
	}

	public String rightSideToString()
	{
		return rightSideString;
	}

	private String toStringPrivate()
	{
		return leftSideToString() + ContextFreeRule.ARROW + rightSideToStringPrivate();
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof ContextFreeRule))
		{
			return false;
		}
		ContextFreeRule other = (ContextFreeRule) obj;
		if (!other.getSymbolOnLeftSide().equals(leftSide))
		{
			return false;
		}
		if (getRightSymbolsCount() != other.getRightSymbolsCount())
		{
			return false;
		}
		int i = 0;
		for (Symbol s : rightSide)
		{
			if (!s.equals(other.getRightSideSymbolAt(i + 1)))
			{
				return false;
			}
			i++;
		}
		return true;
	}

	@Override
	public abstract ContextFreeRule clone();

	public int compareTo(ContextFreeRule o)
	{
		if (o == null)
		{
			return -1;
		}
		return this.toString().compareTo(o.toString());
	}

}
