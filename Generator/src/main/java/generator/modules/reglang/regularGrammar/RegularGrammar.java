/*
 * The class stands for the regular grammar
 */

package generator.modules.reglang.regularGrammar;

import generator.modules.reglang.alphabet.AlphabetGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The class stands for the regular grammar (Chomski--type 3)
 * 
 * @author Jana Kadlecova
 */
@SuppressWarnings({ "rawtypes" })
public class RegularGrammar
{

	private String startVariable;
	private Map<String, Set<List<String>>> rules;
	private Set<String> nonterminals;
	private Set<String> terminals;
	private String name = "G";
	private int terminalsType;
	private int variablesType;
	private int rulesCount = 0;

	/**
	 * Constructor, makes new instance of the class RegularGrammar
	 * 
	 * @param startVariable
	 *            the name of the start variable
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws IllegalArgumentException
	 *             if the parameter is not string or is an empty string
	 */
	public RegularGrammar(String startVariable)
	{
		if (startVariable == null)
		{
			throw new NullPointerException("RegularGrammar.Constructor(): " + "the parameter start variable is null");
		}
		if (!(startVariable instanceof String))
		{
			throw new IllegalArgumentException("RegularGrammar.Constructor(): "
				+ "given variable is not instance of the string");
		}
		if (startVariable.isEmpty())
		{
			throw new IllegalArgumentException("RegularGrammar.Constructor(): " + "given variable is the empty string");
		}
		this.startVariable = startVariable;
		this.rules = new HashMap<String, Set<List<String>>>();
		this.terminals = new HashSet<String>();
		this.nonterminals = new HashSet<String>();
		this.addNonTerminal(startVariable);
		this.addVariable(startVariable);
		this.terminalsType = 0;
		this.variablesType = 1;
	}

	public void setTerminals(Set<String> terminals)
	{
		this.terminals = terminals;
	}

	public int getRulesCount()
	{
		return rulesCount;
	}

	public void setRulesCount(int rulesCount)
	{
		this.rulesCount = rulesCount;
	}

	/**
	 * Constructor, makes new instance of the class RegularGrammar
	 * 
	 * @param g
	 *            the regular grammar
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws IllegalArgumentException
	 *             if the parameter is not the instance of RegularGrammar
	 */
	public RegularGrammar(RegularGrammar g)
	{
		if (g == null)
		{
			throw new NullPointerException("RegularGrammar.Constructor(): " + "the parameter regular grammar is null");
		}
		if (!(g instanceof RegularGrammar))
		{
			throw new IllegalArgumentException("RegularGrammar.Constructor(): "
				+ "given variable is not instance of the regular grammar.");
		}
		this.name = g.getName();
		this.startVariable = g.getStartVariable();
		this.rules = g.getRules();
		this.variablesType = g.getVariablesType();
		this.terminalsType = g.getTerminalsType();
		this.terminals = new HashSet<String>(g.getTerminals());
	}

	/**
	 * returns the name of the grammar
	 * 
	 * @return the name of the grammar
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * sets the name of the grammar
	 * 
	 * @param name
	 *            the name of the grammar
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws IllegalArgumentException
	 *             if the parameter is not the instance of the string or is the empty string
	 */
	public void setName(String name)
	{
		if (name == null)
		{
			throw new NullPointerException("RegularGrammar.setName().");
		}
		if (!(name instanceof String))
		{
			throw new IllegalArgumentException("RegularGrammar.setName(): "
				+ "given name is not instance of the string");
		}
		if (name.isEmpty())
		{
			throw new IllegalArgumentException("RegularGrammar.setName(): " + "given name is the empty string");
		}
		this.name = name;
	}

	/**
	 * returns type of the variables as the number (0-9) type 0 a,b,c (lower case) 1 A,B,C (upper case) 2 1,2,3
	 * (numbers) 3 x,y,z (lowercase end of alphabet) 4 X,Y,Z (upper case end of alphabet) 5 t0,t1, t2 6 p0, p1, p2 7
	 * I,II,III, IV 8 i, ii, iii, iv 9 1, 10, 11, 100
	 * 
	 * @return type of the variables as the number (0-9)
	 */
	public int getVariablesType()
	{
		return this.variablesType;
	}

	/**
	 * returns type of the terminals as the number (0-9) type 0 a,b,c (lower case) 1 A,B,C (upper case) 2 1,2,3
	 * (numbers) 3 x,y,z (lowercase end of alphabet) 4 X,Y,Z (upper case end of alphabet) 5 t0,t1, t2 6 p0, p1, p2 7
	 * I,II,III, IV 8 i, ii, iii, iv 9 1, 10, 11, 100
	 * 
	 * @return type of the terminals as the number (0-9)
	 */
	public int getTerminalsType()
	{
		return this.terminalsType;
	}

	/**
	 * changes the variables type
	 * 
	 * @param variablesType
	 *            number 0-9 type 0 a,b,c (lower case) 1 A,B,C (upper case) 2 1,2,3 (numbers) 3 x,y,z (lowercase end of
	 *            alphabet) 4 X,Y,Z (upper case end of alphabet) 5 t0,t1, t2 6 p0, p1, p2 7 I,II,III, IV 8 i, ii, iii,
	 *            iv 9 1, 10, 11, 100
	 * @throws IllegalArgumentException
	 *             if the variables type is unknown
	 */
	public void setVariablesType(int variablesType)
	{
		if (variablesType < 0 || variablesType > 9)
		{
			throw new IllegalArgumentException("The unknown variables type " + variablesType);
		}
		this.variablesType = variablesType;
	}

	/**
	 * changes the terminals type
	 * 
	 * @param terminalsType
	 *            number 0-9 type 0 a,b,c (lower case) 1 A,B,C (upper case) 2 1,2,3 (numbers) 3 x,y,z (lowercase end of
	 *            alphabet) 4 X,Y,Z (upper case end of alphabet) 5 t0,t1, t2 6 p0, p1, p2 7 I,II,III, IV 8 i, ii, iii,
	 *            iv 9 1, 10, 11, 100
	 * @throws IllegalArgumentException
	 *             if the terminals type is unknown
	 */
	public void setTerminalsType(int terminalsType)
	{
		if (terminalsType < 0 || terminalsType > 9)
		{
			throw new IllegalArgumentException("The unknown variables type " + terminalsType);
		}
		this.terminalsType = terminalsType;
	}

	/**
	 * adds new variable; if this variable is yet included, makes nothing
	 * 
	 * @param variable
	 *            new variable
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws IllegalArgumentException
	 *             if the parameter is not string or is empty string or the new variable is the terminal yet
	 */
	public void addVariable(String variable)
	{
		if (variable == null)
		{
			throw new NullPointerException("RegularGrammar.addVariable().");
		}
		if (!(variable instanceof String))
		{
			throw new IllegalArgumentException("RegularGrammar.addVariable(): "
				+ "given variable is not instance of the string");
		}
		if (variable.isEmpty())
		{
			throw new IllegalArgumentException("RegularGrammar.addVariable(): " + "given variable is the empty string");
		}
		if (this.getTerminals().contains(variable))
		{
			throw new IllegalArgumentException("RegularGrammar.addVariable(): " + "given variable is the terminal");
		}
		this.rules.put(variable, new HashSet<List<String>>());
	}

	public void addNonTerminal(String nonterminal)
	{
		addVariable(nonterminal);
		nonterminals.add(nonterminal);
	}

	public Set<String> getNonterminals()
	{
		return nonterminals;
	}

	public void setNonterminals(Set<String> nonterminals)
	{
		this.nonterminals = nonterminals;
	}

	/**
	 * returns terminals as the unmodifiable set
	 * 
	 * @return terminals as the unmodifiable set
	 */
	public Set<String> getTerminals()
	{
		return Collections.unmodifiableSet(terminals);
	}

	/**
	 * returns variables as the new set
	 * 
	 * @return returns variables as the unmodifiable set
	 */
	public Set<String> getVariables()
	{
		return new HashSet<String>(this.rules.keySet());
	}

	/**
	 * returns the name of the start variable
	 * 
	 * @return the name of the start variable
	 */
	public String getStartVariable()
	{
		return this.startVariable;
	}

	/**
	 * renames the old variable to the new variable
	 * 
	 * @param oldVariable
	 *            the name of the old variable
	 * @param newVariable
	 *            the name of the new variable
	 * @throws NoSuchVariableException
	 *             if the old variable is unknown
	 * @throws NullPointerException
	 *             if some parameter is null
	 * @throws IllegalArgumentException
	 *             if the new variable is not new, if the old variable is not included or if the new variable is the the
	 *             terminal
	 */
	public void renameVariable(String oldVariable, String newVariable) throws NoSuchVariableException
	{
		if (oldVariable == null || newVariable == null)
		{
			throw new NullPointerException("RegularGrammar.renameVariable() " + "an old variable: " + oldVariable
				+ " a new variable: " + newVariable);
		}
		if (this.getVariables().contains(newVariable))
		{
			throw new IllegalArgumentException("RegularGrammar." + "renameVariable(): the variable is not new: "
				+ newVariable);
		}
		if (!this.getVariables().contains(oldVariable))
		{
			throw new NoSuchVariableException("RegularGrammar." + "renameVariable(): cannot find old variable "
				+ oldVariable);
		}
		if (this.getTerminals().contains(newVariable))
		{
			throw new IllegalArgumentException("RegularGrammar."
				+ "renameVariable(): the new variable is the terminal: " + newVariable);
		}
		this.addVariable(newVariable);
		// the old variable was the start variable
		if (oldVariable.equals(this.getStartVariable()))
		{
			this.startVariable = newVariable;
		}

		try
		{
			// copy all rules from the old variable
			this.rules.get(newVariable).addAll(this.getRulesFromTheVariable(oldVariable));
			Set<String> variables = new HashSet<String>(this.getVariables());
			variables.remove(oldVariable);
			// changes rules with to old variable to the rules with the new variable
			for (String variable : variables)
			{
				for (List<String> list : this.getRulesFromTheVariable(variable))
				{
					if (list.size() == 2)
					{
						if (list.get(1).equals(oldVariable))
						{
							this.removeRule(variable, list.get(0), oldVariable);
							this.addRule(variable, list.get(0), newVariable);
						}
					}
				}
			}
			this.removeVariable(oldVariable);
		}
		catch (NoSuchVariableException ex)
		{
			throw new Error("RegularGrammar.renameVariable(): " + ex.getMessage());
		}
		catch (NoSuchTerminalException e)
		{
			throw new Error("RegularGrammar.renameVariable(): " + e.getMessage());
		}
	}

	/**
	 * removes the variable
	 * 
	 * @param removedVariable
	 *            the name of the removed variable
	 * @throws NoSuchVariableException
	 *             the variable is unknown
	 * @throws NullPointerException
	 *             the variable is null
	 */
	public void removeVariable(String removedVariable) throws NoSuchVariableException
	{
		if (removedVariable == null)
		{
			throw new NullPointerException("RegularGrammar.removeVariable()");
		}
		if (!this.getVariables().contains(removedVariable))
		{
			throw new NoSuchVariableException("RegularGrammar." + "removeVariable():" + removedVariable);
		}
		for (String variable : this.getVariables())
		{
			try
			{
				Set<List<String>> lists = this.getRulesFromTheVariable(variable);
				if (lists.size() != 0)
				{
					for (List<String> list : lists)
					{
						if (list.size() == 2)
						{
							if (list.get(1).equals(removedVariable))
							{
								this.removeRule(variable, list.get(0), removedVariable);
							}
						}
					}
				}
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammar.removeVariable(): " + e.getMessage());
			}
			catch (NoSuchTerminalException e)
			{
				throw new Error("RegularGrammar.removeVariable(): " + e.getMessage());
			}
		}
		rules.remove(removedVariable);
	}

	/**
	 * returns the rules of the grammar as the new map
	 * 
	 * @return the rules of the grammar as the new map
	 */
	public Map<String, Set<List<String>>> getRules()
	{
		Map<String, Set<List<String>>> result = new HashMap<String, Set<List<String>>>();
		for (String variable : this.getVariables())
		{
			try
			{
				result.put(variable, new HashSet<List<String>>(this.getRulesFromTheVariable(variable)));
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammar.getRules(): " + e.getMessage());
			}
		}
		return result;
	}

	/**
	 * returns the rules from the variable as the new set or empty set
	 * 
	 * @param variable
	 *            the name of the variable
	 * @return the rules from the variable as the new set
	 * @throws NoSuchVariableException
	 *             the variable is unknown
	 * @throws NullPointerException
	 *             the variable is null
	 */
	public Set<List<String>> getRulesFromTheVariable(String variable) throws NoSuchVariableException
	{
		if (variable == null)
		{
			throw new NullPointerException("RegularGrammar." + "getRulesFromTheVariable");
		}
		if (!this.getVariables().contains(variable))
		{
			throw new NoSuchVariableException("RegularGrammar." + "getRulesFromTheVariable: " + variable);
		}
		Set<List<String>> result = new HashSet<List<String>>();
		for (List<String> l : this.rules.get(variable))
		{
			result.add(new ArrayList<String>(l));
		}
		return result;
	}

	/**
	 * adds the rule: variable -> terminal
	 * 
	 * @param variable
	 *            the name of the variable
	 * @param terminal
	 *            the name of the terminal
	 * @throws NoSuchVariableException
	 *             the variable is unknown
	 * @throws NullPointerException
	 *             the variable or the terminal is null
	 * @throws IllegalArgumentException
	 *             the terminal is the empty string
	 */
	public boolean addRule(String variable, String terminal) throws NoSuchVariableException
	{
		if (variable == null || terminal == null)
		{
			throw new NullPointerException("RegularGrammar.addRule()");
		}
		if (!this.getVariables().contains(variable))
		{
			throw new NoSuchVariableException("RegularGrammar.addRule(): " + variable);
		}
		if (terminal.isEmpty())
		{
			throw new IllegalArgumentException("RegularGrammar.addRule(): " + "the terminal is the empty string");
		}
		List<String> l = new ArrayList<String>();
		l.add(terminal);
		if (!terminal.equals("epsilon"))
		{
			this.terminals.add(terminal);
		}
		boolean succesfullyAdded = this.rules.get(variable).add(l);
		if (succesfullyAdded)
			rulesCount++;
		return succesfullyAdded;
	}

	/**
	 * adds the rule: fromVariable -> terminal+toVariable "epsilon" is special terminal
	 * 
	 * @param fromVariable
	 *            the variable name
	 * @param terminal
	 *            the terminal name
	 * @param toVariable
	 *            the variable name
	 * @throws NoSuchVariableException
	 *             an uknown variable
	 * @throws NullPointerException
	 *             some variable or the terminal is null
	 * @throws IllegalArgumentException
	 *             the terminal is the empty string or is the variable
	 */
	public boolean addRule(String fromVariable, String terminal, String toVariable) throws NoSuchVariableException
	{
		if (fromVariable == null || terminal == null || toVariable == null)
		{
			throw new NullPointerException("RegularGrammar.addRule()");
		}
		if (!this.getVariables().contains(fromVariable))
		{
			throw new NoSuchVariableException("RegularGrammar.addRule(): " + fromVariable);
		}
		if (!this.getVariables().contains(toVariable))
		{
			throw new NoSuchVariableException("RegularGrammar.addRule(): " + toVariable);
		}
		if (terminal.isEmpty())
		{
			throw new IllegalArgumentException("RegularGrammar.addRule(): " + "the terminal is the empty string");
		}
		if (this.getVariables().contains(terminal))
		{
			throw new IllegalArgumentException("RegularGrammar.addRule(): " + "the terminal is the variable "
				+ terminal);
		}
		List<String> l = new ArrayList<String>();
		l.add(terminal);
		l.add(toVariable);
		if (!terminal.equals("epsilon"))
		{
			this.terminals.add(terminal);
		}
		boolean succesfullyAdded = this.rules.get(fromVariable).add(l);
		if (succesfullyAdded)
			rulesCount++;
		return succesfullyAdded;
	}

	/**
	 * removes the rule variable -> terminal if it is included or makes nothing
	 * 
	 * @param variable
	 *            the name of the variable
	 * @param terminal
	 *            the name of the terminal
	 * @throws NoSuchVariableException
	 *             the unknown variable
	 * @throws NoSuchTerminalException
	 *             the unknown terminal
	 * @throws NullPointerException
	 *             some parameter is null
	 */
	public void removeRule(String variable, String terminal) throws NoSuchVariableException, NoSuchTerminalException
	{
		if (variable == null || terminal == null)
		{
			throw new NullPointerException("RegularGrammar.removeRule(): " + "some of the given parameters is null.");
		}
		if (!this.getVariables().contains(variable))
		{
			throw new NoSuchVariableException("RegularGrammar.removeRule(): " + variable);
		}
		if (!this.getTerminals().contains(terminal))
		{
			throw new NoSuchTerminalException("RegularGrammar.removeRule(): " + terminal);
		}
		Set<List<String>> lists;
		try
		{
			lists = this.getRulesFromTheVariable(variable);
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar.removeRule(): " + e.getMessage());
		}
		for (List<String> list : lists)
		{
			if (list.size() == 1)
			{
				if (list.get(0).equals(terminal))
				{
					this.rules.get(variable).remove(list);
				}
			}
		}
	}

	/**
	 * removes the rule variable -> terminal+variable if it is included or makes nothing
	 * 
	 * @param fromVariable
	 *            the name of the variable
	 * @param terminal
	 *            the name of the terminal
	 * @param toVariable
	 *            the name of the variable
	 * @throws NoSuchVariableException
	 *             the unknown fromVariable or toVariable
	 * @throws NoSuchTerminalException
	 *             the unknown terminal
	 * @throws NullPointerException
	 *             some parameter is null
	 */
	public void removeRule(String fromVariable, String terminal, String toVariable) throws NoSuchVariableException,
		NoSuchTerminalException
	{
		if (fromVariable == null || terminal == null || toVariable == null)
		{
			throw new NullPointerException("RegularGrammar.removeRule(): " + "some of the given parameters is null");
		}
		if (!this.getVariables().contains(fromVariable))
		{
			throw new NoSuchVariableException("RegularGrammar.removeRule(): " + fromVariable);
		}
		if (!this.getVariables().contains(toVariable))
		{
			throw new NoSuchVariableException("RegularGrammar.removeRule(): " + toVariable);
		}
		if (!this.getTerminals().contains(terminal))
		{
			throw new NoSuchTerminalException("RegularGrammar.removeRule(): " + terminal);
		}
		Set<List<String>> lists;
		try
		{
			lists = this.getRulesFromTheVariable(fromVariable);
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar.removeRule(): " + e.getMessage());
		}
		for (List<String> list : lists)
		{
			if (list.size() == 2)
			{
				if (list.get(0).equals(terminal) && list.get(1).equals(toVariable))
				{
					this.rules.get(fromVariable).remove(list);
				}
			}
		}
	}

	/**
	 * returns number of rules of this regular grammar
	 * 
	 * @return number of rules of this regular grammar
	 */
	public int getNumberOfRules()
	{
		int i = 0;
		for (String variable : this.getVariables())
		{
			Set<List<String>> lists;
			try
			{
				lists = this.getRulesFromTheVariable(variable);
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammar.getNumberOfRules(): " + e.getMessage());
			}
			i = i + lists.size();
		}
		return i;
	}

	/**
	 * finds out, if the epsilon rule is present
	 * 
	 * @return true, the epsilon rule is present, false otherwise
	 */
	public boolean containsEpsilon()
	{
		Set<List<String>> lists;
		try
		{
			lists = this.getRulesFromTheVariable(this.getStartVariable());
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar.containsEpsilon(): " + e.getMessage());
		}
		for (List<String> l : lists)
		{
			if (l.get(0).equals("epsilon"))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * returns the rules from this variable as the string variable -> terminal1+variable1 | terminal2+variable2 |
	 * terminal3...
	 * 
	 * @param variable
	 * @return the rules from this variable as the string
	 */
	private String rulesFromTheVariableToTheString(String variable) throws NoSuchVariableException
	{
		if (variable == null)
		{
			throw new NullPointerException("RegularGrammar." + "rulesFromTheVariableToTheString(): "
				+ "given parameter is null.");
		}
		if (!this.getVariables().contains(variable))
		{
			throw new NoSuchVariableException("RegularGrammar." + "rulesFromTheVariableToTheString(): " + variable);
		}
		StringBuilder sb = new StringBuilder();
		SortedSet<List<String>> lists;
		int i;
		lists = new TreeSet<List<String>>(new RulesComparator<List>(this.getTerminalsType(), this.getVariablesType()));
		try
		{
			lists.addAll(this.getRulesFromTheVariable(variable));
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar." + "rulesFromTheVariableToTheString(): " + e.getMessage());
		}
		i = lists.size();
		if (lists.size() > 0)
		{
			sb.append(this.stringToPlainText(variable) + " ->");
			for (List<String> list : lists)
			{
				if (list.size() == 2)
				{
					sb.append(" " + this.stringToPlainText(list.get(0)) + this.stringToPlainText(list.get(1)));
				}
				else
				{
					sb.append(" " + this.stringToPlainText(list.get(0)));
				}
				if (i != 1)
				{
					sb.append(" |");
				}
				i--;
			}
		}
		return sb.toString().replace("epsilon", "\u03b5");
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		SortedSet<String> variables = new TreeSet<String>(AlphabetGenerator.sortSet(this.getVariables(),
			this.getVariablesType()));
		SortedSet<String> tempTerminals = new TreeSet<String>(AlphabetGenerator.sortSet(this.getTerminals(),
			this.getTerminalsType()));
		sb.append("Grammar " + this.getName() + " = (" + variables.toString().replace("_{", "").replace("}", "") + ", "
			+ tempTerminals.toString().replace("_{", "").replace("}", "") + ", P, "
			+ this.stringToPlainText(this.getStartVariable()) + "), where\n");
		sb.append("P = {\n");
		String s;
		try
		{
			s = this.rulesFromTheVariableToTheString(this.getStartVariable());
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar.toString(): " + e.getMessage());
		}
		if (!s.isEmpty())
		{
			sb.append(s);
		}
		variables.remove(this.getStartVariable());
		for (String variable : variables)
		{
			try
			{
				s = this.rulesFromTheVariableToTheString(variable);
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammar.toString(): " + e.getMessage());
			}
			if (!s.isEmpty())
			{
				sb.append("\n" + s);
			}
		}
		sb.append("}\n");
		return sb.toString().replace("[", "{").replace("]", "}");
	}

	/**
	 * returns the rules from this variable as the string variable -> terminal1+variable1 | terminal2+variable2 |
	 * terminal3...
	 * 
	 * @param variable
	 * @return the rules from this variable as the string
	 */
	private String rulesFromTheVariableToTheLaTeX(String variable) throws NoSuchVariableException
	{
		if (variable == null)
		{
			throw new NullPointerException("RegularGrammar." + "rulesFromTheVariableToTheString(): "
				+ "the given parameter is null");
		}
		if (!this.getVariables().contains(variable))
		{
			throw new NoSuchVariableException("RegularGrammar." + "rulesFromTheVariableToTheString(): " + variable);
		}
		StringBuilder sb = new StringBuilder();
		SortedSet<List<String>> lists;
		int i;
		lists = new TreeSet<List<String>>(new RulesComparator<List>(this.getTerminalsType(), this.getVariablesType()));
		try
		{
			lists.addAll(this.getRulesFromTheVariable(variable));
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar." + "rulesFromTheVariableToTheString(): " + e.getMessage());
		}
		i = lists.size();
		if (lists.size() > 0)
		{
			sb.append(variable + " & \\rightarrow &");
			for (List<String> list : lists)
			{
				if (list.size() == 2)
				{
					sb.append(" " + list.get(0) + list.get(1));
				}
				else
				{
					sb.append(" " + list.get(0));
				}
				if (i != 1)
				{
					sb.append(" \\mid ");
				}
				i--;
			}
		}
		return sb.toString().replace("epsilon", "\\varepsilon");
	}

	/**
	 * returns the regular grammar in the LaTeX form
	 * 
	 * @return the regular grammar in the LaTeX form
	 */
	public String toLaTeX()
	{
		StringBuilder sb = new StringBuilder();
		SortedSet<String> variables = new TreeSet<String>(AlphabetGenerator.sortSet(this.getVariables(),
			this.getVariablesType()));
		SortedSet<String> tempTerminals = new TreeSet<String>(AlphabetGenerator.sortSet(this.getTerminals(),
			this.getTerminalsType()));
		sb.append("$" + this.getName() + " = (\\{");
		SortedSet<String> variables2 = new TreeSet<String>(AlphabetGenerator.sortSet(this.getVariables(),
			this.getVariablesType()));
		for (int i = 0; i < variables.size(); i++)
		{
			String variable = variables2.first();
			variables2.remove(variable);
			if (sb.toString().length() % 80 < variable.length() + 2 && i != 0)
			{
				sb.append("\\\\\n");
			}
			sb.append(variable + ", ");
		}
		sb.replace(sb.length() - 2, sb.length(), "\\},");
		sb.append(" \\{");
		for (int i = 0; i < this.getTerminals().size(); i++)
		{
			String terminal = tempTerminals.first();
			tempTerminals.remove(terminal);
			if (sb.toString().length() % 80 < terminal.length() + 2)
			{
				sb.append("\\\\\n");
			}
			sb.append(terminal + ", ");
		}
		sb.replace(sb.length() - 2, sb.length(), "\\},");
		sb.append(" P, " + this.getStartVariable() + "),$ where \n");
		sb.append("\\[\n\\setlength{\\arraycolsep}{1.5pt}\n\\begin{array}{rlcl}\nP = \\{ ");
		String s;
		try
		{
			s = this.rulesFromTheVariableToTheLaTeX(this.getStartVariable());
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar.toString(): " + e.getMessage());
		}
		if (!s.isEmpty())
		{
			sb.append("& " + s + ",");
		}
		variables.remove(this.getStartVariable());
		for (String variable : variables)
		{
			try
			{
				s = this.rulesFromTheVariableToTheLaTeX(variable);
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammar.toString(): " + e.getMessage());
			}
			if (!s.isEmpty())
			{
				sb.append(" \\\\\n& " + s + ",");
			}
		}
		sb.replace(sb.length() - 1, sb.length(), "");
		sb.append("\\} \\\\\n\\end{array}\n\\]");
		return sb.toString();

	}

	/**
	 * returns the rules as the string variable -> t1v1 | t2v2 | t3..
	 * 
	 * @param variable
	 *            name of the variable, from which the rules are wanted
	 * @return rules as the string variable -> t1v1 | t2v2 | t3..
	 * @throws NoSuchVariableException
	 */
	private String rulesFromTheVariableToTheIS(String variable) throws NoSuchVariableException
	{
		if (variable == null)
		{
			throw new NullPointerException("RegularGrammar." + "rulesFromTheVariableToTheString()");
		}
		if (!this.getVariables().contains(variable))
		{
			throw new NoSuchVariableException("RegularGrammar." + "rulesFromTheVariableToTheString(): " + variable);
		}
		StringBuilder sb = new StringBuilder();
		SortedSet<List<String>> lists;
		int i;
		lists = new TreeSet<List<String>>(new RulesComparator<List>(this.getTerminalsType(), this.getVariablesType()));
		try
		{
			lists.addAll(this.getRulesFromTheVariable(variable));
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar." + "rulesFromTheVariableToTheString(): " + e.getMessage());
		}
		i = lists.size();
		if (lists.size() > 0)
		{
			sb.append("<" + this.stringToPlainText(variable) + "> ->");
			for (List<String> list : lists)
			{
				if (list.size() == 2)
				{
					sb.append(" \"" + this.stringToPlainText(list.get(0)) + "\"<" + this.stringToPlainText(list.get(1))
						+ ">");
				}
				else
				{
					sb.append(" \"" + this.stringToPlainText(list.get(0)) + "\"");
				}
				if (i != 1)
				{
					sb.append(" |");
				}
				i--;
			}
		}
		return sb.toString().replace("epsilon", "\u03b5");
	}

	/**
	 * returns the regular grammar as the result to IS, viz http://arran.fi.muni.cz:8180/fja/help.html
	 * 
	 * @param teacher
	 *            this regular grammar GRA
	 * @param student
	 *            the requierement to the answer
	 * @return the regular grammar as the result to IS
	 */
	public String toIS(String teacher, String student)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" :e" + "\n" + ":e=\"b:");
		sb.append(teacher + "-" + student + ":");
		String s = "";
		try
		{
			s = this.rulesFromTheVariableToTheIS(this.getStartVariable());
		}
		catch (NoSuchVariableException e)
		{
			throw new Error("RegularGrammar.toIS(): " + e.getMessage());
		}
		if (!s.isEmpty())
		{
			sb.append(s);
		}
		Set<String> variables = this.getVariables();
		variables.remove(this.getStartVariable());
		for (String variable : variables)
		{
			try
			{
				s = this.rulesFromTheVariableToTheIS(variable);
				if (!s.isEmpty())
				{
					sb.append(", " + s);
				}
			}
			catch (NoSuchVariableException e)
			{
				throw new Error("RegularGrammar.toIS(): " + e.getMessage());
			}
		}
		sb.append("\" ok\n");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o)
	{ // don't use the atribute name
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof RegularGrammar))
		{
			return false;
		}
		RegularGrammar g = (RegularGrammar) o;
		if (!this.getStartVariable().equals(g.getStartVariable()))
		{
			return false;
		}
		if (!this.getRules().equals(g.getRules()))
		{
			return false;
		}
		if (!this.getTerminals().equals(g.getTerminals()))
		{
			return false;
		}
		if (this.getTerminalsType() != g.getTerminalsType() || this.getVariablesType() != g.getVariablesType())
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 11 * hash + (this.startVariable != null ? this.startVariable.hashCode() : 0);
		hash = 11 * hash + (this.rules != null ? this.rules.hashCode() : 0);
		hash = 11 * hash + (this.terminals != null ? this.terminals.hashCode() : 0);
		hash = 11 * hash + this.terminalsType;
		hash = 11 * hash + this.variablesType;
		return hash;
	}

	/**
	 * string without some characters: '_' and '{' and '}'
	 * 
	 * @param s
	 *            string
	 * @return string without some characters: '_' and '{' and '}'
	 */
	private String stringToPlainText(String s)
	{
		return s.replace("_{", "").replace("}", "");
	}

}
