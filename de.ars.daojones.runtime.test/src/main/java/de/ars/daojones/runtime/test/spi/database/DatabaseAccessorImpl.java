package de.ars.daojones.runtime.test.spi.database;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import de.ars.daojones.internal.runtime.test.AwareNotFoundException;
import de.ars.daojones.internal.runtime.test.AwareRegistry;
import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.spi.beans.fields.FieldContextWrapper;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.Property;

public class DatabaseAccessorImpl implements Closeable, DatabaseAccessor {

  private static final Messages bundle = Messages.create( "spi.database.DatabaseAccessorImpl" );

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private static final AwareRegistry<Object, DataHandler<Object>> dataHandlers = new AwareRegistry<Object, DataHandler<Object>>(
          ( Class ) DataHandler.class );

  private final Entry model;
  private final TestModelIndex index;

  public DatabaseAccessorImpl( final Entry model, final TestModelIndex index ) {
    super();
    this.model = model;
    this.index = index;
  }

  protected Entry getModel() {
    return model;
  }

  protected TestModelIndex getIndex() {
    return index;
  }

  protected Property getProperty( final String name, final boolean create ) {
    synchronized ( model ) {
      for ( final Property p : model.getProperties() ) {
        if ( p.getName().equals( name ) ) {
          return p;
        }
      }
      // Not found
      if ( create ) {
        final Property p = new Property();
        p.setName( name );
        model.getProperties().add( p );
        return p;
      } else {
        return null;
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  private static <E> DataHandler<E> getDataHandler( final FieldContext<E> context )
          throws UnsupportedFieldTypeException {
    try {
      // manage array types in common
      final Class<? extends E> type = context.getType();
      if ( type.isArray() ) {
        final Class<?> componentType = type.getComponentType();
        final DataHandler<Object> componentTypeHandler = DatabaseAccessorImpl.dataHandlers
                .findAwareNotNull( componentType );
        return new DataHandler<E>() {

          private static final String ELEMENT_START = "[";
          private static final String ELEMENT_END = "]";

          @Override
          public Class<? extends E> getKeyType() {
            return type;
          }

          @SuppressWarnings( "rawtypes" )
          @Override
          public E convertRead( final FieldContext<E> context, final TestModelIndex index, final String value )
                  throws DataAccessException, UnsupportedFieldTypeException {
            if ( null == value ) {
              return null;
            } else {
              String elements$ = value.trim();
              final boolean startsWithStart = elements$.startsWith( ELEMENT_START );
              if ( startsWithStart || elements$.endsWith( ELEMENT_END ) ) {
                elements$ = elements$.substring( 1, elements$.length() - 1 );
                if ( elements$.length() == 0 ) {
                  return ( E ) Array.newInstance( componentType, 0 );
                } else {
                  final String[] elements = elements$.split( "((\\" + ELEMENT_END + "\\" + ELEMENT_START + "))" );
                  final Object result = Array.newInstance( componentType, elements.length );
                  for ( int i = 0; i < elements.length; i++ ) {
                    final Object convertedValue = componentTypeHandler.convertRead( new FieldContextWrapper<Object>(
                            ( FieldContext ) context ) {
                      @Override
                      public Class<? extends Object> getType() {
                        return componentType;
                      }
                    }, index, elements[i] );
                    Array.set( result, i, convertedValue );
                  }
                  return ( E ) result;
                }
              } else {
                try {
                  if ( startsWithStart ) {
                    // !endsWithStart
                    throw new ParseException( elements$, elements$.length() - 1 );
                  } else {
                    // !startsWithStart
                    throw new ParseException( elements$, 0 );
                  }
                } catch ( final ParseException e ) {
                  throw new DataAccessException( DatabaseAccessorImpl.bundle.get( "error.parse", context.getName() ), e );
                }
              }
            }
          }

          @SuppressWarnings( "rawtypes" )
          @Override
          public String convertWrite( final FieldContext<E> context, final TestModelIndex index, final E value )
                  throws DataAccessException, UnsupportedFieldTypeException {
            if ( null == value ) {
              return null;
            } else {
              final int length = Array.getLength( value );
              final StringBuffer sb = new StringBuffer();
              for ( int i = 0; i < length; i++ ) {
                final Object val = Array.get( value, i );
                final String convertedValue = componentTypeHandler.convertWrite( new FieldContextWrapper<Object>(
                        ( FieldContext ) context ) {
                  @Override
                  public Class<? extends Object> getType() {
                    return componentType;
                  }
                }, index, val );
                sb.append( ELEMENT_START ).append( convertedValue ).append( ELEMENT_END );
              }
              return sb.toString();
            }
          }
        };
      } else {
        return ( de.ars.daojones.runtime.test.spi.database.DataHandler<E> ) DatabaseAccessorImpl.dataHandlers
                .findAwareNotNull( type );
      }
    } catch ( final AwareNotFoundException e ) {
      throw new UnsupportedFieldTypeException( context.getName(), context.getType(), e );
    }
  }

  @Override
  public Identificator getIdentificator() throws DataAccessException {
    return index.createIdentificator( model );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <E> E getFieldValue( final FieldContext<E> context ) throws DataAccessException, UnsupportedFieldTypeException {
    final Property property = getProperty( context.getName(), false );
    E result = null;
    if ( null != property ) {
      final Object value = property.getValue();
      if ( value instanceof String ) {
        result = DatabaseAccessorImpl.getDataHandler( context ).convertRead( context, index, ( String ) value );
      } else {
        result = ( E ) value;
      }
    }
    return result;
  }

  @Override
  public <E> void setFieldValue( final FieldContext<E> context, final E value ) throws DataAccessException,
          UnsupportedFieldTypeException {
    final UpdatePolicy updatePolicy = context.getUpdatePolicy();
    final Property property = getProperty( context.getName(), true );
    final String pValue;
    if ( null != value ) {
      pValue = DatabaseAccessorImpl.getDataHandler( context ).convertWrite( context, index, value );
    } else {
      pValue = null;
    }
    final String newValue;
    switch ( updatePolicy ) {
    case APPEND: {
      final StringBuffer sbValue = new StringBuffer();
      final Object currentValue = property.getValue();
      if ( null != currentValue ) {
        sbValue.append( currentValue.toString() );
      }
      if ( null != pValue ) {
        sbValue.append( pValue );
      }
      newValue = null != pValue || null != currentValue ? sbValue.toString() : null;
      break;
    }
    case INSERT: {
      final StringBuffer sbValue = new StringBuffer();
      if ( null != pValue ) {
        sbValue.append( pValue );
      }
      final Object currentValue = property.getValue();
      if ( null != currentValue ) {
        sbValue.append( currentValue.toString() );
      }
      newValue = null != pValue || null != currentValue ? sbValue.toString() : null;
      break;
    }
    default:
      // replace
      newValue = pValue;
      break;
    }
    property.setValue( newValue );
  }

  @Override
  public String[] getFields() throws DataAccessException {
    final List<String> result = new LinkedList<String>();
    for ( final Property p : model.getProperties() ) {
      result.add( p.getName() );
    }
    return result.toArray( new String[result.size()] );
  }

  @Override
  public void close() throws IOException {
    // nothing to do
  }

}
