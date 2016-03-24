package de.ars.daojones.eclipse.jdt.ui.wizards;

import static de.ars.daojones.eclipse.jdt.ui.LoggerConstants.ERROR;
import static de.ars.daojones.eclipse.jdt.ui.LoggerConstants.getLogger;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataObjectContainer;

/**
 * A wizard creating a new bean.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @deprecated This wizard is not necessary anymore. Just implement the {@link Dao} interface.
 */
 @Deprecated
public class NewBeanWizard extends Wizard implements INewWizard {

	private NewClassWizardPage fPage;
	private IStructuredSelection fSelection;

/* (non-Javadoc)
 * @see org.eclipse.jface.wizard.Wizard#performFinish()
 */
//	/**
//	 * Returns the scheduling rule for creating the element.
//	 * @return returns the scheduling rule
//	 */
//	protected ISchedulingRule getSchedulingRule() {
//		return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
//	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IWorkspaceRunnable op= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
				try {
					finishPage(monitor);
				} catch (InterruptedException e) {
					throw new OperationCanceledException(e.getMessage());
				}
			}
		};
		try {
//			ISchedulingRule rule= null;
//			Job job= Job.getJobManager().currentJob();
//			if (job != null)
//				rule= job.getRule();
//			IRunnableWithProgress runnable= null;
//			if (rule != null)
//				runnable= new WorkbenchRunnableAdapter(op, rule, true);
//			else
//				runnable= new WorkbenchRunnableAdapter(op, getSchedulingRule());
			getContainer().run(false, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						op.run(monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
				
			});
		} catch (InvocationTargetException e) {
			getLogger().log(ERROR, "Unable to open file!", e);
			return false;
		} catch  (InterruptedException e) {
			getLogger().log(ERROR, "Unable to open file!", e);
			return false;
		}
		IResource resource= fPage.getModifiedResource();
		if (resource != null) {
			openResource((IFile) resource);
			return true;
		}	
		return false;
	}
	
	/**
	 * Opens a resource in the assigned editor.
	 * @param resource the file resource that should be opened.
	 */
	protected void openResource(final IFile resource) {
		final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			final Display display= getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							IDE.openEditor(activePage, resource, true);
						} catch (PartInitException e) {
							getLogger().log(ERROR, "Unable to open file!", e);
						}
					}
				});
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		fSelection= currentSelection;
	}
	
	/**
	 * Returns the selection.
	 * @return the selection
	 */
	public IStructuredSelection getSelection() {
		return fSelection;
	}

	/**
	 * Finishes the page. This is called before closing the dialog.
	 * @param monitor a monitor
	 * @throws InterruptedException
	 * @throws CoreException
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage= new NewClassWizardPage();
			fPage.init(getSelection());
			fPage.addSuperInterface(Dao.class.getName());
			fPage.setSuperClass(DataObjectContainer.class.getName(), true);
			fPage.setMethodStubSelection(false, false, false, true);
			fPage.setModifiers(Flags.AccAbstract | Flags.AccPublic, false);
		}
		addPage(fPage);
	}


}
