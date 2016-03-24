package de.ars.daojones.drivers.notes;

import java.io.Serializable;

import de.ars.daojones.runtime.beans.identification.Identificator;

/**
 * An identificator to a Notes artifact.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the base object type that this identificator refers to
 */
public abstract class NotesIdentificator implements Identificator {

  private static final long serialVersionUID = 1L;

  /**
   * Returns the type of element that this identificator refers to.
   * 
   * @return the type of element that this identificator refers to.
   */
  public abstract NotesElement getElementType();

  /**
   * Transforms the identificator to another type
   * 
   * @param target
   *          the target element type
   * @return the target identificator
   */
  protected abstract NotesIdentificator to( NotesElement target );

  /**
   * Parses an id to a notes identificator.
   * 
   * @param ser
   *          the id
   * @return the notes identificator
   */
  public static NotesIdentificator valueOf( final Serializable ser ) {
    if ( DatabaseIdentificator.isIdentificatorFor( ser ) ) {
      return DatabaseIdentificator.valueOf( ser );
    } else if ( ViewIdentificator.isIdentificatorFor( ser ) ) {
      return ViewIdentificator.valueOf( ser );
    } else if ( DocumentIdentificator.isIdentificatorFor( ser ) ) {
      return DocumentIdentificator.valueOf( ser );
    } else if ( ViewEntryIdentificator.isIdentificatorFor( ser ) ) {
      return ViewEntryIdentificator.valueOf( ser );
    } else {
      return null;
    }
  }

  public static NotesIdentificator to( final NotesIdentificator source, final NotesElement target ) {
    return null != source ? source.to( target ) : null;
  }
}
