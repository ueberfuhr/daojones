package de.ars.daojones.internal.integration.eclipse.viewers.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * An {@link ITableLabelProvider} that shows the common label in the first
 * column of the tree and extends it by supporting column values.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public abstract class ExtendedTreeLabelProvider implements ITableLabelProvider, ITableFontProvider {

  private final ILabelProvider provider;

  /**
   * Creates an instance.
   * 
   * @param provider
   *          the {@link ILabelProvider} that requests for the first column are
   *          delegated to.
   */
  public ExtendedTreeLabelProvider( final ILabelProvider provider ) {
    super();
    this.provider = provider;
  }

  @Override
  public Font getFont( final Object element, final int columnIndex ) {
    return null;
  }

  @Override
  public final Image getColumnImage( final Object element, final int columnIndex ) {
    final Image result = getImage( element, columnIndex );
    return null != result ? result : this.provider.getImage( element );
  }

  /**
   * Returns the text assigned to the element or <tt>null</tt>, if no text is
   * assigned.
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

  @Override
  public String getColumnText( final Object element, final int columnIndex ) {
    final String result = getText( element, columnIndex );
    return null != result ? result : this.provider.getText( element );
  }

  @Override
  public void addListener( final ILabelProviderListener listener ) {
    this.provider.addListener( listener );
  }

  @Override
  public void dispose() {
    this.provider.dispose();
  }

  @Override
  public boolean isLabelProperty( final Object element, final String property ) {
    return this.provider.isLabelProperty( element, property );
  }

  @Override
  public void removeListener( final ILabelProviderListener listener ) {
    this.provider.removeListener( listener );
  }

}
