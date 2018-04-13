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

public class SymbolException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SymbolException()
	{
	}

	public SymbolException(String message)
	{
		super(message);
	}

	public SymbolException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
