package de.ars.daojones.drivers.notes.annotations;

/**
 * The type of entity that defines how to handle the entity.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public enum MIMEEntityType {

  /**
   * The MIME Entity is attached to the document.
   */
  ATTACHMENT,

  /**
   * The MIME Entity is directly placed into the field.
   */
  INLINE;

}
