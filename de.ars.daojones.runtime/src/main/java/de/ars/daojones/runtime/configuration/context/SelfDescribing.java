package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;

/**
 * An interface that can be implemented by the bean itself. If so, the bean
 * model is analyzed using the bean itself. Otherwise, the bean model is
 * resolved from the bean model configuration.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface SelfDescribing {

  /**
   * Finds a field mapped element. If there are multiple elements (one for
   * reading and one for writing), the first occurence is returned.
   * 
   * @param id
   *          the id of the field
   * @return the field mapped element or <tt>null</tt>
   */
  DatabaseFieldMappedElement findFieldModel( String id );

  /**
   * Finds a field mapped element.
   * 
   * @param id
   *          the id of the field
   * @param write
   *          a flag to find readable (<tt>false</tt>) or writable (
   *          <tt>true</tt>) fields
   * @return the field mapped element or <tt>null</tt>
   */
  DatabaseFieldMappedElement findFieldModel( final String id, final boolean write );

}
