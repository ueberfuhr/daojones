package de.ars.daojones.runtime.spi.beans.fields;

import java.util.List;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.beans.Property;

/**
 * The context of a single field access.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the field type
 */
public interface FieldContext<T> {
  /**
   * Returns the field name.
   * 
   * @return the field name
   */
  String getName();

  /**
   * Returns the type.
   * 
   * @return the type
   */
  Class<? extends T> getType();

  /**
   * Returns the update policy.
   * 
   * @return the update policy
   */
  UpdatePolicy getUpdatePolicy();

  /**
   * Returns the meta data.
   * 
   * @return the meta data
   */
  List<Property> getMetadata();
}