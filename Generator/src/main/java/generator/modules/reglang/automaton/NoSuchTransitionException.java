/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.automaton;

/**
 * The exception, the transition is not present in the automaton
 * 
 * @author Jana Kadlecova
 */
public class NoSuchTransitionException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of <code>NoSuchTransitionException</code> without detail message.
	 */
	public NoSuchTransitionException()
	{
	}

	/**
	 * Constructs an instance of <code>NoSuchTransitionException</code> with the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public NoSuchTransitionException(String msg)
	{
		super(msg);
	}
}
