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

public abstract class AbstractSymbol implements Symbol
{

	private String name;

	private AbstractSymbol()
	{
	}

	protected AbstractSymbol(String name) throws SymbolException
	{

		if (name == null)
		{
			throw new NullPointerException("symbol name");
		}

		if (name.equals(""))
		{
			throw new IncorrectSymbolFormatException("Symbol cannot be empty string");
		}
		if (!(this instanceof Epsilon) && name.equals(Symbol.EPSILON_LETTER))
		{
			throw new IncorrectSymbolFormatException("Symbol " + name + " is reserved only for Epsilon");
		}

		controlFormat(name);

		this.name = name;
	}

	public boolean isCorrectSymbol(String candidate)
	{
		if (candidate == null)
		{
			throw new NullPointerException("candidate");
		}
		if (candidate.equals(""))
		{
			return false;
		}
		if (!(this instanceof Epsilon) && name.equals(Symbol.EPSILON_LETTER))
		{
			return false;
		}

		return true;
	}

	public String getName()
	{
		return name;
	}

	public boolean isTerminal()
	{
		if (this instanceof Terminal)
		{
			if (this instanceof NonTerminal || this instanceof Epsilon)
			{
				throw new RuntimeException("No Symbol cannot by both terminal and non-terminal(or epsylon)");
			}
			return true;
		}
		return false;
	}

	public boolean isEpsilon()
	{
		if (this instanceof NonTerminal || this instanceof Terminal)
		{
			if (this.equals(Symbol.EPSILON))
			{
				throw new RuntimeException("No Epsilon symbol should by terminal or nonterminal");
			}
		}
		return this.equals(Symbol.EPSILON);
	}

	public boolean isNonTerminal()
	{
		if (this instanceof NonTerminal)
		{
			if (this instanceof Terminal || this instanceof Epsilon)
			{
				throw new RuntimeException("No Symbol cannot by both nonterminal and terminal(or epsilon)");
			}
			return true;
		}
		return false;
	}

	public static final class Epsilon extends AbstractSymbol
	{

		public static final Epsilon instance = new Epsilon();

		private Epsilon()
		{
			super();
			if (Symbol.EPSILON_LETTER == null)
			{
				throw new RuntimeException("Symbol.epsilonLetter cannot be null");
			}
			if (Symbol.EPSILON_LETTER.isEmpty())
			{
				throw new RuntimeException("Symbol.epsilonLetter cannot be empty");
			}
			if (Symbol.EPSILON_LETTER.length() > 1)
			{
				throw new RuntimeException("Symbol.epsilonLetter cannot be longer than one symbol");
			}
			super.name = Symbol.EPSILON_LETTER;
		}

		public static Epsilon getInstance()
		{
			return instance;
		}

		@Override
		protected void controlFormat(String name) throws SymbolException
		{
			if (!name.equals(Symbol.EPSILON_LETTER))
			{
				throw new IncorrectSymbolFormatException("name must be Symbol.epsylonLetter");
			}
		}

		@Override
		public boolean isCorrectSymbol(String candidate)
		{
			return super.isCorrectSymbol(candidate) && candidate.equals(Symbol.EPSILON_LETTER);
		}
	}

	@Override
	public String toString()
	{
		return name;
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
		if (!(obj instanceof Symbol))
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		/*
		 * if (this instanceof NonTerminal && !(obj instanceof NonTerminal)){ return false; } if (this instanceof
		 * Terminal && !(obj instanceof Terminal)){ return false; } if (this instanceof EPSILON){ if (obj instanceof
		 * EPSILON){ return true; } return false; }
		 */
		final Symbol other = (Symbol) obj;
		return this.name.equals(other.getName());
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	protected abstract void controlFormat(String name) throws SymbolException;

	public int compareTo(Symbol other)
	{
		if (other == null)
		{
			return -1;
		}
		return this.toString().compareTo(other.toString());
	}

}
