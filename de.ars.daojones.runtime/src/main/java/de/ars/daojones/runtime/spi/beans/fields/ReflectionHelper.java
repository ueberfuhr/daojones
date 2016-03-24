package de.ars.daojones.runtime.spi.beans.fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import de.ars.daojones.internal.runtime.configuration.beans.ModelElementHandler;
import de.ars.daojones.internal.runtime.configuration.beans.ModelElementHandler.ModelElementHandlingException;
import de.ars.daojones.internal.runtime.configuration.context.BeanModelHelper;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.Invocable;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A helper class that provides utility methods for inspecting bean classes.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class ReflectionHelper {

  private ReflectionHelper() {
    super();
  }

  public static interface MemberDescription {

    /**
     * Returns the name of the member.
     * 
     * @return the name of the member
     */
    String getName();

    /**
     * Returns the type of the member.
     * 
     * @return the type of the member
     */
    Class<?> getType();

    /**
     * Returns the member
     * 
     * @return the member
     */
    Member getMember();

    /**
     * Finds an annotation.
     * 
     * @param <A>
     *          the annotation type
     * @param annotationType
     *          the annotation type
     * @return the annotation or <tt>null</tt>, if not found
     */
    public <A extends Annotation> A getAnnotation( Class<A> annotationType );

  }

  private static abstract class MemberDescriptionImpl implements MemberDescription {

    private final Member member;

    public MemberDescriptionImpl( final Member member ) {
      super();
      this.member = member;
    }

    @Override
    public String getName() {
      return member.getName();
    }

    @Override
    public Member getMember() {
      return member;
    }

    @Override
    public <A extends Annotation> A getAnnotation( final Class<A> annotationType ) {
      return member instanceof AnnotatedElement ? ( ( AnnotatedElement ) member ).getAnnotation( annotationType )
              : null;
    }

  }

  /**
   * Finds the corresponding member for the model.
   * 
   * @param model
   *          the model
   * @param cl
   *          the class loader
   * @return the member
   * @throws ConfigurationException
   * @see MethodParameter
   */
  public static MemberDescription findMember( final DatabaseFieldMappedElement model, final ClassLoader cl )
          throws ConfigurationException {
    try {
      final ThreadLocal<MemberDescription> result = new ThreadLocal<MemberDescription>();
      new ModelElementHandler() {

        @Override
        protected void handle( final de.ars.daojones.runtime.configuration.beans.MethodParameter model )
                throws ConfigurationException {
          try {
            final Class<?> declaringClass = de.ars.daojones.internal.runtime.utilities.ReflectionHelper.findClass(
                    model.getDeclaringBean().getType(), cl );
            final Invocable invocable = model.getInvocable();
            final int idx = invocable.getParameters().indexOf( model );
            final MethodParameterImpl methodParameterImpl;
            if ( invocable instanceof Constructor ) {
              final Constructor constructorModel = ( Constructor ) invocable;
              final java.lang.reflect.Constructor<?> constructor = BeanModelHelper.findConstructor( declaringClass,
                      constructorModel );
              methodParameterImpl = new MethodParameterImpl( constructor, idx );
            } else {
              final Method methodModel = ( Method ) invocable;
              final java.lang.reflect.Method method = BeanModelHelper.findMethod( declaringClass, methodModel );
              methodParameterImpl = new MethodParameterImpl( method, idx );
            }
            result.set( new MemberDescriptionImpl( methodParameterImpl ) {

              @Override
              public Class<?> getType() {
                return methodParameterImpl.getType();
              }

            } );
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchMethodException e ) {
            throw new ConfigurationException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new ConfigurationException( e );
          }
        }

        @Override
        protected void handle( final MethodResult model ) throws ConfigurationException, ModelElementHandlingException {
          try {
            final Invocable invocable = model.getInvocable();
            final Method methodModel = ( Method ) invocable;
            final Class<?> declaringClass = de.ars.daojones.internal.runtime.utilities.ReflectionHelper.findClass(
                    model.getDeclaringBean().getType(), cl );
            final java.lang.reflect.Method method = BeanModelHelper.findMethod( declaringClass, methodModel );
            result.set( new MemberDescriptionImpl( method ) {

              @Override
              public Class<?> getType() {
                return method.getReturnType();
              }
            } );
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchMethodException e ) {
            throw new ConfigurationException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new ConfigurationException( e );
          }
        }

        @Override
        protected void handle( final Field model ) throws ConfigurationException {
          try {
            final Class<?> declaringClass = de.ars.daojones.internal.runtime.utilities.ReflectionHelper.findClass(
                    model.getDeclaringBean().getType(), cl );
            final java.lang.reflect.Field field = declaringClass.getDeclaredField( model.getName() );
            result.set( new MemberDescriptionImpl( field ) {

              @Override
              public Class<?> getType() {
                return field.getType();
              }
            } );
          } catch ( final SecurityException e ) {
            throw new ConfigurationException( e );
          } catch ( final NoSuchFieldException e ) {
            throw new ConfigurationException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new ConfigurationException( e );
          }
        }
      }.handle( model );
      return result.get();
    } catch ( final ModelElementHandlingException e ) {
      throw new ConfigurationException( e );
    }
  }
}
