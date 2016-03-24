package de.ars.daojones.integration.eclipse;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

/**
 * An editor for the database path. This interface must be implemented by a
 * driver UI to provide a driver-specific editor for the database path.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public interface DatabasePathEditor {

  /**
   * Creates the controls of this editor.
   * 
   * @param parent
   *          the parent composite
   */
  public void createControls( Composite parent );

  /**
   * Observes the database path.
   * 
   * @param path
   *          the database path
   * @param ctx
   *          the binding context
   */
  public void setModel( IObservableValue path, DataBindingContext ctx );

  /**
   * Sets the focus to the first control.
   */
  public void setFocus();

  /**
   * Sets the flag to enable or disable editing the database path.
   * 
   * @param editable
   *          the flag to enable or disable editing the database path
   */
  public void setEditable( boolean editable );

}
