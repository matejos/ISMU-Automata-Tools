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

public class SymbolAlreadyExistsException extends SymbolException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SymbolAlreadyExistsException()
	{
	}

	public SymbolAlreadyExistsException(String message)
	{
		super(message);
	}

	public SymbolAlreadyExistsException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
