/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Interface.java.
 * @copyright Rastislav Mirek all rights reserved
 */
public interface ContextFreeRule extends Cloneable, Comparable<ContextFreeRule>
{

	public static final String ARROW = " \u2192 ";
	public static final String RIGHT_SIDE_SEPARATOR = " | ";

	public NonTerminal getSymbolOnLeftSide();
	public Symbol[] getSymbolsOnRightSide();
	public Symbol getFirstRightSymbol();
	public Symbol getRightSideSymbolAt(int position);
	public int getRightSymbolsCount();
	public String leftSideToString();
	public String rightSideToString();
	public boolean isEpsilonRule();
	public boolean isSimpleRule();
	public boolean isTerminalRule();
	@Override
	public String toString();
	@Override
	public boolean equals(Object o);
	@Override
	public int hashCode();
}
