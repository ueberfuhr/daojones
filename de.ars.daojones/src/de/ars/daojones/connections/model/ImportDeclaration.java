package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.*;

/**
 * Default implementation for {@link IImportDeclaration};
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportDeclaration extends AbstractConnectionConfigurationElement implements IImportDeclaration {

	private static final long serialVersionUID = -4805726749095368321L;
	
	@XmlAttribute(name=IMPORT_FILE, required=true)
	private String file;

	/**
	 * Creates a new instance.
	 */
	public ImportDeclaration() {
		super();
	}
	
	/**
	 * Sets the file of the import.
	 * @param file the file
	 * @see de.ars.daojones.connections.model.IImportDeclaration#getFile()
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @see de.ars.daojones.connections.model.IImportDeclaration#getFile()
	 */
	// TODO Java6-Migration
//	@Override
	public String getFile() {
		return file;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
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
		ImportDeclaration other = (ImportDeclaration) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

}
