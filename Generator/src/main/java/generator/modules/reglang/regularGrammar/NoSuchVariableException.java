/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.regularGrammar;

/**
 * @author Jana Kadlecova
 */
public class NoSuchVariableException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchVariableException()
	{
	}

	public NoSuchVariableException(String string)
	{
		super(string);
	}

}
