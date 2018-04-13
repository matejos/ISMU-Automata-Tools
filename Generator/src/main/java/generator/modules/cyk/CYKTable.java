
package generator.modules.cyk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author JUH POJ for CYK table.
 */
public class CYKTable
{
	List<List<Set<String>>> cykTable = new ArrayList<List<Set<String>>>();
	int differentSets = 0;
	int emptySets = 0;
	boolean generatable;

	public CYKTable(List<List<Set<String>>> cykTable, int differentSets, int emptySets, boolean generatable)
	{
		super();
		this.cykTable = cykTable;
		this.differentSets = differentSets;
		this.emptySets = emptySets;
		this.generatable = generatable;
	}

	public int getDifferentSets()
	{
		return differentSets;
	}

	public boolean isGeneratable()
	{
		return generatable;
	}

	public void setCanBeGenerated(boolean canBeGenerated)
	{
		this.generatable = canBeGenerated;
	}

	public void setDifferentSets(int differentSets)
	{
		this.differentSets = differentSets;
	}

	public int getEmptySets()
	{
		return emptySets;
	}

	public void setEmptySets(int emptySets)
	{
		this.emptySets = emptySets;
	}

	public List<List<Set<String>>> getCykTable()
	{
		return cykTable;
	}

	public void setCykTable(List<List<Set<String>>> cykTable)
	{
		this.cykTable = cykTable;
	}

}
