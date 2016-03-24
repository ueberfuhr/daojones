package de.ars.daojones.eclipse.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Creates, adds and disposes actions for the menus and action bars of each
 * workbench window.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	/**
	 * Creates an instance.
	 * 
	 * @param configurer
	 *            the {@link IActionBarConfigurer}
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	protected void makeActions(IWorkbenchWindow window) {
	}

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
	}

}
