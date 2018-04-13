/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.alphabet;

import java.util.Comparator;

/**
 * @author Jana Kadlecova
 */
public class RomanNumbersComparator<T> implements Comparator<T>
{

	public int compare(T o1, T o2)
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
		int i1 = 0;
		int i2 = 0;
		try
		{
			i1 = RomanNumbersConverter.romanToArabic(s1);
		}
		catch (NumberFormatException e)
		{
			parseableo1 = false;
		}
		try
		{
			i2 = RomanNumbersConverter.romanToArabic(s2);
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
		return i1 - i2;
	}

}
