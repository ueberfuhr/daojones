package de.ars.daojones.internal.runtime.test.notes;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.spi.beans.fields.FieldContextImpl;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.notes.NotesTestModel;
import de.ars.daojones.runtime.test.spi.database.DatabaseAccessorImpl;
import de.ars.daojones.runtime.test.spi.database.TestDatabaseEntry;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class NotesDatabaseEntry extends TestDatabaseEntry {

  public NotesDatabaseEntry( final Entry model, final BeanModel beanModel, final TestModelIndex index ) {
    super( model, beanModel, index );
  }

  /**
   * Decides whether a field is mapped to a column value or to the document
   * behind the view entry.
   * 
   * @param fieldContext
   *          the field context
   * @return <tt>true</tt> if a rich text item is used for the context
   */
  protected boolean isDocumentMapped( final FieldContext<?> fieldContext ) {
    return NotesDriverConfiguration.MODEL_PROPERTY_DOCUMENT_MAPPED.equals( Properties.getFieldType( fieldContext
            .getMetadata() ) );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <E> E getFieldValue( final FieldContext<E> context ) throws DataAccessException, UnsupportedFieldTypeException {
    if ( isDocumentMapped( context ) && DatabaseAccessor.class.getName().equals( context.getType().getName() ) ) {
      // read field 
      final FieldContext<String> subContext = new FieldContextImpl<String>( NotesTestModel.DOCUMENT_MAPPING_PROPERTY,
              String.class, UpdatePolicy.REPLACE, context.getMetadata() );
      final String entryId = super.getFieldValue( subContext );
      if ( null != entryId ) {
        try {
          final Entry documentEntry = getIndex().findEntry( entryId );
          return ( E ) new DatabaseAccessorImpl( documentEntry, getIndex() );
        } catch ( final ConfigurationException e ) {
          throw new DataAccessException( e );
        }
      } else {
        return null;
      }
    } else {
      return super.getFieldValue( context );
    }
  }

}
