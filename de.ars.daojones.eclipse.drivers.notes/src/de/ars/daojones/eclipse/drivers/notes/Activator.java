package de.ars.daojones.eclipse.drivers.notes;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import de.ars.daojones.drivers.notes.ThreadManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	/**
	 *  The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.ars.daojones.eclipse.drivers.notes";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		ThreadManager.getInstance().termThread();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * Logs a message.
	 * @param severity the severity (one of {@link IStatus#ERROR}, {@link IStatus#WARNING}, {@link IStatus#INFO})
	 * @param message the message
	 */
	public static void log(int severity, String message) {
		log(severity, message, null);
	}
	/**
	 * Logs a message and an exception stack trace.
	 * @param severity the severity (one of {@link IStatus#ERROR}, {@link IStatus#WARNING}, {@link IStatus#INFO})
	 * @param message the message
	 * @param t the exception containing the stack trace
	 */
	public static void log(int severity, String message, Throwable t) {
		if(null != Activator.getDefault()) {
			Activator.getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, t));
		}
	}

}
