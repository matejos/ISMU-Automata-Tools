/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SimpleNonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SimpleTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SymbolException;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Terminal;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */

public class SymbolManager
{

	public static final String[] ALPHABETH = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
		"O", "P", "R", "S", "T", "Q", "X", "Y", "U", "V", "W", "Z" };
	public static final int ALPHABETH_LENGTH = 26;

	public static Terminal getNewT(ContextFreeGrammar<? extends ContextFreeRule> grammar)
	{
		if (grammar == null)
		{
			throw new NullPointerException("grammar");
		}
		Terminal t = null;
		for (String s : ALPHABETH)
		{
			try
			{
				t = new SimpleTerminal(s.toLowerCase());
			}
			catch (SymbolException ex)
			{
				Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
				throw new InternalError("String: " + s.toLowerCase()
					+ " should be correct name for terminal but its not.");
			}
			if (!grammar.terminalExists(t))
			{
				return t;
			}
		}
		throw new RuntimeException("All 26 symbols allowed for terminals are used. Cannot create new terminal symbol.");
	}

	public static Set<Terminal> getTerminalSet(int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException("size must be at least zero");
		}
		if (size > ALPHABETH_LENGTH)
		{
			throw new IllegalArgumentException("size must be less or equal to " + ALPHABETH_LENGTH);
		}
		Terminal t = null;
		Set<Terminal> result = new HashSet<Terminal>();
		for (int i = 0; i < size; i++)
		{
			try
			{
				t = new SimpleTerminal(ALPHABETH[i].toLowerCase());
			}
			catch (SymbolException ex)
			{
				Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
				throw new InternalError("String: " + ALPHABETH[i].toLowerCase()
					+ " should be correct name for terminal but its not.");
			}
			result.add(t);
		}
		return result;
	}

	private static NonTerminal getNewNFromOld(ContextFreeGrammar<? extends ContextFreeRule> grammar, String oldName)
		throws RuntimeException
	{

		NonTerminal newSymbol;

		try
		{
			oldName = oldName + "\'";
			newSymbol = new SimpleNonTerminal(oldName);
			while (grammar.nonTerminalExists(newSymbol))
			{
				newSymbol = new SimpleNonTerminal(newSymbol.getName() + "\'");
			}
		}
		catch (SymbolException ex)
		{
			Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
		return newSymbol;
	}

	private SymbolManager()
	{
	}

	private static void nullTest(Object o)
	{
		if (o == null)
		{
			throw new NullPointerException("o");
		}
	}

	public static NonTerminal getNewN(ContextFreeGrammar<? extends ContextFreeRule> grammar, NonTerminal derivatedFrom)
	{
		nullTest(grammar);
		nullTest(derivatedFrom);

		String oldName = derivatedFrom.getName();
		return getNewNFromOld(grammar, oldName);

	}

	public static NonTerminal getNewN(ContextFreeGrammar<? extends ContextFreeRule> grammar, Terminal derivatedFrom)
	{
		nullTest(grammar);
		nullTest(derivatedFrom);

		// String oldName = derivatedFrom.getName().toUpperCase();
		String oldName = derivatedFrom.getName();
		return getNewNFromOld(grammar, oldName);

	}

	public static NonTerminal getNewN(ContextFreeGrammar<? extends ContextFreeRule> grammar)
	{
		nullTest(grammar);

		boolean found = false;
		NonTerminal candidate = null;
		String inAdition = "";
		while (!found)
		{
			for (int i = 0; i < 26; i++)
			{
				try
				{
					candidate = new SimpleNonTerminal(ALPHABETH[i] + inAdition);
				}
				catch (SymbolException ex)
				{
					Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
					throw new RuntimeException(ex);
				}
				if (!grammar.nonTerminalExists(candidate))
				{
					found = true;
					break;
				}
			}
			inAdition = inAdition + "\'";
		}

		return candidate;
	}

	public static NonTerminal getNewN(ContextFreeGrammar<? extends ContextFreeRule> grammar, int numberInAlphabeth)
	{
		nullTest(grammar);
		if (numberInAlphabeth < 1)
		{
			throw new IllegalArgumentException("numberInAlphabeth must be more than zero");
		}

		String inAdition = "";
		NonTerminal result = null;
		for (int i = 1; i <= numberInAlphabeth / ALPHABETH_LENGTH; i++)
		{
			inAdition = inAdition + "\'";
		}
		try
		{
			result = new SimpleNonTerminal(ALPHABETH[numberInAlphabeth % ALPHABETH_LENGTH] + inAdition);
		}
		catch (SymbolException ex)
		{
			Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
		return result;
	}

	public static NonTerminal getNewN(ContextFreeGrammar<? extends ContextFreeRule> grammar, String from)
	{
		nullTest(grammar);
		nullTest(from);
		if (!NonTerminal.CORRECT_NAME_PATTERN.matcher(from).matches())
		{
			throw new IllegalArgumentException("String '" + from + "' is not correct nonterminal name.");
		}
		NonTerminal result = null;
		try
		{
			while (grammar.nonTerminalExists(new SimpleNonTerminal(from)))
			{
				from = from + "'";
			}
			result = new SimpleNonTerminal(from);
		}
		catch (SymbolException ex)
		{
			Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
			throw new InternalError("String: " + from + " schould be correct name for terminal but its not.");
		}
		return result;
	}

	public static NonTerminal getNewN(ContextFreeGrammar<? extends ContextFreeRule> grammar, NonTerminal[] derivedFrom)
	{
		nullTest(grammar);
		nullTest(derivedFrom);
		if (derivedFrom.length < 2)
		{
			throw new IllegalArgumentException(
				"use another method for creating new nonterminal derivated from single nonterminal");
		}

		String newName = "";
		for (NonTerminal n : derivedFrom)
		{
			newName = newName + n.getName();
		}

		newName = "<" + newName + ">";
		NonTerminal candidate;
		try
		{
			candidate = new SimpleNonTerminal(newName);
			while (grammar.nonTerminalExists(candidate))
			{
				candidate = new SimpleNonTerminal(candidate.getName() + "'");
			}
		}
		catch (SymbolException ex)
		{
			Logger.getLogger(SymbolManager.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return candidate;
	}

}
