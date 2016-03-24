package de.ars.eclipse.ui.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.ars.daojones.eclipse.ui.Activator;

/**
 * A utility class providing methods for opening the message dialog.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class MessageDialogUtil {

	/**
	 * Opens the message dialog to show an error. The parent shell is the shell
	 * of the currently active window. The message shown in the dialog is the
	 * whole stacktrace of the given exception.
	 * 
	 * @param title
	 *            the title of the message box
	 * @param t
	 *            the exception
	 */
	public static void openError(String title, Throwable t) {
		openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell(), title, t);

	}

	/**
	 * Opens the message dialog to show an error. The message shown in the
	 * dialog is the whole stacktrace of the given exception.
	 * 
	 * @param shell
	 *            the parent shell
	 * @param title
	 *            the title of the message box
	 * @param t
	 *            the exception
	 */
	public static void openError(Shell shell, String title, Throwable t) {
		final StringWriter out = new StringWriter();
		t.printStackTrace(new PrintWriter(out));
		ErrorDialog.openError(shell, title, null, new Status(IStatus.ERROR,
				Activator.PLUGIN_ID, title, t));
	}

}
