package de.ars.daojones.eclipse.jdt.internal.libraries;

import static de.ars.daojones.eclipse.jdt.Artifacts.getDaoJonesLibrary;
import static de.ars.daojones.eclipse.jdt.LoggerConstants.ERROR;
import static de.ars.daojones.eclipse.jdt.LoggerConstants.getLogger;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

/**
 * The DaoJones runtime library.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class RuntimeLibrary implements IClasspathContainer {

	/**
	 * The name of the library.
	 */
	public static final String NAME = "DaoJones Runtime Library";
	/**
	 * The id of the library that is used in the path.
	 */
	public static final String ID = "RUNTIME";
	/**
	 * The path of the library.
	 */
	public static final IPath PATH = new Path(ClasspathContainers.LIBRARY_CONTAINER_ID).append(ID);

	/**
	 * Returns the DaoJones runtime library as a classpath entry.
	 * @return the DaoJones runtime library as a classpath entry
	 */
	protected IClasspathEntry getDaoJonesAPIEntry() {
//		boolean isTest = false;
		try {
			final File file = getDaoJonesLibrary();
			final boolean isTest = null != file && file.isDirectory();
			final IPath basePath = new Path(file.getAbsolutePath());
			return JavaCore.newLibraryEntry(isTest ? basePath.append("bin") : basePath, isTest ? basePath.append("src") : Path.EMPTY, null);
		} catch (IOException e) {
			getLogger().log(ERROR, "Error during finding DaoJones library!", e);
		}
//		return JavaCore.newVariableEntry(new Path(VARIABLE_RUNTIME + (isTest ? "/bin" : "")), null, null);
		return null;
	}
	
	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		return new IClasspathEntry[] {
			getDaoJonesAPIEntry()
			// TODO Add Drivers to library
		};
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	@Override
	public String getDescription() {
		return NAME;//"Contains the runtime classes for DaoJones applications.";
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
	 */
	@Override
	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	/**
	 * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
	 */
	@Override
	public IPath getPath() {
		return PATH;
	}

}
