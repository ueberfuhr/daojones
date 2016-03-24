package de.ars.daojones.runtime.spi.beans.fields;

import java.util.List;

import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;

/**
 * A wrapper for another field context. You can create a subclass of this to
 * change the behaviour of an original field context. It is recommended to use
 * this class instead of implementing the interface to be more stable against
 * additional methods in further versions.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 */
public class FieldContextWrapper<T> implements FieldContext<T> {

  private final FieldContext<T> delegate;

  /**
   * Conbstructor.
   * 
   * @param delegate
   *          the delegate
   */
  public FieldContextWrapper( final FieldContext<T> delegate ) {
    super();
    this.delegate = delegate;
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public Class<? extends T> getType() {
    return delegate.getType();
  }

  @Override
  public UpdatePolicy getUpdatePolicy() {
    return delegate.getUpdatePolicy();
  }

  @Override
  public List<Property> getMetadata() {
    return delegate.getMetadata();
  }

}
