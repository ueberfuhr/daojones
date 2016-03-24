package de.ars.daojones.runtime.spi.beans.fields;

import java.util.List;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;

/**
 * A default implementation of field context.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the field type
 */
public class FieldContextImpl<T> implements FieldContext<T> {

  private final String name;
  private final Class<? extends T> type;
  private final UpdatePolicy updatePolicy;
  private final List<Property> metadata;

  /**
   * Constructor.
   * 
   * @param name
   *          the field name
   * @param type
   *          the field type
   * @param updatePolicy
   *          the update policy
   * @param metadata
   *          the field meta data
   */
  public FieldContextImpl( final String name, final Class<? extends T> type, final UpdatePolicy updatePolicy,
          final List<Property> metadata ) {
    super();
    this.name = name;
    this.type = type;
    this.updatePolicy = updatePolicy;
    this.metadata = metadata;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Class<? extends T> getType() {
    return type;
  }

  @Override
  public List<Property> getMetadata() {
    return metadata;
  }

  public static <T> FieldContext<T> from( final DatabaseFieldMapping model, final Class<? extends T> type ) {
    return new FieldContextImpl<T>( model.getName(), type, model.getUpdatePolicy(), model.getMetadata() );
  }

  @Override
  public UpdatePolicy getUpdatePolicy() {
    return updatePolicy;
  }

}
