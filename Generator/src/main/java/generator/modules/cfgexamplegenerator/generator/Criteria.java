/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import javax.swing.JComponent;

/**
 * @author drasto
 */
public class Criteria
{

	private int upperBound;
	private int lowerBound;
	private JComponent max;
	private JComponent min;
	private CriteriaType type;
	private long exceededMax = 0;
	private long exceededMin = 0;
	private long boundCrossedAtMax = 0;
	private long exceededMaxAsHighest = 0;
	private long exceededMinAsLowest = 0;
	private int value = -1;
	private boolean wasMinExceeded = false;
	private boolean wasMaxExceeded = false;
	private boolean wasWorst = false;
	private boolean wasBoundCrossed = false;
	private AlgorithmType algType;

	public Criteria(int upperBound, int lowerBound, JComponent max, JComponent min, CriteriaType type,
		AlgorithmType algType)
	{
		if (upperBound <= 0 && lowerBound < 0)
		{
			throw new IllegalArgumentException(
				"this criteria has no reason to exist since both lower and upper bounds are less then zero");
		}
		if (upperBound > 0 && max == null)
		{
			throw new NullPointerException("upperBound is greater then zero but max is null");
		}
		if (lowerBound < 0 && min != null)
		{
			throw new IllegalStateException("if lowerBound < 0 min should not be set");
		}
		if (upperBound < 0 && max != null)
		{
			throw new IllegalStateException("if upperBound < 0 max should not be set");
		}
		if (lowerBound > 0 && min == null)
		{
			throw new NullPointerException("lowerBound is greater then zero but min == null");
		}
		if (type == null)
		{
			throw new NullPointerException("type");
		}
		if (algType == null)
		{
			throw new NullPointerException("algType");
		}
		this.algType = algType;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.max = max;
		this.min = min;
		this.type = type;
	}

	void setValue(int value)
	{
		if (value < 0)
		{
			throw new IllegalArgumentException("value is less then zero");
		}
		this.value = value;
	}

	public int getValue()
	{
		if (value < 0)
		{
			throw new IllegalStateException("vaule is not set yet");
		}
		return value;
	}

	public AlgorithmType getAlgType()
	{
		return algType;
	}

	public int getLowerBound()
	{
		return lowerBound;
	}

	public int getUpperBound()
	{
		return upperBound;
	}

	public JComponent getMax()
	{
		return max;
	}

	public JComponent getMin()
	{
		return min;
	}

	public CriteriaType getType()
	{
		return type;
	}

	public long getExededMax()
	{
		return exceededMax;
	}

	public long getExededMaxAsHighest()
	{
		return exceededMaxAsHighest;
	}

	public long getExededMin()
	{
		return exceededMin;
	}

	public long getExededMinAsLowest()
	{
		return exceededMinAsLowest;
	}

	void iterExededMax()
	{
		control();
		wasMaxExceeded = true;
		exceededMax++;
	}

	void iterExededMin()
	{
		control();
		wasMinExceeded = true;
		exceededMin++;
	}

	void iterExceededMaxAsHighest()
	{
		control();
		wasWorst = true;
		exceededMaxAsHighest++;
	}

	void iterExceededMinAsLowest()
	{
		control();
		wasWorst = true;
		exceededMinAsLowest++;
	}

	public boolean wasMaxExeded()
	{
		return wasMaxExceeded;
	}

	public boolean wasMinExeded()
	{
		return wasMinExceeded;
	}

	public boolean wasWorst()
	{
		return wasWorst;
	}

	void clearBooelans()
	{
		wasMaxExceeded = false;
		wasMinExceeded = false;
		wasBoundCrossed = false;
		wasWorst = false;
	}

	public long getBoundCrossedAtMax()
	{
		return boundCrossedAtMax;
	}

	void iterBoundCrossedAtMax()
	{
		boundCrossedAtMax++;
	}

	public boolean wasBoundCrossed()
	{
		return wasBoundCrossed;
	}

	void boundCrossed()
	{
		wasBoundCrossed = true;
	}

	private void control()
	{

	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Criteria other = (Criteria) obj;
		if (this.type != other.type)
		{
			return false;
		}
		if (this.algType != other.algType)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return 3 * type.hashCode() + 5 * algType.hashCode();
	}

}
