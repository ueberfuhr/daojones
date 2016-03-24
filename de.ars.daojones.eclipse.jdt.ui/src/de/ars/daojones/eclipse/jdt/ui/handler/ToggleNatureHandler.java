package de.ars.daojones.eclipse.jdt.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.ars.daojones.eclipse.jdt.ProjectNature;
import de.ars.daojones.eclipse.jdt.ui.Activator;
import de.ars.equinox.utilities.ui.HandlerUtil;

/**
 * A handler handling changes to the DaoJones JDT Nature on a Java Project.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ToggleNatureHandler extends AbstractHandler {
	
	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	// TODO Java6-Migration
	// @Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		for(final IProject project : HandlerUtil.getSelectedObjects(event, IProject.class)) {
			final Job job = new Job("Change project natures") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						ProjectNature.toggleNature(project);
						return Status.OK_STATUS;
					} catch(Throwable t) {
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, t.getMessage(), t);
					}
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
		return null;
	}

}
