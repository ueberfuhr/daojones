package de.ars.daojones.drivers.notes;

import de.ars.daojones.runtime.Identificator;

/**
 * The Notes driver implementation for an {@link Identificator} identifying a
 * single document in the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NotesDocumentIdentificator implements Identificator {

	private static final long serialVersionUID = 109176844460471430L;
	private final String unid;

	/**
	 * Creates an identificator.
	 * 
	 * @param unid
	 *            the UniversalID of the document
	 */
	public NotesDocumentIdentificator(String unid) {
		super();
		this.unid = unid;
	}

	String getUnid() {
		return unid;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new NotesDocumentIdentificator(getUnid());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((unid == null) ? 0 : unid.hashCode());
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
		final NotesDocumentIdentificator other = (NotesDocumentIdentificator) obj;
		if (unid == null) {
			if (other.unid != null)
				return false;
		} else if (!unid.equals(other.unid))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getUnid();
	}

}
