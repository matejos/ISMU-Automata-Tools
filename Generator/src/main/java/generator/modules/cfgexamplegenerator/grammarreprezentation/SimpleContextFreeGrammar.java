/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class SimpleContextFreeGrammar<F extends ContextFreeRule> extends AbstractContextFreeGrammar<F>
{

	public SimpleContextFreeGrammar()
	{
		super();
	}

	public SimpleContextFreeGrammar<F> clone()
	{
		SimpleContextFreeGrammar result = new SimpleContextFreeGrammar();
		result.setStartSymbol(this.getStartSymbol());
		for (NonTerminal n : this.getNonTerminals())
		{
			result.addNonTerminal(n);
		}
		try
		{
			result.addRulesAutoAddSymbols(this.getRules());
		}
		catch (TooManyRulesException ex)
		{
			Logger.getLogger(SimpleContextFreeGrammar.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(
				"Here too many rules has no reason to be thrown unless you have made another class inheriting from "
					+ "SimpleContextFreeGrammar which should be final", ex);
		}
		for (Terminal t : this.getTerminals())
		{
			result.addTerminal(t);
		}
		return result;
	}

}
