/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.alphabet;

import java.util.Comparator;

/**
 * @author Jana Kadlecova
 */
public class BinaryNumbersComparator<T> implements Comparator<T>
{

	public int compare(T o1, T o2) throws ClassCastException
	{
		if (o1 == null || o2 == null)
		{
			throw new NullPointerException("BinarAlphabetComparator");
		}
		if (!(o1 instanceof String) || !(o2 instanceof String))
		{
			throw new ClassCastException("BinarAlphabetComparator");
		}
		String s1 = (String) o1;
		String s2 = (String) o2;
		if (s1.equals(s2))
		{
			return 0;
		}
		if (s1.equals("epsilon"))
		{
			return 1;
		}
		if (s2.equals("epsilon"))
		{
			return -1;
		}
		boolean parseableo1 = true;
		boolean parseableo2 = true;
		try
		{
			BinaryNumbersConverter.binarToDecimal(s1);
		}
		catch (NumberFormatException e)
		{
			parseableo1 = false;
		}
		try
		{
			BinaryNumbersConverter.binarToDecimal(s2);
		}
		catch (NumberFormatException e)
		{
			parseableo2 = false;

		}
		if (!(parseableo1 && parseableo2))
		{
			if (!(parseableo1) && !(parseableo2))
			{
				return s1.compareTo(s2);
			}
			if (!parseableo1)
			{
				return 1;
			}
			return -1;
		}
		return BinaryNumbersConverter.binarToDecimal(s1) - BinaryNumbersConverter.binarToDecimal(s2);
	}

}
