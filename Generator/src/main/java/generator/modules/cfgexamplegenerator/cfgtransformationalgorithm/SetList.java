/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This Object represents a Set with ordering. It can contain each element only once. If the same element is added to
 * the SetList more then one time the second and following additions are ignored(so the ordering remain unchanged). For
 * multiple additions with set properities see class Multiset and OrderedMultiset.
 * 
 * @author drasto, bafco
 */
public class SetList<E> implements Set<E>, List<E>, Cloneable
{

	private Set<E> s;
	private List<E> l;

	/**
	 * Constructs an empty SetList with an initial capacity of ten.
	 */
	public SetList()
	{
		s = new HashSet<E>();
		l = new ArrayList<E>();
	}

	/**
	 * Constructs an empty SetList with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the SetList
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	public SetList(int initialCapacity)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		s = new HashSet<E>(initialCapacity);
		l = new ArrayList<E>(initialCapacity);
	}

	/**
	 * Constructs a SetList containing the elements of the specified collection, in the order they are returned by the
	 * collection's iterator.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this SetList
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public SetList(Collection<? extends E> c)
	{
		l = new ArrayList<E>();
		s = new HashSet<E>();
		for (E e : c)
		{
			add(e);
		}
	}

	/**
	 * Returns the number of elements in this SetList.
	 * 
	 * @return the number of elements in this SetList
	 */
	public int size()
	{
		return l.size();
	}

	/**
	 * Returns <tt>true</tt> if this SetList contains no elements.
	 * 
	 * @return <tt>true</tt> if this SetList contains no elements
	 */
	public boolean isEmpty()
	{
		return s.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this SetList contains the specified element. More formally, returns <tt>true</tt> if and
	 * only if this SetList contains at least one element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * 
	 * @param o
	 *            element whose presence in this SetList is to be tested
	 * @return <tt>true</tt> if this SetList contains the specified element
	 */
	public boolean contains(Object o)
	{
		return s.contains(o);
	}

	/**
	 * Returns an iterator over the elements in this SetList. The elements are returned in no particular order.
	 * 
	 * @return an Iterator over the elements in this SetList
	 * @see ConcurrentModificationException
	 */
	public Iterator<E> iterator()
	{
		return new IteratorImpl();
	}

	/**
	 * Returns an array containing all of the elements in this SetList in proper sequence (from first to last element).
	 * <p>
	 * The returned array will be "safe" in that no references to it are maintained by this SetList. (In other words,
	 * this method must allocate a new array). The caller is thus free to modify the returned array.
	 * <p>
	 * This method acts as bridge between array-based and collection-based APIs.
	 * 
	 * @return an array containing all of the elements in this SetList in proper sequence
	 */
	public Object[] toArray()
	{
		return l.toArray();
	}

	/**
	 * Returns an array containing all of the elements in this SetList in proper sequence (from first to last element);
	 * the runtime type of the returned array is that of the specified array. If the SetList fits in the specified
	 * array, it is returned therein. Otherwise, a new array is allocated with the runtime type of the specified array
	 * and the size of this SetList.
	 * <p>
	 * If the SetList fits in the specified array with room to spare (i.e., the array has more elements than the
	 * SetList), the element in the array immediately following the end of the collection is SetList to <tt>null</tt>.
	 * (This is useful in determining the length of the SetList <i>only</i> if the caller knows that the SetList does
	 * not contain any null elements.)
	 * 
	 * @param a
	 *            the array into which the elements of the SetList are to be stored, if it is big enough; otherwise, a
	 *            new array of the same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the SetList
	 * @throws ArrayStoreException
	 *             if the runtime type of the specified array is not a supertype of the runtime type of every element in
	 *             this SetList
	 * @throws NullPointerException
	 *             if the specified array is null
	 */
	public <T> T[] toArray(T[] a)
	{
		return l.toArray(a);
	}

	/**
	 * Adds the specified element to end of this SetList if it is not already present in it. More formally, adds the
	 * specified element <tt>e</tt> to this SetList if this SetList contains no element <tt>e2</tt> such that
	 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this SetList already contains the element,
	 * the call leaves the SetList unchanged and returns <tt>false</tt>.
	 * 
	 * @param e
	 *            element to be added to this SetList
	 * @return <tt>true</tt> if this SetList did not already contain the specified element
	 */
	public boolean add(E e)
	{
		if (s.contains(e))
		{
			return false;
		}
		s.add(e);
		l.add(e);
		return true;
	}

	/**
	 * Removes the specified element from this SetList if it is present. More formally, removes an element <tt>e</tt>
	 * such that <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if this SetList contains such an
	 * element. Returns <tt>true</tt> if this SetList contained the element (or equivalently, if this SetList changed as
	 * a result of the call). (This SetList will not contain the element once the call returns.)
	 * 
	 * @param o
	 *            object to be removed from this SetList, if present
	 * @return <tt>true</tt> if the SetList contained the specified element
	 */
	public boolean remove(Object o)
	{
		if (!s.contains(o))
		{
			return false;
		}
		l.remove(o);
		s.remove(o);
		return true;
	}

	public boolean containsAll(Collection<?> c)
	{
		return s.containsAll(c);
	}

	public boolean addAll(Collection<? extends E> c)
	{
		boolean result = false;
		for (E e : c)
		{
			result = add(e) | result;
		}
		return result;
	}

	public boolean retainAll(Collection<?> c)
	{
		List<E> nl = new ArrayList<E>();
		Set<E> ns = new HashSet<E>();
		// int i = 0;
		for (E e : l)
		{
			if (c.contains(e))
			{
				nl.add(e);
				ns.add(e);
				// i++;
			}
		}
		if (s.size() == ns.size())
		{
			return false;
		}
		s = ns;
		l = nl;
		return true;
	}

	public boolean removeAll(Collection<?> c)
	{
		boolean result = false;
		for (Object e : c)
		{
			result = remove(e) | result;
		}
		return result;
	}

	public void clear()
	{
		s.clear();
		l.clear();
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		int oldSize = s.size();
		int i = 0;
		for (E e : c)
		{
			add(index + i, e);
		}
		return s.size() != oldSize;
	}

	public E get(int index)
	{
		return l.get(index);
	}

	public E set(int index, E element)
	{
		if (s.contains(element))
		{
			throw new IllegalArgumentException(element + " is already contained in SetList");
		}
		E result = l.set(index, element);
		s.add(element);
		return result;
	}

	public void add(int index, E element)
	{
		if (s.contains(element))
		{
			return;
		}
		l.add(index, element);
		s.add(element);
	}

	public E remove(int index)
	{
		E result = l.remove(index);
		s.remove(result);
		return result;
	}

	public int indexOf(Object o)
	{
		return l.indexOf(o);
	}

	public int lastIndexOf(Object o)
	{
		return l.lastIndexOf(o);
	}

	public ListIterator<E> listIterator()
	{
		return new IteratorImpl();
	}

	@Deprecated
	public ListIterator<E> listIterator(int index)
	{
		throw new UnsupportedOperationException("Not supported by this SetList.");
	}

	public List<E> subList(int fromIndex, int toIndex)
	{
		return l.subList(fromIndex, toIndex);
	}

	@Override
	public SetList<E> clone()
	{
		return new SetList<E>(this);
	}

	private class IteratorImpl implements Iterator<E>, ListIterator<E>
	{

		public IteratorImpl()
		{
		}
		int index = 0;
		int size = s.size();

		public boolean hasNext()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			return index < size;
		}

		public E next()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			index++;
			return l.get(index - 1);
		}

		public void remove()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			SetList.this.remove(index - 1);
			size--;
		}

		public boolean hasPrevious()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			l.listIterator().hasPrevious();
			return index - 2 >= 0;
		}

		public E previous()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			index--;
			return l.get(index - 1);
		}

		public int nextIndex()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			return index;
		}

		public int previousIndex()
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			return index - 2;
		}

		@Deprecated
		public void set(E e)
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			throw new UnsupportedOperationException("Not supported by this iterator");
		}

		@Deprecated
		public void add(E e)
		{
			if (s.size() != size)
			{
				throw new ConcurrentModificationException(
					"The size of of SetList was changed during iteration by means other then iterators "
						+ "own methods");
			}
			throw new UnsupportedOperationException("not supported by this iterator");
		}
	}

}
