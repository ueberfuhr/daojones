package de.ars.eclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * A utility class providing helper methods concerning building UI forms.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class FormsUtil {

	/**
	 * Returns the layout data for a simple label.
	 * 
	 * @param parent
	 *            the parent {@link Composite}
	 * @return the layout data
	 */
	public static Object createLabelData(Composite parent) {
		if (parent.getLayout() instanceof GridLayout) {
			final GridData result = new GridData(
					GridData.VERTICAL_ALIGN_BEGINNING
							| GridData.FILL_HORIZONTAL);
			result.grabExcessHorizontalSpace = false;
			return result;
		} else if (parent.getLayout() instanceof RowLayout) {
			final RowData result = new RowData();
			return result;
		} else
			return null;
	}

	/**
	 * Returns the layout data for a text field.
	 * 
	 * @param messageIndent
	 *            a flag indicating whether to define an area for a message icon
	 *            or not
	 * @return the layout data for a text field
	 */
	public static GridData createSingleLineTextData(boolean messageIndent) {
		final GridData result = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		if (messageIndent)
			result.horizontalIndent = 5;
		return result;
	}

	/**
	 * Returns the layout data for a {@link Section}.
	 * 
	 * @return the layout data for a {@link Section}
	 */
	public static GridData createSectionData() {
		return new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
	}

	/**
	 * Returns the layout data for a text area.
	 * 
	 * @param messageIndent
	 *            a flag indicating whether to define an area for a message icon
	 *            or not
	 * @param height
	 *            the height of the text area
	 * @return the layout data for a text area
	 */
	public static GridData createMultipleLineTextData(boolean messageIndent,
			int height) {
		final GridData result = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_VERTICAL);
		if (messageIndent)
			result.horizontalIndent = 5;
		result.heightHint = height;
		return result;
	}

	/**
	 * A {@link FocusListener} that can be added to a text field. This
	 * {@link FocusListener} selects the whole text if the text field gets the
	 * focus.
	 */
	public static final FocusListener SELECT_ALL_ON_FOCUS = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			if (null != e.getSource() && e.getSource() instanceof Text) {
				((Text) e.getSource()).selectAll();
			}
		}
	};

	/**
	 * Creates a {@link Label}.
	 * 
	 * @param toolkit
	 *            the {@link FormToolkit}
	 * @param parent
	 *            the parent {@link Composite}
	 * @param title
	 *            the text
	 * @param tooltip
	 *            the tooltip
	 * @param markAsMandatory
	 *            if true, a '*' is added to the label to indicate that the
	 *            label is related to a mandatory input field
	 * @return the {@link Label}
	 */
	public static Label createLabel(FormToolkit toolkit, Composite parent,
			String title, String tooltip, boolean markAsMandatory) {
		final Label result = toolkit.createLabel(parent, title
				+ (markAsMandatory ? "*" : ""), SWT.BALLOON);
		result.setLayoutData(createLabelData(parent));
		if (null != tooltip)
			result.setToolTipText(tooltip);
		// result.setForeground(ColorDescriptor.createFrom(new
		// RGB(0,0,255)).createColor(Display.getCurrent()));
		return result;
	}

	/**
	 * Sets the visible state of a couple of controls that are positioned on one
	 * {@link Composite}.
	 * 
	 * @param visible
	 *            the visible state to set
	 * @param parent
	 *            the parent {@link Composite}
	 * @param controls
	 *            the controls
	 */
	public static void setVisible(boolean visible, Composite parent,
			Control... controls) {
		for (Control c : controls) {
			final Object data = c.getLayoutData();
			if (data instanceof GridData) {
				((GridData) data).exclude = !visible;
			}
			c.setVisible(visible);
		}
		parent.layout(controls);
	}

}
