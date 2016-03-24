package de.ars.daojones.internal.runtime.security;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.security.auth.callback.CallbackHandler;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;
import de.ars.daojones.runtime.spi.security.CallbackHandlerContext;
import de.ars.daojones.runtime.spi.security.CallbackHandlerFactory;

public class GenericCallbackHandlerFactory implements CallbackHandlerFactory {

  public static final String CLASS_PROPERTY = "class";

  private static final Messages bundle = Messages.create( "runtime.security.GenericCallbackHandlerFactory" );

  @Override
  public CallbackHandler createCallbackHandler( final CallbackHandlerContext context ) throws CallbackHandlerException {
    final Properties props = context.getProperties();
    final String implementationClassName = props.getProperty( GenericCallbackHandlerFactory.CLASS_PROPERTY );
    props.remove( implementationClassName );
    if ( null != implementationClassName ) {
      try {
        final Class<?> implementationClass = ReflectionHelper.findClass( implementationClassName,
                GenericCallbackHandlerFactory.class.getClassLoader() );
        final CallbackHandler handler = ( CallbackHandler ) ReflectionHelper.newInstance( implementationClass );
        // further props except implementation class
        if ( !props.isEmpty() ) {
          final BeanInfo beanInfo = Introspector.getBeanInfo( implementationClass );
          final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
          for ( final PropertyDescriptor propertyDescriptor : propertyDescriptors ) {
            final String propertyName = propertyDescriptor.getName();
            final String propertyValue$ = props.getProperty( propertyName );
            if ( null != propertyValue$ ) {
              final Method writeMethod = propertyDescriptor.getWriteMethod();
              if ( null == writeMethod ) {
                throw new NoSuchMethodException( GenericCallbackHandlerFactory.bundle.get(
                        "implementation.class.property.no.write.method", implementationClass.getName(), propertyName ) );
              } else {
                final Class<?> propertyType = propertyDescriptor.getPropertyType();
                final Object propertyValue;
                if ( propertyType.isAssignableFrom( String.class ) ) {
                  propertyValue = propertyValue$;
                } else {
                  final Class<?> localEditorClass = propertyDescriptor.getPropertyEditorClass();
                  final PropertyEditor editor;
                  if ( null != localEditorClass ) {
                    editor = ( PropertyEditor ) ReflectionHelper.newInstance( localEditorClass );
                  } else {
                    editor = PropertyEditorManager.findEditor( propertyType );
                  }
                  if ( null == editor ) {
                    throw new CallbackHandlerException( GenericCallbackHandlerFactory.bundle.get(
                            "implementation.class.property.no.editor", implementationClass.getName(), propertyName,
                            propertyType.getName() ) );
                  } else {
                    try {
                      editor.setAsText( propertyValue$ );
                      propertyValue = editor.getValue();
                    } catch ( final IllegalArgumentException e ) {
                      throw new CallbackHandlerException( GenericCallbackHandlerFactory.bundle.get(
                              "implementation.class.property.editor.conversion", implementationClass.getName(),
                              propertyName, propertyValue$ ), e );
                    }
                  }
                }
                writeMethod.invoke( handler, propertyValue );
              }
            }
            // remove independent from the property value, because an entry could be available with a value of null
            props.remove( propertyName );
          }
          // further properties that did not match the implementation class
          if ( !props.isEmpty() ) {
            throw new CallbackHandlerException( GenericCallbackHandlerFactory.bundle.get(
                    "implementation.class.properties.not.found", implementationClass.getName(), props.toString() ) );
          }
        }
        return handler;
      } catch ( final Exception e ) {
        throw new CallbackHandlerException( e );
      }
    } else {
      throw new CallbackHandlerException( GenericCallbackHandlerFactory.bundle.get( "implementation.class.missing",
              GenericCallbackHandlerFactory.CLASS_PROPERTY ) );
    }
  }
}
