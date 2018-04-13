/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import generator.modules.cfgexamplegenerator.grammarreprezentation.AbstractSymbol.Epsilon;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Interface.java.
 * @copyright Rastislav Mirek all rights reserved
 */
public interface Symbol extends Comparable<Symbol>
{

	public static final String EPSILON_LETTER = "\u03b5";
	public static final AbstractSymbol.Epsilon EPSILON = Epsilon.getInstance();

	public String getName();
	public boolean isCorrectSymbol(String candidate);
	public boolean isTerminal();
	public boolean isEpsilon();
	public boolean isNonTerminal();
	@Override
	public boolean equals(Object o);
	@Override
	public int hashCode();
	@Override
	public String toString();
}
