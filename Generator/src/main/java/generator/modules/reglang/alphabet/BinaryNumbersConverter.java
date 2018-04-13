/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.alphabet;

/**
 * @author Jana Kadlecova
 */
public class BinaryNumbersConverter
{

	public static String numberToBinar(int number)
	{
		StringBuilder newNumber = new StringBuilder();
		int rest = 0;
		while (rest != 0 || number != 0)
		{
			rest = number % 2;
			number = number / 2;
			if (rest == 0 && number == 0)
			{
				break;
			}
			newNumber.append(String.valueOf(rest));
		}
		return newNumber.reverse().toString();
	}

	/**
	 * return decimal number as a string
	 * 
	 * @param s
	 *            string to convertion
	 * @return return decimal number as a string
	 * @throws NumberFormatException
	 *             given string cannot be parsed as a string
	 */
	public static int binarToDecimal(String s) throws NumberFormatException
	{
		return Integer.parseInt(s, 2);
	}
}
