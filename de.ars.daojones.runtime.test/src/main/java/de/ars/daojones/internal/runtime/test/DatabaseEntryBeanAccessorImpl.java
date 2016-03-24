package de.ars.daojones.internal.runtime.test;

import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.beans.fields.ConverterContext;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.fields.TypeConverter;
import de.ars.daojones.runtime.beans.fields.TypeConverterHelper;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.spi.beans.fields.AlreadyInjectingException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.beans.fields.DefaultBeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContextImpl;
import de.ars.daojones.runtime.spi.beans.fields.ReflectionHelper;
import de.ars.daojones.runtime.spi.beans.fields.ReflectionHelper.MemberDescription;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

public class DatabaseEntryBeanAccessorImpl extends DefaultBeanAccessor implements BeanAccessorProvider {

  @SuppressWarnings( "unused" )
  private static final Messages logger = Messages.create( "Accessor" );

  @Override
  public BeanAccessor getBeanAccessor() {
    return this;
  }

  @Override
  public Identificator getIdentificator( final BeanModel model, final Object bean ) throws DataAccessException,
          ConfigurationException {
    final DatabaseEntry entry = ( DatabaseEntry ) bean;
    return entry.getIdentificator();
  }

  @Override
  public void setIdentificator( final BeanModel model, final Object bean, final Identificator identificator )
          throws DataAccessException, ConfigurationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getValue( final Object bean, final DatabaseFieldMappedElement field ) throws FieldAccessException,
          ConfigurationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setValue( final Object bean, final DatabaseFieldMappedElement field, final Object value )
          throws FieldAccessException, ConfigurationException, ClassCastException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T createBeanInstance( final BeanAccessorContext<T> context ) throws FieldAccessException,
          DataAccessException, ConfigurationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> void inject( final BeanAccessorContext<T> context, final T bean ) throws FieldAccessException,
          DataAccessException, ConfigurationException, AlreadyInjectingException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> void store( final BeanAccessorContext<T> context, final T bean ) throws FieldAccessException,
          DataAccessException, ConfigurationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> void reinjectAfterStore( final BeanAccessorContext<T> context, final T bean ) throws FieldAccessException,
          DataAccessException, ConfigurationException, AlreadyInjectingException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected BeanModel getBeanModel( final ApplicationContext ctx, final Object bean, final boolean mandatory )
          throws ConfigurationException {
    final DatabaseEntry entry = ( DatabaseEntry ) bean;
    final BeanModel beanModel = entry.getBeanModel();
    return beanModel;
  }

  @Override
  public Object getDatabaseValue( final ApplicationContext ctx, final Object entryBean, final String field )
          throws FieldAccessException, ConfigurationException {
    /*
     * Normally, the bean is the instance with values that were already read from the database. Within the Test Support, we only have an entry.
     */
    final DatabaseEntry entry = ( DatabaseEntry ) entryBean;
    final BeanModel beanModel = entry.getBeanModel();
    final DatabaseFieldMappedElement fieldModel = findFieldModel( beanModel, entryBean, field );
    try {
      // we need to find the type of the mapped element -> reflection!
      final MemberDescription member = ReflectionHelper.findMember( fieldModel, entryBean.getClass().getClassLoader() );
      Class<?> type = member.getType();
      // we have to find out the type of the field
      final BeanAccessorContext<?> context = BeanAccessorContext.createContextFor( ctx, member.getMember()
              .getDeclaringClass(), null );
      final Converter converter = createConverter( context, type, fieldModel.getDeclaringBean(),
              fieldModel.getFieldMapping() );
      if ( null != converter ) {
        if ( converter instanceof TypeConverter<?, ?> ) {
          final ConverterContext converterContext = new ConverterContext( context, entryBean, fieldModel, type ) {
          };
          type = TypeConverterHelper.getDatabaseType( ( TypeConverter<?, ?> ) converter, converterContext );
        }
      }
      return entry.getFieldValue( FieldContextImpl.from( fieldModel.getFieldMapping(), type ) );
    } catch ( final DataAccessException e ) {
      throw new FieldAccessException( fieldModel.getFieldMapping().getName(), e );
    }

  }
}
