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

public final class SimpleContextFreeRule extends AbstractContextFreeRule
{

	public SimpleContextFreeRule(NonTerminal left, Symbol[] right)
	{
		super(left, right);
	}

	@Override
	public ContextFreeRule clone()
	{
		return new SimpleContextFreeRule(getSymbolOnLeftSide(), getSymbolsOnRightSide());
	}
}
