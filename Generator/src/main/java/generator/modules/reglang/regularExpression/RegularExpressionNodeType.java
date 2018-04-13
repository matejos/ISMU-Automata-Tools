
package generator.modules.reglang.regularExpression;

public enum RegularExpressionNodeType
{
	CHAR, CONCATENATION, UNION, ITERATION, EPS, EMPTY_SET;

	public boolean isOperation()
	{
		if (this.equals(CONCATENATION) || this.equals(UNION) || this.equals(ITERATION))
			return true;
		return false;
	}

	public boolean isBinaryNode()
	{
		if (this.equals(CONCATENATION) || this.equals(UNION))
			return true;
		return false;
	}

	public boolean isSingleChar()
	{
		switch (this)
		{
			case CHAR:
			case EPS:
			case EMPTY_SET:
			case ITERATION:
				return true;
			case CONCATENATION:
			case UNION:
				return false;
			default:
				return false;
		}
	}
}
