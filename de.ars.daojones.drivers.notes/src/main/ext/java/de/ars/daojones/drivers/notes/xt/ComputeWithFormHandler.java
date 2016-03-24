package de.ars.daojones.drivers.notes.xt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A {@link NotesEventHandler} that invokes computeWithForm before the document
 * is saved. It is possible to register <i>transient fields</i> that should not
 * be stored when saving the document. Such fields are part of the document and
 * would otherwise be initialized automatically. A document can be computed with
 * a custom form - it is not limited to the form that the bean is mapped to.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class ComputeWithFormHandler extends NotesEventHandlerAdapter {

  private static final String FORM = "Form";

  private static final Messages bundle = Messages.create( "xt.ComputeWithForm" );

  private final Set<String> transientFields = new HashSet<String>();
  private String alternateForm;

  /**
   * Default constructor.
   */
  public ComputeWithFormHandler() {
    super();
  }

  /**
   * Constructor to set transient fields.
   * 
   * @param transientFields
   *          the transient fields
   */
  public ComputeWithFormHandler( final String... transientFields ) {
    this();
    getTransientFields().addAll( Arrays.asList( transientFields ) );
  }

  /**
   * Returns the transient fields. Modifications to the result of this method
   * directly affect the handler.
   * 
   * @return the transient fields
   */
  public Set<String> getTransientFields() {
    return transientFields;
  }

  /**
   * Returns the name of the alternate form. The alternate form is the Notes
   * form that is used to compute and validate fields of the documents.
   * 
   * @return the alternate form
   */
  public String getAlternateForm() {
    return alternateForm;
  }

  /**
   * Sets the alternate form. The alternate form is the Notes form that is used
   * to compute and validate fields of the documents.
   * 
   * @param alternateForm
   *          the alternate form
   */
  public void setAlternateForm( final String alternateForm ) {
    this.alternateForm = alternateForm;
  }

  @Override
  public void beforeSave( final NotesEventHandlerContext<Document> context, final DocumentSaveConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException {
    final Document doc = context.getSource();
    // Set transient fields to document
    for ( final String tf : getTransientFields() ) {
      final Item firstItem = doc.getFirstItem( tf );
      if ( null != firstItem ) {
        firstItem.setSaveToDisk( false );
      }
    }
    final String form = doc.getItemValueString( ComputeWithFormHandler.FORM );
    final boolean hasAlternateForm = null != getAlternateForm();
    // Set the alternate form
    if ( hasAlternateForm ) {
      doc.replaceItemValue( ComputeWithFormHandler.FORM, getAlternateForm() );
    }
    try {
      final boolean valid = doc.computeWithForm( /*unused*/true, /*raise Error*/true );
      // Should raise error, but local client access just returns false...
      if ( !valid ) {
        final String message = ComputeWithFormHandler.bundle.get( "error.validate",
                doc.getItemValueString( ComputeWithFormHandler.FORM ) );
        throw new DataAccessException( message );
      }
    } finally {
      if ( hasAlternateForm ) {
        if ( null != form ) {
          doc.replaceItemValue( ComputeWithFormHandler.FORM, form );
        } else {
          doc.removeItem( ComputeWithFormHandler.FORM );
        }
      }
    }
  }
}
