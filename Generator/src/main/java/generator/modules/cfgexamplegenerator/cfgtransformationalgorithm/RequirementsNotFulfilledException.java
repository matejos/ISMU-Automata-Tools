/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */

public class RequirementsNotFulfilledException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequirementsNotFulfilledException()
	{

	}

	public RequirementsNotFulfilledException(String message)
	{
		super(message);
	}

	public RequirementsNotFulfilledException(Throwable cause)
	{
		super(cause);
	}

	public RequirementsNotFulfilledException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
