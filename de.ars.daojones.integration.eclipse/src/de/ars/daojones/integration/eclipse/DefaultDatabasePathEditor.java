package de.ars.daojones.integration.eclipse;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Default implementation of {@link DatabasePathEditor} providing a single text
 * field.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public class DefaultDatabasePathEditor implements DatabasePathEditor {

  private Text text;

  @Override
  public void createControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    this.text = new Text( parent, SWT.SINGLE | SWT.BORDER );
    this.text.setLayoutData( GridDataFactory.fillDefaults().grab( true, false ).create() );
  }

  @Override
  public void setEditable( final boolean editable ) {
    this.text.setEditable( false );
    this.text.setEnabled( false );
  }

  /**
   * Returns the text field.
   * 
   * @return the text field.
   */
  protected Text getText() {
    return this.text;
  }

  @Override
  public void setFocus() {
    getText().setFocus();
  }

  @Override
  public void setModel( final IObservableValue path, final DataBindingContext ctx ) {
    final IObservableValue value = SWTObservables.observeText( getText(), SWT.Modify );
    ctx.bindValue( value, path, null, null );
  }

}
