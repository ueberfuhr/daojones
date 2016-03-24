package de.ars.daojones.eclipse.jdt.ui;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.ars.daojones.eclipse.jdt.beans.BeanBuilder;
import de.ars.daojones.eclipse.jdt.ui.console.BeanBuilderConsoleFactory;
import de.ars.daojones.eclipse.jdt.ui.preferences.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/**
	 *  The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.ars.daojones.eclipse.jdt.ui";

	// The shared instance
	private static Activator plugin;
	
	private static final Handler CONSOLE_HANDLER = new Handler() {
		private BeanBuilderConsoleFactory factory;
		@Override
		public void close() throws SecurityException {}
		@Override
		public void flush() {}
		@Override
		public void publish(LogRecord record) {
			if(Activator.getDefault().getPreferenceStore().getBoolean(Constants.PREF_WORKBENCH_CONSOLE_AUTOACTIVATE.getLocalName())) {
				if(null == factory) factory = new BeanBuilderConsoleFactory();
				factory.openConsole();
			}
		}
	};
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		BeanBuilder.logger.addHandler(CONSOLE_HANDLER);
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		BeanBuilder.logger.removeHandler(CONSOLE_HANDLER);
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
		Activator.getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, t));
	}	

}
