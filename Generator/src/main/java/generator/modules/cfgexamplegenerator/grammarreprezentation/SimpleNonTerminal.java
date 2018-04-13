/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import java.util.regex.Matcher;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */

public final class SimpleNonTerminal extends AbstractSymbol implements NonTerminal
{

	public SimpleNonTerminal(String name) throws SymbolException
	{
		super(name);
	}

	protected void controlFormat(String name) throws IncorrectSymbolFormatException
	{
		if (name == null)
		{
			throw new NullPointerException("name");
		}
		Matcher matcher = CORRECT_NAME_PATTERN.matcher(name);
		if (!matcher.matches())
		{
			throw new IncorrectSymbolFormatException("The given String " + name
				+ " is not correct name for nonterminal. The correct form always matches regular" + "expression "
				+ CORRECT_NAME_REGEX + " .");
		}
	}

	@Override
	public boolean isCorrectSymbol(String name)
	{
		if (!super.isCorrectSymbol(name))
		{
			return false;
		}

		// String upperCase = name.toUpperCase();
		// return (name.startsWith("<") || upperCase.charAt(1) == name.charAt(1)) &&
		// (name.endsWith(">") || upperCase.charAt(name.length()) == name.charAt(name.length()));
		try
		{
			controlFormat(name);
		}
		catch (IncorrectSymbolFormatException e)
		{
			return false;
		}
		return true;
	}

}
