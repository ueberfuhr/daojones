package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.internal.runtime.utilities.Configuration;
import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public abstract class BeanModelHelper {

  private BeanModelHelper() {
    super();
  }

  /**
   * Finds a class by name. This includes primitive types.
   * 
   * @param name
   *          the class name
   * @param cl
   *          the classloader
   * @return the class
   * @throws ClassNotFoundException
   */
  public static Class<?> findClass( final String name, final ClassLoader cl ) throws ClassNotFoundException {
    return ReflectionHelper.findClass( name, cl );
  }

  /**
   * Finds a field mapped element. If there are multiple elements (one for
   * reading and one for writing), the first occurence is returned.
   * 
   * @param bean
   *          the bean
   * @param id
   *          the id of the field
   * @return the field mapped element or <tt>null</tt>
   * @see #findFieldModel(Bean, String, boolean)
   */
  public static DatabaseFieldMappedElement findFieldModel( final Bean bean, final String id ) {
    return de.ars.daojones.internal.runtime.configuration.context.BeanModelHelper.findFieldModel( bean, id );
  }

  /**
   * Resolves the database name of the field with a given id.<br/>
   * <br/>
   * <b>Please note:</b> If the id could not be found, the id is resolved to be
   * the database name of the field by default. This behaviour can be switched
   * to throw a ConfigurationException by setting the system property
   * <tt>{@value Configuration#RESOLVE_FIELDS_TO_NAME_PROPERTY}</tt> to
   * <tt>true</tt>.
   * 
   * @param bean
   *          the bean model
   * @param id
   *          the id of the field
   * @return the name of the database field
   * @throws ConfigurationException
   *           if there isn't any field with the given id.
   */
  public static String resolveFieldName( final Bean bean, final String id ) throws ConfigurationException {
    return de.ars.daojones.internal.runtime.configuration.context.BeanModelHelper.resolveFieldName( bean, id );
  }

  /**
   * Finds a field mapped element.
   * 
   * @param bean
   *          the bean
   * @param id
   *          the id of the field
   * @param write
   *          a flag to find readable (<tt>false</tt>) or writable (
   *          <tt>true</tt>) fields
   * @return the field mapped element or <tt>null</tt>
   * @see #findFieldModel(Bean, String)
   */
  public static DatabaseFieldMappedElement findFieldModel( final Bean bean, final String id, final boolean write ) {
    return de.ars.daojones.internal.runtime.configuration.context.BeanModelHelper.findFieldModel( bean, id, write );
  }

}
