package de.ars.daojones.eclipse.jdt.ui.console;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.MessageConsole;

import com.swtdesigner.ResourceManager;

import de.ars.daojones.eclipse.jdt.beans.BeanBuilder;

/**
 * An {@link IConsoleFactory} creating a console for the Bean Builder.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class BeanBuilderConsoleFactory implements IConsoleFactory {

    private static final String NAME = "DaoJones Bean Builder";
    private static final ImageDescriptor IMAGE = ResourceManager.getPluginImageDescriptor(
            Platform.getBundle("de.ars.daojones.eclipse.ui").getBundleContext(), 
            "icons/chart_line.png"
    );

	private static final IConsoleListener CONSOLELISTENER = new IConsoleListener() {
		/**
		 * @see IConsoleListener#consolesAdded(IConsole[])
		 */
		@Override
		public void consolesAdded(IConsole[] consoles) {
			for(final IConsole console : consoles) {
				if(console == BeanBuilderConsoleFactory.console) {
					BeanBuilder.logger.addHandler(consoleHandler);
				}
			}
		}
		/**
		 * @see IConsoleListener#consolesRemoved(IConsole[])
		 */
		@Override
		public void consolesRemoved(IConsole[] consoles) {
			for(final IConsole console : consoles) {
                if(console == BeanBuilderConsoleFactory.console) {
                    BeanBuilder.logger.removeHandler(consoleHandler);
                }
			}
		}
	};
	
	private static final MessageConsole console;
	private static final ConsoleHandler consoleHandler;
	
	static {
	    console = new MessageConsole(NAME, IMAGE);
	    consoleHandler = new ConsoleHandler(console);
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(CONSOLELISTENER);
	}
	
	/**
	 * Creates an instance.
	 */
	public BeanBuilderConsoleFactory() {
		super();
	}
	
	/**
	 * @see IConsoleFactory#openConsole()
	 */
	@Override
	public void openConsole() {
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}
	
}
