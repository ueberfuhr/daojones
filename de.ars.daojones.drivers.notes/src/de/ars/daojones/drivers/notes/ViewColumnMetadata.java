package de.ars.daojones.drivers.notes;

import java.io.Serializable;

import lotus.domino.NotesException;
import lotus.domino.ViewColumn;

/**
 * A transfer object providing information about a column in a view.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ViewColumnMetadata implements Serializable {

	private static final long serialVersionUID = 4230458536935890966L;

	private final String title;
	private final String formula;
	private final boolean isFormula;
	
	public ViewColumnMetadata(ViewColumn column) throws NotesException {
		this(column.getTitle(), column.isFormula() ? column.getFormula() : column.getItemName(), column.isFormula());
	}
	public ViewColumnMetadata(String title, String formula, boolean isFormula) {
		super();
		this.title = title;
		this.formula = formula;
		this.isFormula = isFormula;
	}

	protected String getTitle() {
		return title;
	}

	protected String getFormula() {
		return formula;
	}

	protected boolean isFormula() {
		return isFormula;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewColumnMetadata other = (ViewColumnMetadata) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
}
