package de.ars.daojones.drivers.notes.types;

import java.io.Serializable;
import java.util.Date;

/**
 * Provides the status of a document or view entry.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public interface Status extends Serializable {

  /**
   * Returns the date of creation.
   * 
   * @return the date of creation
   */
  Date getCreation();

  /**
   * Returns the date of last modification.
   * 
   * @return the date of last modification
   */
  Date getLastModified();

}
