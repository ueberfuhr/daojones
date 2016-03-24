package de.ars.daojones.eclipse.jdt.beans;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.ars.daojones.eclipse.jdt.Activator;

import static de.ars.daojones.eclipse.jdt.LoggerConstants.*;
/**
 * A singleton reading all extensions of problem detectors
 * and initializes them.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ProblemDetectors {

	private static final String EXTENSION_POINT = Activator.PLUGIN_ID + ".beansproblemdetectors";
	private static ProblemDetectors theInstance = null;
	private Collection<ProblemDetector> DETECTORS = null;
	
	private ProblemDetectors() {
		super();
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance
	 */
	public synchronized static ProblemDetectors getInstance() {
		if(null == theInstance) theInstance = new ProblemDetectors();
		return theInstance;
	}
	
	private synchronized Collection<ProblemDetector> getDetectors() {
		if(null == DETECTORS) {
			DETECTORS = new HashSet<ProblemDetector>();
			final IExtensionRegistry registry = Platform.getExtensionRegistry();
			// read elements
			final IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
			for(IConfigurationElement el : elements) {
				try {
					DETECTORS.add((ProblemDetector)el.createExecutableExtension("class"));
				} catch (CoreException e) {
					getLogger().log(ERROR, "Unable to create problem detector instance of type " + el.getAttribute("class") + "!", e);
				}
			}
		}
		return DETECTORS;
	}
	
	/**
	 * Visits all registered problem detectors.
	 * @param env the {@link ProblemDetectorEnvironment}
	 * @param unit the unit to visit
	 */
	public void accept(ProblemDetectorEnvironment env, CompilationUnit unit) {
		for(ProblemDetector detector : getDetectors()) {
			synchronized(detector) {
				detector.setEnvironment(env);
				unit.accept(detector);
			}
		}
	}
	
}
