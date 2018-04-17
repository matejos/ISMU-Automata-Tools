
package generator.modules.reglang.regularExpression;

/**
 * Class for representing regular expressions via nodes - binary tree. Iteration has one child, concatenation and union
 * has two children. Node can be either operation or char.
 * 
 * @author JUH
 */
public class RegularExpressionNode
{
	private RegularExpressionNodeType nodeType;
	private RegularExpressionNode leftChild;
	private RegularExpressionNode rightChild;
	private String alphChar;
	private RegularExpressionNode parent;

	public RegularExpressionNode(RegularExpressionNodeType nodeType)
	{
		super();
		this.nodeType = nodeType;
	}

	public RegularExpressionNode(RegularExpressionNode node)
	{
		super();
		this.nodeType = node.getNodeType();
		this.leftChild = node.getLeftChild();
		this.rightChild = node.getRightChild();
		this.alphChar = node.getAlphChar();
		this.parent = node.getParent();
	}

	public RegularExpressionNode(String alphChar)
	{
		super();
		this.nodeType = RegularExpressionNodeType.CHAR;
		this.alphChar = alphChar;
	}

	public RegularExpressionNodeType getNodeType()
	{
		return nodeType;
	}
	public void setNodeType(RegularExpressionNodeType nodeType)
	{
		this.nodeType = nodeType;
	}
	public RegularExpressionNode getLeftChild()
	{
		return leftChild;
	}
	public void setLeftChild(RegularExpressionNode leftChild)
	{
		this.leftChild = leftChild;
	}
	public RegularExpressionNode getRightChild()
	{
		return rightChild;
	}
	public void setRightChild(RegularExpressionNode rightChild)
	{
		this.rightChild = rightChild;
	}

	public String getAlphChar()
	{
		return alphChar;
	}
	public void setAlphChar(String alphChar)
	{
		this.alphChar = alphChar;
	}
	public RegularExpressionNode getIterationChild()
	{
		return leftChild;
	}

	public void setIterationChild(RegularExpressionNode child)
	{
		this.leftChild = child;
	}

	public RegularExpressionNode getParent()
	{
		return parent;
	}

	public void setParent(RegularExpressionNode parent)
	{
		this.parent = parent;
	}

	public RegularExpressionNode addIterationChild(String alphChar)
	{
		if (!nodeType.equals(RegularExpressionNodeType.ITERATION))
		{
			throw new UnsupportedOperationException("Trying to add iteration child to non-iteration node.");
		}
		if (leftChild != null)
		{
			throw new UnsupportedOperationException("Iteration node already contains child.");
		}

		if (alphChar == null)
		{
			throw new IllegalArgumentException("Char not given");
		}
		RegularExpressionNode newChild = new RegularExpressionNode(alphChar);

		this.setIterationChild(newChild);
		return getIterationChild();
	}

	public RegularExpressionNode addIterationChild(RegularExpressionNodeType type)
	{
		if (!nodeType.equals(RegularExpressionNodeType.ITERATION))
		{
			throw new UnsupportedOperationException("Trying to add iteration child to non-iteration node.");
		}
		if (leftChild != null)
		{
			throw new UnsupportedOperationException("Iteration node already contains child.");
		}
		RegularExpressionNode newChild = new RegularExpressionNode(type);
		this.setIterationChild(newChild);
		return getIterationChild();
	}

	public RegularExpressionNode addLeftChild(String alphChar)
	{
		if (leftChild != null)
		{
			throw new UnsupportedOperationException("Node already contains left child.");
		}

		if (alphChar == null)
		{
			throw new IllegalArgumentException("Char not given");
		}
		RegularExpressionNode newLeftChild = new RegularExpressionNode(alphChar);
		leftChild = newLeftChild;
		leftChild.setParent(this);
		return leftChild;
	}

	public RegularExpressionNode addLeftChild(RegularExpressionNodeType type)
	{
		if (leftChild != null)
		{
			throw new UnsupportedOperationException("Node already contains left child.");
		}

		RegularExpressionNode newLeftChild = new RegularExpressionNode(type);

		leftChild = newLeftChild;
		leftChild.setParent(this);
		return leftChild;
	}

	public RegularExpressionNode addRightChild(String alphChar)
	{
		if (rightChild != null)
		{
			throw new UnsupportedOperationException("Node already contains right child.");
		}
		if (alphChar == null)
		{
			throw new IllegalArgumentException("Char not given");
		}
		RegularExpressionNode newRightChild = new RegularExpressionNode(alphChar);
		rightChild = newRightChild;
		rightChild.setParent(this);
		return rightChild;
	}

	public RegularExpressionNode addRightChild(RegularExpressionNodeType type)
	{
		if (rightChild != null)
		{
			throw new UnsupportedOperationException("Node already contains right child.");
		}
		RegularExpressionNode newRightChild = new RegularExpressionNode(type);
		rightChild = newRightChild;
		rightChild.setParent(this);
		return rightChild;
	}

	@Override
	public String toString()
	{
		String expression = "";
		switch (nodeType)
		{
			case CONCATENATION:
				if (leftChild.getNodeType().isSingleChar() && rightChild.getNodeType().isSingleChar())
				{

					if (leftChild.getNodeType().equals(RegularExpressionNodeType.EPS)
						&& rightChild.getNodeType().equals(RegularExpressionNodeType.EPS))
					{
						expression = "eps";
						break;
					}
					if (leftChild.getNodeType().equals(RegularExpressionNodeType.EPS))
					{
						expression = rightChild.toString();
						break;
					}
					if (rightChild.getNodeType().equals(RegularExpressionNodeType.EPS))
					{
						expression = leftChild.toString();
						break;
					}
					expression = "(" + leftChild.toString() + "." + rightChild.toString() + ")";
				}
				else
				{
					if (leftChild.getNodeType().equals(RegularExpressionNodeType.EPS))
					{
						expression = rightChild.toString();
						break;
					}
					if (rightChild.getNodeType().equals(RegularExpressionNodeType.EPS))
					{
						expression = leftChild.toString();
						break;
					}
					expression = "(" + leftChild.toString() + "." + rightChild.toString() + ")";
				}
				break;
			case UNION:
				if (leftChild.getNodeType().isSingleChar() && rightChild.getNodeType().isSingleChar())
				{
					expression = "(" + leftChild.toString() + "+" + rightChild.toString() + ")";
				}
				else
				{
					expression = "(" + leftChild.toString() + "+" + rightChild.toString() + ")";
				}
				break;
			case ITERATION:
				expression = getIterationChild().toString() + "*";
				break;
			case CHAR:
				expression = alphChar;
				break;
			case EMPTY_SET:
				expression = "\u2205";
				break;
			case EPS:
				expression = "\u03b5";
				break;

			default:
				break;
		}
		return expression;
	}

	public String toStringWithEpsConcat()
	{
		String expression = "";
		switch (nodeType)
		{
			case CONCATENATION:
				if (leftChild.getNodeType().isSingleChar() && rightChild.getNodeType().isSingleChar())
				{
					expression = "(" + leftChild.toStringWithEpsConcat() + "." + rightChild.toStringWithEpsConcat()
						+ ")";
				}
				else
				{
					expression = "(" + leftChild.toStringWithEpsConcat() + "." + rightChild.toStringWithEpsConcat()
						+ ")";
				}
				break;
			case UNION:
				if (leftChild.getNodeType().isSingleChar() && rightChild.getNodeType().isSingleChar())
				{
					expression = "(" + leftChild.toStringWithEpsConcat() + "+" + rightChild.toStringWithEpsConcat()
						+ ")";
				}
				else
				{
					expression = "(" + leftChild.toStringWithEpsConcat() + "+" + rightChild.toStringWithEpsConcat()
						+ ")";
				}
				break;
			case ITERATION:
				expression = getIterationChild().toStringWithEpsConcat() + "*";
				break;
			case CHAR:
				expression = alphChar;
				break;
			case EMPTY_SET:
				expression = "\u2205";
				break;
			case EPS:
				expression = "\u03b5";
				break;

			default:
				break;
		}
		return expression;
	}

	public String toPlainTextWithEpsConcat()
	{
		String resultString = toStringWithEpsConcat();
		if (resultString.startsWith("(") && resultString.endsWith(")"))
		{
			resultString = resultString.substring(1, resultString.length() - 1);
		}
		return resultString;
	}

	public String toPlainText()
	{
		String resultString = toString();
		if (resultString.startsWith("(") && resultString.endsWith(")"))
		{
			resultString = resultString.substring(1, resultString.length() - 1);
		}
		return resultString;
	}
	
	public String toLatexText()
	{
		String resultString = toString();
		if (resultString.startsWith("(") && resultString.endsWith(")"))
		{
			resultString = resultString.substring(1, resultString.length() - 1);
		}
		resultString = resultString.replace("\u2205", "\\emptyset").replace("\u03b5", "\\varepsilon").replace("*", "^*");
		
		return resultString;
	}
	
	public String toLatexWithEpsConcat()
	{
		String resultString = toStringWithEpsConcat();
		if (resultString.startsWith("(") && resultString.endsWith(")"))
		{
			resultString = resultString.substring(1, resultString.length() - 1);
		}
		resultString = resultString.replace("\u2205", "\\emptyset").replace("\u03b5", "\\varepsilon").replace("*", "^*");
		return resultString;
	}

	public String toIS(String student)
	{
		String resultString = toString();
		if (resultString.startsWith("(") && resultString.endsWith(")"))
		{
			resultString = resultString.substring(1, resultString.length() - 1);
		}
		resultString = resultString.replace("*", "^*");
		StringBuilder sb = new StringBuilder();
		sb.append(" :e" + "\n");
		sb.append(":e=\"f:");
		sb.append("REG-" + student + ":");
		sb.append(resultString);
		sb.append("\" ok\n");
		return sb.toString();
	}

	public String toISText()
	{
		String resultString = toLatexText();
		resultString = "<m>" + resultString + "</m>";
		return resultString;
	}

	public RegularExpressionStatistics getStatistics()
	{
		String expr = toPlainText();
		int concatCount = expr.length() - expr.replace(".", "").length();
		int unionCount = expr.length() - expr.replace("+", "").length();
		int iterCount = expr.length() - expr.replace("*", "").length();
		boolean hasEps = false;
		if (expr.contains("\u03b5"))
			hasEps = true;
		RegularExpressionStatistics stats = new RegularExpressionStatistics(unionCount, iterCount, concatCount, hasEps);
		return stats;
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
		RegularExpressionNode other = (RegularExpressionNode) obj;
		return this.toString().equals(other.toString());
	}

}
