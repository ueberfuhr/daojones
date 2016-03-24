package de.ars.daojones.internal.drivers.notes.datahandler;

import java.lang.reflect.Array;
import java.util.Vector;

import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.ConvertingDataHandler;
import de.ars.daojones.drivers.notes.DataHandler;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.DataHandlerNotFoundException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.spi.beans.fields.FieldContextWrapper;

public abstract class AbstractArrayDataHandler<T> extends InternalAbstractDataHandler<Object/*T[]*/, Object/*D[]*/> {

  // Do not cast the array to (T[])

  @Override
  protected Object getValueToConvert( final Object item ) {
    return item;
  }

  @Override
  protected Object getDefaultValue( final Class<? extends Object> type ) {
    return Array.newInstance( type.getComponentType(), 0 );
  }

  @Override
  @SuppressWarnings( { "unchecked", "rawtypes" } )
  public Object convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Object> fieldContext,
          final Object value ) throws NotesException, DataHandlerException {

    try {
      final Vector<Object> source = ( Vector<Object> ) value;
      final Class<T> componentType = ( Class<T> ) fieldContext.getType().getComponentType();
      final FieldContext<T> componentContext = new FieldContextWrapper<T>( ( FieldContext ) fieldContext ) {

        @Override
        public Class<? extends T> getType() {
          return componentType;
        }

      };
      // Do not cast this to (T[])
      final Object dest = Array.newInstance( componentType, source.size() );
      if ( !source.isEmpty() ) {
        final DataHandler<T> dataHandler = context.getDataHandlerProvider().findProvider( componentType );
        if ( dataHandler instanceof ConvertingDataHandler ) {
          final ConvertingDataHandler<T, Object> converter = ( ConvertingDataHandler<T, Object> ) dataHandler;
          int index = 0;
          for ( final Object src : source ) {
            final T convertedValue = converter.convertAfterRead( context, componentContext, src );
            Array.set( dest, index, convertedValue );
            index++;
          }
        } else {
          throw new DataHandlerException( this, getMessage( "error.array.invalidhandler", componentType,
                  dataHandler.getClass(), ConvertingDataHandler.class ) );
        }
      }
      return dest;
    } catch ( final DataHandlerNotFoundException e ) {
      throw new DataHandlerException( this, e );
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public Object convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Object> fieldContext,
          final Object value ) throws NotesException, DataHandlerException {
    try {
      Object convertedValue = value;
      if ( null != value && value.getClass().isArray() ) {
        final Vector<Object> v = new Vector<Object>();
        final int len = Array.getLength( value );
        for ( int i = 0; i < len; i++ ) {
          final Object component = Array.get( value, i );
          final Class<?> componentType = null != component ? component.getClass() : value.getClass().getComponentType();
          final FieldContext<Object> componentContext = new FieldContextWrapper<Object>( fieldContext ) {

            @Override
            public Class<? extends Object> getType() {
              return componentType;
            }

          };
          final DataHandler<T> dataHandler = ( DataHandler<T> ) context.getDataHandlerProvider().findProvider(
                  componentType );
          if ( dataHandler instanceof ConvertingDataHandler ) {
            final ConvertingDataHandler<Object, Object> converter = ( ConvertingDataHandler<Object, Object> ) dataHandler;
            final Object convertedComponent = converter.convertForUpdate( context, componentContext, component );
            v.add( convertedComponent );
          } else {
            throw new DataHandlerException( this, getMessage( "error.array.invalidhandler", componentType,
                    dataHandler.getClass(), ConvertingDataHandler.class ) );
          }
        }
        convertedValue = v;
      }
      return convertedValue;
    } catch ( final DataHandlerNotFoundException e ) {
      throw new DataHandlerException( this, e );
    }
  }
}
