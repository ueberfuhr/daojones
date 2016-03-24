/**
 * 
 */
package de.ars.daojones.eclipse.ui.help.commands.cs;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.ars.daojones.eclipse.ui.help.Activator;

/**
 * This command runs a command.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class RunShellCommand extends AbstractHandler implements IHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String command = event.getParameter("command");
		Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Executing batch command \'" + command + "\'."));
		try {
			return Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			throw new ExecutionException("Unable to run command!", e);
		}
	}

}
