package de.ars.daojones.drivers.notes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Collection;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import de.ars.daojones.internal.drivers.notes.DataHandlerContextWrapper;
import de.ars.daojones.internal.drivers.notes.NotesViewHelper;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.spi.beans.fields.FieldContextWrapper;
import de.ars.daojones.runtime.spi.database.ViewHelper;

/**
 * A data handler that reads the value from a field and tries to convert into
 * the data type. A handler is also responsible for writing fields.
 * 
 * Custom data handler implementations should derive from this class to be safe
 * in case of additional methods of the data handler interface.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the data type
 * @param <D>
 *          the database type
 */
public abstract class AbstractDataHandler<T, D> extends DataHandlerAdapter<T> implements ConvertingDataHandler<T, D> {

  private static final Messages bundle = Messages.create( "DataHandler" );

  @Override
  public T readDocument( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext )
          throws NotesException, DataHandlerException {
    try {
      final Document document = context.getSource();
      final String field = fieldContext.getName();
      if ( document.hasItem( field ) ) {
        final Object item = readDocumentItem( context, fieldContext );
        return convertItemForRead( context, fieldContext, item );
      } else {
        final Class<? extends T> type = fieldContext.getType();
        return getDefaultValue( type );
      }
    } catch ( final IOException e ) {
      throw new DataHandlerException( this, e );
    }
  }

  protected T convertItemForRead( final DataHandlerContext<?> context, final FieldContext<T> fieldContext,
          final Object item ) throws NotesException, DataHandlerException {
    final D valueToConvert = getValueToConvert( item );
    final Class<? extends T> type = fieldContext.getType();
    final T result = convertAfterRead( context, fieldContext, valueToConvert );
    return null != result ? result : getDefaultValue( type );
  }

  /**
   * Returns the default value. The default value is <tt>null</tt>. Overwrite
   * this method for custom default values. For primitive types, the default
   * value of <tt>null</tt> is automatically set to the primitive type's default
   * value.
   * 
   * @param type
   *          the type
   * @return the default value
   */
  protected T getDefaultValue( final Class<? extends T> type ) {
    return null;
  }

  /**
   * Reads a value from the rich text item.
   * 
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @param item
   *          the rich text item
   * @return the value
   * @throws NotesException
   * @throws IOException
   */
  protected Object readFromRichText( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext,
          final RichTextItem item ) throws NotesException, IOException {
    return item.getText();
  }

  /**
   * Reads a document item.
   * 
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @return the value
   * @throws NotesException
   * @throws IOException
   */
  protected Object readDocumentItem( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext )
          throws NotesException, IOException {
    final boolean isRichText = isRichText( context, fieldContext );
    final Document doc = context.getSource();
    final Item item = doc.getFirstItem( fieldContext.getName() );
    if ( isRichText && null != item && Item.RICHTEXT == item.getType() ) {
      return readFromRichText( context, fieldContext, ( RichTextItem ) item );
    } else {
      return doc.getItemValue( fieldContext.getName() );
    }
  }

  /**
   * Finds the value that is converted. The default behaviour is to read the
   * first value of the list.
   * 
   * @param item
   *          the item's value that is returned by
   *          {@link #readDocumentItem(Document, String)}
   * @return the value that is converted.
   * @see #readDocumentItem(Document, String)
   */
  @SuppressWarnings( "unchecked" )
  protected D getValueToConvert( final Object item ) {
    if ( item instanceof Collection ) {
      final Collection<?> values = ( Collection<?> ) item;
      if ( values.isEmpty() ) {
        return null;
      } else {
        return ( D ) values.iterator().next();
      }
    } else {
      return ( D ) item;
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public T readView( final DataHandlerContext<ViewEntry> context, final FieldContext<T> fieldContext )
          throws NotesException, DataHandlerException {
    try {
      final Object fieldValue = readViewColumn( context, fieldContext );
      final Object result = convertItemForRead( context, fieldContext, fieldValue );
      return ( T ) result;
    } catch ( final ParseException e ) {
      throw new DataHandlerException( this, e );
    } catch ( final ConfigurationException e ) {
      throw new DataHandlerException( this, e );
    }
  }

  protected Object readViewColumn( final DataHandlerContext<ViewEntry> context, final FieldContext<T> fieldContext )
          throws NotesException, ParseException, DataHandlerException, ConfigurationException {
    final ViewEntry viewEntry = context.getSource();
    final String columnName = fieldContext.getName();
    Integer columnIndex = ViewHelper.getColumnIndex( columnName );
    final Object fieldValue;
    if ( null == columnIndex ) {
      // field with a name must be resolved
      final View view = NotesViewHelper.getView( viewEntry );
      if ( null == view ) {
        // should never occur
        throw new DataHandlerException( this );
      } else {
        final ViewColumn column = NotesViewHelper.findColumn( view, columnName );
        if ( null == column ) {
          throw new ConfigurationException( AbstractDataHandler.bundle.get( "error.column.notfound", view.getName(),
                  columnName ) );
        } else {
          columnIndex = column.getColumnValuesIndex();
        }
      }
    }
    // field ?1 ?2 ... accessed per index
    @SuppressWarnings( "rawtypes" )
    final Vector columnValues = viewEntry.getColumnValues();
    if ( columnIndex >= columnValues.size() ) {
      throw new ConfigurationException( new IndexOutOfBoundsException( AbstractDataHandler.bundle.get(
              "error.column.invalidindex", columnIndex, 0, columnValues.size() - 1 ) ) );
    } else {
      fieldValue = columnValues.get( columnIndex ); // double, date or string
    }
    return fieldValue;
  }

  @Override
  public void writeView( final DataHandlerContext<ViewEntry> context, final FieldContext<T> fieldContext, final T value )
          throws NotesException, DataHandlerException {
    try {
      final D updateValue = convertForUpdate( context, fieldContext, value );
      writeViewColumn( context, fieldContext, updateValue );
    } catch ( final ParseException e ) {
      throw new DataHandlerException( this, e );
    } catch ( final ConfigurationException e ) {
      throw new DataHandlerException( this, e );
    }
  }

  private void writeViewColumn( final DataHandlerContext<ViewEntry> context, final FieldContext<T> fieldContext,
          final D value ) throws NotesException, ParseException, DataHandlerException, ConfigurationException {
    try {
      final ViewEntry viewEntry = context.getSource();
      final String columnName = fieldContext.getName();
      final View view = NotesViewHelper.getView( viewEntry );
      if ( null == view ) {
        // should never occur
        throw new DataHandlerException( this );
      } else {
        final ViewColumn column = NotesViewHelper.findColumn( view, columnName );
        if ( null == column ) {
          throw new ConfigurationException( AbstractDataHandler.bundle.get( "error.column.notfound", view.getName(),
                  columnName ) );
        } else {
          if ( column.isField() ) {
            final String docField = column.getItemName();
            final Document doc = viewEntry.getDocument();
            writeDocumentItem( new DataHandlerContextWrapper<Document>( context ) {

              @Override
              public Document getSource() throws NotesException {
                return doc;
              }

            }, new FieldContextWrapper<T>( fieldContext ) {

              @Override
              public String getName() {
                return docField;
              }

            }, value );
          } else {
            throw new ConfigurationException( AbstractDataHandler.bundle.get( "error.column.write.formula",
                    view.getName(), columnName ) );
          }
        }
      }
    } catch ( final IOException e ) {
      throw new DataHandlerException( this, e );
    }
  }

  @Override
  public void writeDocument( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext,
          final T value ) throws NotesException, DataHandlerException {
    try {
      final D updateValue = convertForUpdate( context, fieldContext, value );
      writeDocumentItem( context, fieldContext, updateValue );
    } catch ( final IOException e ) {
      throw new DataHandlerException( this, e );
    }
  }

  /**
   * Appends a value to a rich text item.
   * 
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @param value
   *          the value (never <tt>null</tt>)
   * @param item
   *          the rich text item
   * @throws NotesException
   */
  protected void appendToRichText( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext,
          final D value, final RichTextItem item ) throws NotesException, IOException {
    final Reader reader = new StringReader( value.toString() );
    try {
      final BufferedReader br = new BufferedReader( reader );
      try {
        String line;
        boolean first = true;
        while ( null != ( line = br.readLine() ) ) {
          if ( !first ) {
            item.addNewLine();
          }
          item.appendText( line );
          first = false;
        }
      } finally {
        br.close();
      }
    } finally {
      reader.close();
    }
  }

  /**
   * Appends a value to a simple item.
   * 
   * @param value
   *          the value
   * @param updatePolicy
   *          the update policy (one of {@link UpdatePolicy#APPEND} or
   *          {@link UpdatePolicy#INSERT})
   * @param item
   *          the item
   * @throws NotesException
   */
  protected void appendToSimpleItem( final D value, final UpdatePolicy updatePolicy, final Item item )
          throws NotesException {
    @SuppressWarnings( "unchecked" )
    Vector<Object> values = item.getValues();
    if ( null == values ) {
      values = new Vector<Object>();
    }
    if ( updatePolicy == UpdatePolicy.APPEND ) {
      values.add( value );
    } else {
      values.add( 0, value );
    }
    item.setValues( values );
  }

  /**
   * Decides whether the field is a simple or a rich text item.
   * 
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @return <tt>true</tt> if a rich text item is used for the context
   */
  protected boolean isRichText( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext ) {
    return NotesDriverConfiguration.MODEL_PROPERTY_RICHTEXT
            .equals( Properties.getFieldType( fieldContext.getMetadata() ) );
  }

  /**
   * Writes a document item. Implementors of data handlers should use this
   * method to write an item. Overwriting methods should invoke this method to
   * write the document item.
   * 
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @param value
   *          the value
   * @return the new or existing item that can be <tt>null</tt> in case of
   *         writing <tt>null</tt> values which means that an existing field is
   *         removed
   * @throws NotesException
   * @throws IOException
   */
  protected Item writeDocumentItem( final DataHandlerContext<Document> context, final FieldContext<T> fieldContext,
          final D value ) throws NotesException, IOException {
    final Document doc = context.getSource();
    final String name = fieldContext.getName();
    final UpdatePolicy updatePolicy = fieldContext.getUpdatePolicy();
    final boolean isRichText = isRichText( context, fieldContext );
    Item item = doc.getFirstItem( name );
    switch ( updatePolicy ) {
    case INSERT:
    case APPEND:
      if ( isRichText ) {
        if ( null == item ) {
          item = doc.createRichTextItem( name );
        } else if ( item.getType() != Item.RICHTEXT ) {
          // transform type
          final Vector<?> values = item.getValues();
          item.remove();
          item = doc.createRichTextItem( name );
          if ( null != values && !values.isEmpty() ) {
            item.appendToTextList( values );
          }
        }
        if ( null != value ) {
          final RichTextItem rt = ( RichTextItem ) item;
          if ( updatePolicy == UpdatePolicy.APPEND ) {
            appendToRichText( context, fieldContext, value, rt );
          } else {
            final RichTextItem copy = ( RichTextItem ) doc.copyItem( rt, name + "_tmp" );
            rt.remove();
            final RichTextItem newItem = doc.createRichTextItem( name );
            appendToRichText( context, fieldContext, value, newItem );
            newItem.appendRTItem( copy );
            copy.remove();
            item = newItem;
          }
        }
      } else {
        if ( null != value ) {
          if ( null == item ) {
            item = doc.replaceItemValue( name, value );
          } else {
            appendToSimpleItem( value, updatePolicy, item );
          }
        }
      }
      break;
    case REPLACE:
      if ( null != item ) {
        item.remove();
        item = null;
      }
      if ( isRichText ) {
        final RichTextItem rt = doc.createRichTextItem( name );
        if ( null != value ) {
          appendToRichText( context, fieldContext, value, rt );
        }
        item = rt;
      } else {
        item = doc.replaceItemValue( name, null != value ? value : "" );
      }
      break;
    default:
      throw new IllegalArgumentException( "" + updatePolicy );
    }
    return item;
  }

  @Override
  public abstract T convertAfterRead( DataHandlerContext<?> context, final FieldContext<T> fieldContext, final D value )
          throws NotesException, DataHandlerException;

  @SuppressWarnings( "unchecked" )
  @Override
  public D convertForUpdate( final DataHandlerContext<?> context, final FieldContext<T> fieldContext, final T value )
          throws NotesException, DataHandlerException {
    return ( D ) value;
  }

}
