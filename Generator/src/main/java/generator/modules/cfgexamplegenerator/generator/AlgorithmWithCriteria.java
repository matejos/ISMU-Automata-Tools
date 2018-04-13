/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author drasto
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AlgorithmWithCriteria
{

	private Set<Criteria> criteria = new HashSet();
	private AlgorithmType alType;

	public AlgorithmWithCriteria(AlgorithmType alType, Collection<Criteria> criteria)
	{
		if (alType == null)
		{
			throw new NullPointerException("alType");
		}
		if (criteria == null)
		{
			throw new NullPointerException("criteria");
		}
		this.alType = alType;
		this.criteria.addAll(criteria);
	}

	public AlgorithmWithCriteria(AlgorithmType alType, Criteria[] criteria)
	{
		if (alType == null)
		{
			throw new NullPointerException("alType");
		}
		if (criteria == null)
		{
			throw new NullPointerException("criteria");
		}
		this.alType = alType;
		for (Criteria c : criteria)
		{
			if (!c.getAlgType().equals(alType))
			{
				throw new IllegalArgumentException(
					"all criteria given to this AlgorithmWithCriteria must have algorithm type equals to "
						+ "given alType.");
			}
			this.criteria.add(c);
		}
	}

	public boolean hasCriteria()
	{
		return !criteria.isEmpty();
	}

	public AlgorithmType getAlTyepe()
	{
		return alType;
	}

	public Set<Criteria> getCriteria()
	{
		return Collections.unmodifiableSet(criteria);
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
		final AlgorithmWithCriteria other = (AlgorithmWithCriteria) obj;
		if (this.alType != other.alType)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return alType.hashCode();
	}

}
