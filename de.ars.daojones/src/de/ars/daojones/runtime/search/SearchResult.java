package de.ars.daojones.runtime.search;

import de.ars.daojones.runtime.Dao;

/**
 * A result entry of the search.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the DaoJones bean type.
 */
public abstract class SearchResult<T extends Dao> implements
		Comparable<SearchResult<T>> {

	private final T object;
	private final long priority;

	/**
	 * Creates an instance.
	 * 
	 * @param object
	 *            the search result
	 * @param priority
	 *            the priority for ordering purposes
	 */
	protected SearchResult(final T object, final long priority) {
		super();
		this.object = object;
		this.priority = priority;
	}

	/**
	 * Returns the title.
	 * 
	 * @return the title
	 */
	public abstract String getTitle();

	/**
	 * Returns the description.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * Returns the priority for ordering purposes. This priority does only
	 * matter within one search engine.
	 * 
	 * @return the priority for ordering purposes
	 */
	public long getPriority() {
		return this.priority;
	}

	/**
	 * Returns the search result object.
	 * 
	 * @return the search result object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 * <p>
	 * 
	 * @param o
	 *            the Object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * 
	 * @throws ClassCastException
	 *             if the specified object's type prevents it from being
	 *             compared to this Object.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(SearchResult<T> o) {
		final int priorityComp = -Long.valueOf(getPriority()).compareTo(
				o.getPriority());
		if (0 != priorityComp)
			return priorityComp;
		if (null == getTitle())
			return 1;
		if (null == o.getTitle())
			return -1;
		final int titleComp = getTitle().compareTo(o.getTitle());
		if (0 != titleComp)
			return titleComp;
		if (null == getDescription())
			return 1;
		if (null == o.getDescription())
			return -1;
		final int descComp = getDescription().compareTo(o.getDescription());
		if (0 != descComp)
			return descComp;
		if (null == getObject())
			return 1;
		if (null == o.getObject())
			return -1;
		if (getObject().getClass().equals(o.getObject().getClass())
				&& getObject() instanceof Comparable) {
			final int compareObj = ((Comparable<T>) getObject()).compareTo(o
					.getObject());
			if (0 != compareObj)
				return compareObj;
		}
		return 0;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((getTitle() == null) ? 0 : getTitle().hashCode());
		result = PRIME
				* result
				+ ((getDescription() == null) ? 0 : getDescription().hashCode());
		result = PRIME * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SearchResult other = (SearchResult) obj;
		final String title = getTitle();
		final String oTitle = other.getTitle();
		if (title == null) {
			if (oTitle != null)
				return false;
		} else if (!title.equals(oTitle))
			return false;
		final String desc = getDescription();
		final String oDesc = other.getDescription();
		if (desc == null) {
			if (oDesc != null)
				return false;
		} else if (!desc.equals(oDesc))
			return false;
		if (getObject() == null) {
			if (other.getObject() != null)
				return false;
		} else if (!getObject().equals(other.getObject()))
			return false;
		return true;
	}

}