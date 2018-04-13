/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.alphabet;

import java.util.Comparator;

/**
 * @author Jana Kadlecova
 */
public class AlphabetComparator<T> implements Comparator<T>
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
		if (s1.length() != s2.length())
		{
			return s1.length() - s2.length();
		}
		return s1.compareTo(s2);
	}

}
