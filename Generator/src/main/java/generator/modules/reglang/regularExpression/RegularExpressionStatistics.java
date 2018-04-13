
package generator.modules.reglang.regularExpression;

public class RegularExpressionStatistics
{
	private int unionCount;
	private int iterationCount;
	private int concatenationCount;
	private boolean hasEps;

	public RegularExpressionStatistics(int unionCount, int iterationCount, int concatenationCount, boolean hasEps)
	{
		super();
		this.unionCount = unionCount;
		this.iterationCount = iterationCount;
		this.concatenationCount = concatenationCount;
		this.hasEps = hasEps;
	}

	public boolean isHasEps()
	{
		return hasEps;
	}

	public void setHasEps(boolean hasEps)
	{
		this.hasEps = hasEps;
	}

	public int getUnionCount()
	{
		return unionCount;
	}
	public void setUnionCount(int unionCount)
	{
		this.unionCount = unionCount;
	}
	public int getIterationCount()
	{
		return iterationCount;
	}
	public void setIterationCount(int iterationCount)
	{
		this.iterationCount = iterationCount;
	}
	public int getConcatenationCount()
	{
		return concatenationCount;
	}
	public void setConcatenationCount(int concatenationCount)
	{
		this.concatenationCount = concatenationCount;
	}

	@Override
	public String toString()
	{
		return "RegularExpressionStatistics [unionCount=" + unionCount + ", iterationCount=" + iterationCount
			+ ", concatenationCount=" + concatenationCount + ", hasEps=" + hasEps + "]";
	}

}
