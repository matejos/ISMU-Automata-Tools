/*
 * Makes new alphabet of given type, makes new sorted alphabet of given type, makes new Comparator to the alphabet of
 * given type or sort the alphabet of given type
 */

package generator.modules.reglang.alphabet;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Class AlphabetGenerator stands for the generator of alphabets, which can be of given type
 * 
 * @author Jana Kadlecova
 */
public class AlphabetGenerator
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private AlphabetGenerator()
	{
	}

	/**
	 * returns alphabet a, b, c, d ... z1, z2, z3 as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @return alphabet a, b, c, d ... z1, z2, z3 as the sorted set
	 */
	public static SortedSet<String> getLowerCase(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new AlphabetComparator<String>());
		char c = 'a';
		for (int i = 0; i < count; i++)
		{
			if (i < 26)
			{
				alphabet.add(Character.toString(c));
				c++;
			}
			else
			{
				alphabet.add('z' + "_{" + Integer.toString(i - 25) + "}");
			}
		}
		return alphabet;
	}

	/**
	 * returns alphabet A, B, C, ... Z, Z1, Z2, ... as the sorted set
	 * 
	 * @param count
	 *            numbet of elements
	 * @return alphabet A, B, C, ... Z, Z1, Z2, ... as the sorted set
	 */
	public static SortedSet<String> getUpperCase(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new AlphabetComparator<String>());
		char c = 'A';
		for (int i = 0; i < count; i++)
		{
			if (i < 26)
			{
				alphabet.add(Character.toString(c));
				c++;
			}
			else
			{
				alphabet.add('Z' + "_{" + Integer.toString(i - 25) + "}");
			}
		}
		return alphabet;
	}

	/**
	 * returns alhabet 1, 2, 3, .. as the sorted set
	 * 
	 * @param count
	 *            numbet of elements
	 * @return alhabet 1, 2, 3, .. as the sorted set
	 */
	public static SortedSet<String> getNumbers(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new NumbersComparator<String>());
		for (int i = 1; i <= count; i++)
		{
			alphabet.add(String.valueOf(i));
		}
		return alphabet;
	}

	/**
	 * returns alphabet I, II, III, IV... as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @return alphabet I, II, III, IV... as the sorted set
	 */
	public static SortedSet<String> getRomanNumbers(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new RomanNumbersComparator<String>());
		for (int i = 1; i <= count; i++)
		{
			alphabet.add(RomanNumbersConverter.arabicToRoman(i));
		}
		return alphabet;
	}

	/**
	 * returns alphabet i, ii, iii, iv... as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @return alphabet i, ii, iii, iv... as the sorted set
	 */
	public static SortedSet<String> getLowerRomanNumbers(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new RomanNumbersComparator<String>());
		for (int i = 1; i <= count; i++)
		{
			alphabet.add(RomanNumbersConverter.arabicToRoman(i).toLowerCase());
		}
		return alphabet;
	}

	/**
	 * returns alphabet 1, 10, 11, 100... as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @return alphabet 1, 10, 11, 100... as the sorted set
	 */
	public static SortedSet<String> getBinaryNumbers(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new BinaryNumbersComparator<String>());
		for (int i = 1; i <= count; i++)
		{
			alphabet.add(BinaryNumbersConverter.numberToBinar(i));
		}
		return alphabet;
	}

	/**
	 * returns the alphabet letter1, letter2, letter3... as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @param letter
	 * @return the alphabet letter1, letter2, letter3... as the sorted set
	 */
	public static SortedSet<String> getLetterWithIndex(int count, String letter)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new AlphabetComparator<String>());
		for (int i = 0; i < count; i++)
		{
			alphabet.add(letter + "_{" + String.valueOf(i) + "}");
		}
		return alphabet;
	}

	/**
	 * returns alphabet x,y,z ... z1, z2, z3 as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @return alphabet z, y, z ... z1, z2, z3 as the sorted set
	 */
	public static SortedSet<String> getLowerCaseEnd(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new AlphabetComparator<String>());
		char c = 'z';
		for (int i = 0; i < count; i++)
		{
			if (i < 26)
			{
				alphabet.add(Character.toString(c));
				c--;
			}
			else
			{
				alphabet.add('z' + "_{" + Integer.toString(i - 25) + "}");
			}
		}
		return alphabet;
	}

	/**
	 * returns alphabet X, Y, Z... Z1, Z2, Z3 as the sorted set
	 * 
	 * @param count
	 *            number of elements
	 * @return alphabet X, Y, Z... Z1, Z2, Z3 as the sorted set
	 */
	public static SortedSet<String> getUpperCaseEnd(int count)
	{
		SortedSet<String> alphabet = new TreeSet<String>(new AlphabetComparator<String>());
		char c = 'Z';
		for (int i = 0; i < count; i++)
		{
			if (i < 26)
			{
				alphabet.add(Character.toString(c));
				c--;
			}
			else
			{
				alphabet.add('Z' + "_{" + Integer.toString(i - 25) + "}");
			}
		}
		return alphabet;
	}

	/**
	 * returns given set as new sorted set
	 * 
	 * @param set
	 *            set to sort
	 * @param type
	 *            0 a,b,c 1 A,B,C 2 1,2,3 3 x,y,z 4 X,Y,Z 5 t0,t1, t2 (q0, q1, q2) 6 p0, p1, p2 (s0, s1, s2) 7 I,II,III,
	 *            IV 8 i, ii, iii, iv 9 1, 10, 11, 100
	 * @return given set as new sorted set
	 */
	public static SortedSet<String> sortSet(Set<String> set, int type)
	{
		SortedSet<String> sortedCollection;
		switch (type)
		{
			case 7:
				sortedCollection = new TreeSet<String>(new RomanNumbersComparator<String>());
				sortedCollection.addAll(set);
				break;
			case 8:
				sortedCollection = new TreeSet<String>(new RomanNumbersComparator<String>());
				sortedCollection.addAll(set);
				break;
			case 2:
				sortedCollection = new TreeSet<String>(new NumbersComparator<String>());
				sortedCollection.addAll(set);
				break;
			case 9:
				sortedCollection = new TreeSet<String>(new BinaryNumbersComparator<String>());
				sortedCollection.addAll(set);
				break;
			default:
				sortedCollection = new TreeSet<String>(new AlphabetComparator<String>());
				sortedCollection.addAll(set);
				break;
		}
		return sortedCollection;

	}

	/**
	 * returns the comparator of the given type of alphabet
	 * 
	 * @param type
	 *            0 a,b,c 1 A,B,C 2 1,2,3 3 x,y,z 4 X,Y,Z 5 t0,t1, t2 (q0, q1, q2) 6 p0, p1, p2 (s0, s1, s2) 7 I,II,III,
	 *            IV 8 i, ii, iii, iv 9 1, 10, 11, 100
	 * @return comparator of the given type of alphabet
	 */
	public static Comparator<String> getComparator(int type)
	{
		Comparator<String> c;
		switch (type)
		{
			case 7:
				c = new RomanNumbersComparator<String>();
				break;
			case 8:
				c = new RomanNumbersComparator<String>();
				break;
			case 2:
				c = new NumbersComparator<String>();
				break;
			case 9:
				c = new BinaryNumbersComparator<String>();
				break;
			default:
				c = new AlphabetComparator<String>();
				break;
		}
		return c;
	}

	/**
	 * returns new sorted set with the alphabet of given type with given number of elements
	 * 
	 * @param type
	 *            0 a,b,c 1 A,B,C 2 1,2,3 3 x,y,z 4 X,Y,Z 5 t0,t1, t2 (q0, q1, q2) 6 p0, p1, p2 (s0, s1, s2) 7 I,II,III,
	 *            IV 8 i, ii, iii, iv 9 1, 10, 11, 100
	 * @param count
	 *            number of elements
	 * @return new sorted set with the alphabet of given type with given number of elements
	 */
	public static SortedSet<String> getNewSortedAlphabet(int type, int count)
	{
		if (type < 0 || type > 9 || count < 0)
		{
			throw new IllegalArgumentException("AlphabetBuilder.getNewSortedAlphabet");
		}
		SortedSet<String> alphabet = new TreeSet<String>();
		switch (type)
		{
			case 0:
				alphabet = AlphabetGenerator.getLowerCase(count);
				break;
			case 1:
				alphabet = AlphabetGenerator.getUpperCase(count);
				break;
			case 2:
				alphabet = AlphabetGenerator.getNumbers(count);
				break;
			case 3:
				alphabet = AlphabetGenerator.getLowerCaseEnd(count);
				break;
			case 4:
				alphabet = AlphabetGenerator.getUpperCaseEnd(count);
				break;
			case 5:
				alphabet = AlphabetGenerator.getLetterWithIndex(count, "t");
				break;
			case 6:
				alphabet = AlphabetGenerator.getLetterWithIndex(count, "p");
				break;
			case 7:
				alphabet = AlphabetGenerator.getRomanNumbers(count);
				break;
			case 8:
				alphabet = AlphabetGenerator.getLowerRomanNumbers(count);
				break;
			case 9:
				alphabet = AlphabetGenerator.getBinaryNumbers(count);
				break;
		}
		return alphabet;
	}

}
