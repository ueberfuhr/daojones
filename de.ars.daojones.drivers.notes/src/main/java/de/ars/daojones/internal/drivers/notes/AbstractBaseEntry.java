package de.ars.daojones.internal.drivers.notes;

import java.io.IOException;

import lotus.domino.Base;
import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.drivers.notes.DataHandler;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.DataHandlerNotFoundException;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

public abstract class AbstractBaseEntry<Source extends Base> implements DatabaseEntry,
        DataHandler.DataHandlerContext<Source> {

  private static final Messages bundle = Messages.create( "AbstractBaseEntry" );

  private final Source source;
  private final BeanModel beanModel;
  private final DataHandlerProvider dataHandlerProvider;

  public AbstractBaseEntry( final Source source, final BeanModel beanModel,
          final DataHandlerProvider dataHandlerProvider ) {
    super();
    this.source = source;
    this.beanModel = beanModel;
    this.dataHandlerProvider = dataHandlerProvider;
  }

  @Override
  public Source getSource() throws NotesException {
    return source;
  }

  @Override
  public Session getSession() throws NotesException {
    return getDatabase().getParent();
  }

  @Override
  public void close() throws IOException {
    try {
      source.recycle();
    } catch ( final NotesException e ) {
      throw new IOException( e );
    }
  }

  protected abstract <E> E getFieldValue( final FieldContext<E> context, DataHandler<E> handler )
          throws DataAccessException, NotesException, DataHandlerException;

  private static <E> E exception( final FieldContext<E> context, final Throwable cause ) throws DataAccessException {
    final Class<? extends E> type = context.getType();
    final String key;
    final Class<?> simpleType;
    if ( type.isArray() ) {
      key = "error.fieldaccess.array";
      simpleType = type.getComponentType();
    } else {
      key = "error.fieldaccess.simple";
      simpleType = type;
    }
    throw new DataAccessException( AbstractBaseEntry.bundle.get( key, context.getName(), simpleType.getName(),
            context.getUpdatePolicy() ), cause );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <E> E getFieldValue( final FieldContext<E> context ) throws DataAccessException, UnsupportedFieldTypeException {
    try {
      final DataHandler<E> handler = dataHandlerProvider.findProvider( ( Class<E> ) context.getType() );
      final E result = getFieldValue( context, handler );
      return result;
    } catch ( final DataHandlerNotFoundException e ) {
      throw new UnsupportedFieldTypeException( context.getName(), context.getType(), e );
    } catch ( final Exception e ) {
      return AbstractBaseEntry.exception( context, e );
    }
  }

  protected abstract <E> void setFieldValue( final FieldContext<E> context, DataHandler<E> handler, final E value )
          throws DataAccessException, NotesException, DataHandlerException;

  @SuppressWarnings( "unchecked" )
  @Override
  public <E> void setFieldValue( final FieldContext<E> context, final E value ) throws DataAccessException,
          UnsupportedFieldTypeException {
    try {
      final DataHandler<E> handler = dataHandlerProvider.findProvider( ( Class<E> ) context.getType() );
      setFieldValue( context, handler, value );
    } catch ( final NotesException e ) {
      AbstractBaseEntry.exception( context, e );
    } catch ( final DataHandlerNotFoundException e ) {
      AbstractBaseEntry.exception( context, e );
    } catch ( final DataHandlerException e ) {
      AbstractBaseEntry.exception( context, e );
    }
  }

  @Override
  public BeanModel getBeanModel() {
    return beanModel;
  }

  @Override
  public DataHandlerProvider getDataHandlerProvider() {
    return dataHandlerProvider;
  }

  @Override
  public String getApplication() {
    return getBeanModel().getId().getApplicationId();
  }

}
