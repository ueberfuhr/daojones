package de.ars.daojones.eclipse.ui;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * An enumeration containing some icons and images that this plug-in is
 * providing.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum Images {

	/**
	 * The icon for DaoJones.
	 */
	DAOJONES("icons/chart_line.png"),
	/**
	 * The icon for DaoJones used for the project nature.
	 */
	PROJECT_DECORATOR("icons/chart_line.png"),
	/**
	 * The icon for a connection.
	 */
	CONNECTION("icons/connect.png"),
	/**
	 * The icon for a connection with a 'new' decorator.
	 */
	CONNECTION_NEW("icons/connect_new.png"),
	/**
	 * The icon for an import declaration that is part of the connection
	 * configuration.
	 */
	CONNECTION_IMPORT("icons/link.png"),
	/**
	 * The icon for an import declaration that is part of the connection
	 * configuration with a 'new' decorator.
	 */
	CONNECTION_IMPORT_NEW("icons/link_new.png"),
	/**
	 * The icon for a credential that is part of the connection configuration.
	 */
	CREDENTIAL("icons/user.png"),
	/**
	 * The icon for a credential that is part of the connection configuration
	 * with a 'new' decorator.
	 */
	CREDENTIAL_NEW("icons/user_new.png"),
	/**
	 * An empty icon for a missing image.
	 */
	MISSING_IMAGE(null);

	private final String filename;

	private Images(String filename) {
		this.filename = filename;
	}

	private String getFilename() {
		return filename;
	}

	/**
	 * Returns the {@link Image}.
	 * 
	 * @return the {@link Image}
	 */
	public Image getImage() {
		final org.eclipse.jface.resource.ImageRegistry reg = Activator
				.getDefault().getImageRegistry();
		Image result = reg.get(name());
		if (null == result) {
			reg.put(name(), result = (null != getFilename() ? ImageDescriptor
					.createFromURL(FileLocator.find(Activator.getDefault()
							.getBundle(), new Path(getFilename()), null))
					: ImageDescriptor.getMissingImageDescriptor())
					.createImage());
		}
		return result;
	}

}
