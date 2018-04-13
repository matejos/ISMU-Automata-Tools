
package generator.modules.reglang.regularExpression;

public class RegularTransitionGraphEdge
{
	private RegularExpressionNode edgeValue;
	private String stateTo;
	private String stateFrom;

	public RegularTransitionGraphEdge()
	{
	}

	public RegularTransitionGraphEdge(RegularExpressionNode edgeValue, String stateTo, String stateFrom)
	{
		super();
		this.edgeValue = edgeValue;
		this.stateTo = stateTo;
		this.stateFrom = stateFrom;
	}

	public RegularTransitionGraphEdge(RegularTransitionGraphEdge edge)
	{
		super();
		this.edgeValue = new RegularExpressionNode(edge.getEdgeValue());
		this.stateTo = edge.getStateTo();
		this.stateFrom = edge.getStateFrom();
	}

	public RegularExpressionNode getEdgeValue()
	{
		return edgeValue;
	}
	public void setEdgeValue(RegularExpressionNode edgeValue)
	{
		this.edgeValue = edgeValue;
	}
	public String getStateTo()
	{
		return stateTo;
	}
	public void setStateTo(String stateTo)
	{
		this.stateTo = stateTo;
	}
	public String getStateFrom()
	{
		return stateFrom;
	}
	public void setStateFrom(String stateFrom)
	{
		this.stateFrom = stateFrom;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegularTransitionGraphEdge other = (RegularTransitionGraphEdge) obj;
		return toString().equals(other.toString());
	}

	@Override
	public String toString()
	{
		return stateFrom + "--" + edgeValue + "-->" + stateTo;
	}

}
