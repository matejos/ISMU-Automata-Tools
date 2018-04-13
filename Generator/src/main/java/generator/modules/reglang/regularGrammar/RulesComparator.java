/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.regularGrammar;

import generator.modules.reglang.alphabet.AlphabetComparator;
import generator.modules.reglang.alphabet.BinaryNumbersComparator;
import generator.modules.reglang.alphabet.NumbersComparator;
import generator.modules.reglang.alphabet.RomanNumbersComparator;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The supporting class
 * 
 * @author Jana Kadlecova
 */
public class RulesComparator<List> implements Comparator<List>
{

	int terminalsType;
	int variablesType;

	/**
	 * @param terminalsType
	 *            0: a,b,c 1: A,B,C 2: 1,2,3 3: x,y,z 4: X,Y,Z 5: s0,s1,s2 6: t0, t1, t2 7: I, II, III 8: i, ii, iii 9:
	 *            1, 10, 11, 100
	 * @param variablesType
	 */
	public RulesComparator(int terminalsType, int variablesType)
	{
		this.terminalsType = terminalsType;
		this.variablesType = variablesType;
	}

	@SuppressWarnings("unchecked")
	public int compare(List o1, List o2)
	{
		if (o1 == null || o2 == null)
		{
			throw new NullPointerException("RulesComparator");
		}
		if (!(o1 instanceof ArrayList) || !(o2 instanceof ArrayList))
		{
			throw new ClassCastException("RulesComparator");
		}

		// ArrayList<String> list1 = (ArrayList<String>) o1;
		// ArrayList<String> list2 = (ArrayList<String>) o2;
		// zamena
		/*
		 * zmena 2 ArrayList<String> list1 = new ArrayList<String>(); ArrayList<String> list2 = new ArrayList<String>();
		 * list1.addAll((Collection<String>)o1); list2.addAll((Collection<String>)o2);
		 */
		ArrayList<String> list1 = (ArrayList<String>) o1;
		ArrayList<String> list2 = (ArrayList<String>) o2;

		// konec zameny

		if (list1.size() > list2.size())
		{
			return -1;
		}
		if (list1.size() < list2.size())
		{
			return 1;
		}
		int i = 0;
		Comparator<String> c = this.getComparator(terminalsType);
		i = c.compare(list1.get(0), list2.get(0));
		if (i != 0 || (list1.size() == 1 && list2.size() == 1))
		{
			return i;
		}
		else
		{
			c = this.getComparator(variablesType);
		}
		return c.compare(list1.get(1), list2.get(1));
	}

	private Comparator<String> getComparator(int type)
	{
		Comparator<String> c;
		switch (type)
		{
			case 2:
				c = new NumbersComparator<String>();
				break;
			case 7:
				c = new RomanNumbersComparator<String>();
				break;
			case 8:
				c = new RomanNumbersComparator<String>();
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

}
