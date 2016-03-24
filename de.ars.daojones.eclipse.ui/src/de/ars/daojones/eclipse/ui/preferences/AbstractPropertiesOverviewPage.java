package de.ars.daojones.eclipse.ui.preferences;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * An overivew page that lists the subpages.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class AbstractPropertiesOverviewPage extends PropertyPage {
	
	private final String id;

	/**
	 * Creates a new instance.
	 * @param id the id
	 */
	public AbstractPropertiesOverviewPage(String id) {
		super();
		this.id = id;
	}
	
	/**
	 * Returns the id.
	 * @return the id
	 */
	protected String getId() {
		return this.id;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FormLayout());
		int index = 0;
		for(Node node : getPreferenceNodes()) {
			final Control link = createLink(container, node);
			final FormData contentTypeAreaData = new FormData();
			contentTypeAreaData.left = new FormAttachment(0,0);
			contentTypeAreaData.top = new FormAttachment(0,10+index*20);
			link.setLayoutData(contentTypeAreaData);
			index++;
		}
		return container;
	}
	/**
	 * Creates a link control to open the page.
	 * @param parent the parent
	 * @param node the node
	 * @return the link control
	 */
	protected abstract Control createLink(Composite parent, Node node);
	
	/**
	 * Returns a list containing all child nodes.
	 * @return the child nodes
	 */
	protected abstract List<Node> getPreferenceNodes();
	
}
