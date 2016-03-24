package de.ars.daojones.eclipse.jdt.internal.libraries;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.ars.daojones.eclipse.jdt.Artifacts;

import static de.ars.daojones.eclipse.jdt.LoggerConstants.*;

/**
 * Initializes the DaoJones ClassPath Variables to SDK and RT.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ClasspathVariableInitializer extends org.eclipse.jdt.core.ClasspathVariableInitializer {

	private void initialize(String variable, IPath value) throws JavaModelException {
		JavaCore.setClasspathVariable(variable, value, null);
		getLogger().log(DEBUG, "Initialized classpath variable \"" + variable + "\" to \"" + value + "\".");
	}
	
	/**
	 * @see org.eclipse.jdt.core.ClasspathVariableInitializer#initialize(java.lang.String)
	 */
	@Override
	public void initialize(String variable) {
		try {
			if(Artifacts.VARIABLE_RUNTIME.equals(variable)) {
				initialize(variable, new Path(Artifacts.getDaoJonesLibrary().getCanonicalPath()));
//			} else if(Artifacts.VARIABLE_SDK.equals(variable)) {
//				initialize(variable, new Path(Artifacts.getDaoJonesSDKLibrary().getCanonicalPath()));
			}
		} catch (JavaModelException e) {
			getLogger().log(ERROR, "Unable to initialize classpath variable " + variable + "!", e);
		} catch (IOException e) {
			getLogger().log(ERROR, "Unable to initialize classpath variable " + variable + "!", e);
		}
	}

}
