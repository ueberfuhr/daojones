package de.ars.daojones.eclipse.ui.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * A preferences page for general DaoJones settings.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ProjectPropertiesOverviewPage extends AbstractPropertiesOverviewPage {
	
	private static final String ID = "de.ars.daojones.eclipse.ui.project.properties";

	/**
	 * Creates an instance.
	 */
	public ProjectPropertiesOverviewPage() {
		super(ID);
	}
	/**
	 * @see AbstractPropertiesOverviewPage#getPreferenceNodes()
	 */
	@Override
	protected List<Node> getPreferenceNodes() {
		final List<Node> result = new LinkedList<Node>();
		for(final IPreferenceNode node : PreferencesUtil.propertiesContributorsFor(this.getElement())) {
			if(ID.equals(node.getId())) {
				for(final IPreferenceNode child : node.getSubNodes()) {
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
				break;
			}
		}
		return result;
	}
	
	/**
	 * @see AbstractPropertiesOverviewPage#createLink(Composite, Node)
	 */
	@Override
	protected Control createLink(Composite parent, final Node node) {
		final Link link = new Link(parent, SWT.NONE);
		link.setText("See <a>'" + node.getTitle() + "'</a>");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((IWorkbenchPreferenceContainer)getContainer()).openPage(node.getId(), ProjectPropertiesOverviewPage.this.getElement());
			}
		});
		return link;
	}

}
