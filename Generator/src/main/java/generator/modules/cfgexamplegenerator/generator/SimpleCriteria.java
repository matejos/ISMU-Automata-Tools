/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import javax.swing.JSpinner;

/**
 * @author drasto
 */
public class SimpleCriteria
{

	private AlgorithmType form;
	private CriteriaType type;
	private JSpinner spin;
	private String name;
	private int value;
	private boolean isLowerBound;
	private int bound;
	private boolean isFulfilled = true;
	private long notFulfilledCount = 0;
	private boolean notCritical = false;

	public SimpleCriteria(AlgorithmType form, CriteriaType type, boolean isLowerBound, int bound, JSpinner spin,
		int initialValue)
	{
		if (form == null)
		{
			throw new NullPointerException("form");
		}
		if (type == null)
		{
			throw new NullPointerException("type");
		}
		if (bound < 0)
		{
			throw new IllegalArgumentException("bound should always be greater then -1");
		}
		if (initialValue < 0)
		{
			throw new IllegalArgumentException("initial value should always be greater then -1");
		}

		this.bound = bound;
		this.form = form;
		this.isLowerBound = isLowerBound;
		this.type = type;
		this.spin = spin;
		setValue(initialValue);
	}

	public SimpleCriteria(AlgorithmType form, CriteriaType type, boolean isLowerBound, int bound, JSpinner spin,
		boolean notCritical)
	{
		this(form, type, isLowerBound, bound, spin, 0);
		this.notCritical = notCritical;
	}

	public SimpleCriteria(AlgorithmType form, CriteriaType type, boolean isLowerBound, int bound, String name,
		int initialValue)
	{
		if (form == null)
		{
			throw new NullPointerException("form");
		}
		if (type == null)
		{
			throw new NullPointerException("type");
		}
		if (bound < 0)
		{
			throw new IllegalArgumentException("bound should always be greater then -1");
		}
		if (name == null)
		{
			throw new NullPointerException("name");
		}
		if (initialValue < 0)
		{
			throw new IllegalArgumentException("initial value should always be greater then -1");
		}

		this.bound = bound;
		this.form = form;
		this.isLowerBound = isLowerBound;
		this.type = type;
		this.name = name;
		setValue(initialValue);
	}

	public int getBound()
	{
		return bound;
	}

	public boolean isIsLowerBound()
	{
		return isLowerBound;
	}

	public boolean isCritical()
	{
		return !isLowerBound && !notCritical;
	}

	public boolean isIsFulfilled()
	{
		return isFulfilled;
	}

	public AlgorithmType getForm()
	{
		return form;
	}

	public CriteriaType getType()
	{
		return type;
	}

	public int getValue()
	{
		return value;
	}

	public JSpinner getSpin()
	{
		return spin;
	}

	public void setValue(int value)
	{
		if (value < 0)
		{
			throw new IllegalArgumentException("value should always be greater or equal to zero");
		}

		if (isLowerBound)
		{
			isFulfilled = value >= bound;
		}
		else
		{
			isFulfilled = value <= bound;
		}

		this.value = value;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final SimpleCriteria other = (SimpleCriteria) obj;
		if (this.form != other.form)
		{
			return false;
		}
		if (this.type != other.type)
		{
			return false;
		}
		if (this.isLowerBound != other.isLowerBound)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 53 * hash + (this.form != null ? this.form.hashCode() : 0);
		hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
		hash = 53 * hash + (this.isLowerBound ? 1 : 0);
		return hash;
	}

	public long getNotFulfilledCount()
	{
		return notFulfilledCount;
	}

	public void wasNotFulfilled()
	{
		notFulfilledCount++;
	}

	@Override
	public String toString()
	{
		if (spin == null)
		{
			return name;
		}
		return spin.getName();
	}

}
