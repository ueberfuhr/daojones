package de.ars.daojones.eclipse.ui.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * A preferences page for general DaoJones settings.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class WorkbenchPreferencePage extends AbstractPropertiesOverviewPage implements IWorkbenchPreferencePage {
	
	private static final String ID = "de.ars.daojones.eclipse.ui.preferences";

	/**
	 * Creates an instance.
	 */
	public WorkbenchPreferencePage() {
		super(ID);
	}
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	// TODO Java6-Migration
	// @Override
	public void init(IWorkbench workbench) {}
	/**
	 * @see AbstractPropertiesOverviewPage#getPreferenceNodes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<Node> getPreferenceNodes() {
		final List<Node> result = new LinkedList<Node>();
		for(IPreferenceNode node : (List<IPreferenceNode>)PlatformUI.getWorkbench().getPreferenceManager().getElements(PreferenceManager.PRE_ORDER)) {
			if (node.getId().equals(getId())) {
				final IPreferenceNode[] childNodes = node.getSubNodes();
				for(final IPreferenceNode child : childNodes) {
					result.add(new Node() {
						// TODO Java6-Migration
						// @Override
						public String getId() {
							return child.getId();
						}
						// TODO Java6-Migration
						// @Override
						public String getTitle() {
							return child.getLabelText();
						}
					});
				}
			}
		};
		return result;
	}
	/**
	 * @see AbstractPropertiesOverviewPage#createLink(Composite, Node)
	 */
	@Override
	protected Control createLink(Composite parent, Node node) {
		final PreferenceLinkArea link = new PreferenceLinkArea(
			parent,
			SWT.NONE,
			node.getId(), 
			"See <a>''{0}''</a>",
			(IWorkbenchPreferenceContainer) getContainer(), 
			null
		);
		return link.getControl();
	}

}
