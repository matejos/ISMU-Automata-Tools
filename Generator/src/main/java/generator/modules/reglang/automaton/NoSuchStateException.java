/*
 * The exception
 */

package generator.modules.reglang.automaton;

/**
 * The exception, the state is not present in the automaton
 * 
 * @author Jana Kadlecova
 */
public class NoSuchStateException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of <code>NoSuchStateException</code> without detail message.
	 */
	public NoSuchStateException()
	{
	}

	/**
	 * Constructs an instance of <code>NoSuchStateException</code> with the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public NoSuchStateException(String msg)
	{
		super(msg);
	}
}
