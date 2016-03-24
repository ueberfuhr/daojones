package de.ars.daojones.integration.eclipse.viewers.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * An {@link ITableLabelProvider} that shows the common label in the first
 * column of the tree and extends it by supporting column values.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.1.0
 */
public abstract class ExtendedTreeLabelProvider implements ITableLabelProvider {

  private final ILabelProvider provider;

  /**
   * Creates an instance.
   * 
   * @param provider
   *          the {@link ILabelProvider} that requests for the first column are
   *          delegated to.
   */
  public ExtendedTreeLabelProvider( ILabelProvider provider ) {
    super();
    this.provider = provider;
  }

  /**
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
   *      int)
   */
  public final Image getColumnImage( Object element, int columnIndex ) {
    final Image result = getImage( element, columnIndex );
    return null != result ? result : provider.getImage( element );
  }

  /**
   * Returns the text assigned to the element or null, if no text is assigned.
   * 
   * @param element
   *          the element
   * @param columnIndex
   *          the column index that is greater than zero
   * @return the text
   */
  protected abstract String getText( Object element, int columnIndex );

  /**
   * Returns the text assigned to the element or null, if no text is assigned.
   * 
   * @param element
   *          the element
   * @param columnIndex
   *          the column index that is greater than zero
   * @return the text
   */
  protected abstract Image getImage( Object element, int columnIndex );

  /**
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
   *      int)
   */
  public String getColumnText( Object element, int columnIndex ) {
    final String result = getText( element, columnIndex );
    return null != result ? result : provider.getText( element );
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void addListener( ILabelProviderListener listener ) {
    provider.addListener( listener );
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
   */
  public void dispose() {
    provider.dispose();
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
   *      java.lang.String)
   */
  public boolean isLabelProperty( Object element, String property ) {
    return provider.isLabelProperty( element, property );
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void removeListener( ILabelProviderListener listener ) {
    provider.removeListener( listener );
  }

}
