package de.ars.daojones.drivers.notes;

import de.ars.daojones.runtime.Identificator;

/**
 * The Notes driver implementation for an {@link Identificator} identifying a
 * ViewEntry within a view.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NotesViewEntryIdentificator extends NotesDocumentIdentificator {

	private static final long serialVersionUID = -6519037869734473895L;
	private final String viewName;

	/**
	 * Creates an identificator.
	 * 
	 * @param viewName
	 *            the name of the view
	 * @param unid
	 *            the UniversalID of the document
	 */
	public NotesViewEntryIdentificator(String viewName, String unid) {
		super(unid);
		this.viewName = viewName;
	}

	String getViewName() {
		return this.viewName;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new NotesViewEntryIdentificator(getViewName(), getUnid());
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getUnid() + "@" + getViewName();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUnid() == null) ? 0 : getUnid().hashCode());
		result = prime * result
				+ ((viewName == null) ? 0 : viewName.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotesViewEntryIdentificator other = (NotesViewEntryIdentificator) obj;
		if (getUnid() == null) {
			if (other.getUnid() != null)
				return false;
		} else if (!getUnid().equals(other.getUnid()))
			return false;
		if (viewName == null) {
			if (other.viewName != null)
				return false;
		} else if (!viewName.equals(other.viewName))
			return false;
		return true;
	}

}
