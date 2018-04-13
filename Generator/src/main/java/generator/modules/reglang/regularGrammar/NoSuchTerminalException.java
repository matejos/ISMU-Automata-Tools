/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.regularGrammar;

/**
 * @author Jana Kadlecova
 */
public class NoSuchTerminalException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchTerminalException()
	{
	}

	public NoSuchTerminalException(String string)
	{
		super(string);
	}

}
