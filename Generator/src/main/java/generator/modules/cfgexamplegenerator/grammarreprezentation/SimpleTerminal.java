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

public final class SimpleTerminal extends AbstractSymbol implements Terminal
{

	public SimpleTerminal(String name) throws SymbolException
	{
		super(name);
	}

	protected void controlFormat(String name) throws IncorrectSymbolFormatException
	{
		if (name.length() > 1)
		{
			throw new IncorrectSymbolFormatException("terminal symbol must be of length 1");
		}
		String inLowerCase = name.toLowerCase();
		if (!name.equals(inLowerCase))
		{
			throw new IncorrectSymbolFormatException("terminal symbol must be in lower case. If you "
				+ "think it was in lower case there could by some problems with your local language"
				+ "settings. Use lower case characters that are same in your default language and in"
				+ "language you write them or contact author for more help.");
		}
	}

	@Override
	public boolean isCorrectSymbol(String name)
	{
		if (!super.isCorrectSymbol(name))
		{
			return false;
		}

		String inLowerCase = name.toLowerCase();
		return name.length() <= 1 && name.equals(inLowerCase);
	}

}
