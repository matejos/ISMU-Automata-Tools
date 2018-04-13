/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */

public enum Form
{

	NO_EPSILON("without " + Symbol.EPSILON_LETTER + "-productions"),
	NO_UNREACHABLE("no unreachable symbols"),
	NO_NONGENERATING("no symbols that cannot be transformed to only terminal strings"),
	NO_SIMPLE_RULES("no unit productions"),
	NOT_CYCLIC("no cycles in grammar"),
	NO_LEFT_RECURSION("no left recursion"),
	CNF("Chomsky normal form"),
	GNF("Greibach normal form");

	private String message;

	Form(String message)
	{
		this.message = message;
	}

	@Override
	public String toString()
	{
		return message;
	}

}
