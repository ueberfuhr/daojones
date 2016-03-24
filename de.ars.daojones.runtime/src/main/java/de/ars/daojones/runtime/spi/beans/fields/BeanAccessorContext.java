package de.ars.daojones.runtime.spi.beans.fields;

import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.configuration.beans.TypeMappedElement;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A transfer object for bean accessor invokations that provides context
 * information.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the bean type
 */
public final class BeanAccessorContext<T> {

  private final Class<T> beanType;
  private final BeanModel model;
  private final ConnectionProvider connectionProvider;
  private final DatabaseAccessor db;
  private final BeanModelManager beanModelManager;

  /**
   * Construcotr.
   * 
   * @param beanType
   *          the bean type
   * @param connectionProvider
   *          the connection provider
   * @param db
   *          the database accessor
   * @param beanModelManager
   *          the bean model manager
   */
  public BeanAccessorContext( final Class<T> beanType, final BeanModel model,
          final ConnectionProvider connectionProvider, final DatabaseAccessor db,
          final BeanModelManager beanModelManager ) {
    super();
    this.beanType = beanType;
    this.model = model;
    this.connectionProvider = connectionProvider;
    this.db = db;
    this.beanModelManager = beanModelManager;
  }

  /**
   * Loads a class that is referenced by the model.
   * 
   * @param referencingModel
   *          the model element that refers to the class name
   * @param className
   *          the class name
   * @return the class
   * @throws ClassNotFoundException
   */
  public Class<?> loadClass( final String className ) throws ClassNotFoundException {
    return loadClass( className, getModel().getBean() );
  }

  /**
   * Loads the class of a type mapped element.
   * 
   * @param element
   *          the type mapped element
   * @return the class
   * @throws ClassNotFoundException
   */
  public Class<?> loadClass( final TypeMappedElement element ) throws ClassNotFoundException {
    return loadClass( element.getType(), element );
  }

  /**
   * Loads a class that is referenced by the model.<br/>
   * <br/>
   * <b>Please note:</b> This method uses the classloader from the client
   * application, so this will work for different classloader architectures.
   * 
   * @param className
   *          the class name
   * @param referencingModel
   *          the model element that refers to the class name
   * @return the class
   * @throws ClassNotFoundException
   */
  public Class<?> loadClass( final String className, final TypeMappedElement referencingModel )
          throws ClassNotFoundException {
    //    final Class<?> referencingType = ReflectionHelper.findClass( getBeanType(),
    //            referencingModel.getType() );
    final Class<?> referencingType = ReflectionHelper.findClass( referencingModel.getType(), getBeanType()
            .getClassLoader() );
    return ReflectionHelper.findClass( className, referencingType.getClassLoader() );
  }

  /**
   * Returns the bean type.
   * 
   * @return the bean Type
   */
  public Class<T> getBeanType() {
    return beanType;
  }

  /**
   * Returns the connection provider.
   * 
   * @return the connection provider
   */
  public ConnectionProvider getConnectionProvider() {
    return connectionProvider;
  }

  /**
   * Returns the database accessor.
   * 
   * @return the database accessor
   */
  public DatabaseAccessor getDb() {
    return db;
  }

  /**
   * Returns the bean model.
   * 
   * @return the bean model
   */
  public BeanModel getModel() {
    return model;
  }

  /**
   * Returns the bean model manager.
   * 
   * @return the bean model manager
   */
  public BeanModelManager getBeanModelManager() {
    return beanModelManager;
  }

  /**
   * Derives a bean accessor context for the given bean.
   * 
   * @param <T>
   *          the bean type
   * @param context
   *          the current accessor context that the new one is derived from
   * @param beanType
   *          the bean type class
   * @return the derived bean accessor context
   * @throws ConfigurationException
   *           if there isn't any model configured for this bean
   */
  public static <T> BeanAccessorContext<T> createSubContextFor( final BeanAccessorContext<?> context,
          final Class<T> beanType ) throws ConfigurationException {
    return BeanAccessorContext.createSubContextFor( context, beanType, context.getDb() );
  }

  /**
   * Derives a bean accessor context for the given bean.
   * 
   * @param <T>
   *          the bean type
   * @param context
   *          the current accessor context that the new one is derived from
   * @param beanType
   *          the bean type class
   * @param db
   *          the database accessor
   * @return the derived bean accessor context
   * @throws ConfigurationException
   *           if there isn't any model configured for this bean
   */
  public static <T> BeanAccessorContext<T> createSubContextFor( final BeanAccessorContext<?> context,
          final Class<T> beanType, final DatabaseAccessor db ) throws ConfigurationException {
    final String applicationId = context.getModel().getId().getApplicationId();
    final BeanModelManager beanModelManager = context.getBeanModelManager();
    final BeanModel beanModel = beanModelManager.getEffectiveModel( applicationId, beanType );
    final ConnectionProvider cp = context.getConnectionProvider();
    return new BeanAccessorContext<T>( beanType, beanModel, cp, db, beanModelManager );
  }

  /**
   * Creates a bean accessor context for the given bean and field.
   * 
   * @param <T>
   *          the bean type
   * @param ctx
   *          the application context
   * @param beanType
   *          the bean type
   * @param db
   *          the database accessor
   * @return the bean accessor context
   * @throws ConfigurationException
   */
  public static <T> BeanAccessorContext<T> createContextFor( final ApplicationContext ctx, final Class<T> beanType,
          final DatabaseAccessor db ) throws ConfigurationException {
    final String applicationId = ctx.getApplicationId();
    final BeanModelManager beanModelManager = ctx.getBeanModelManager();
    final BeanModel beanModel = beanModelManager.getEffectiveModel( applicationId, beanType );
    final ConnectionProvider cp = ctx.getConnectionProvider();
    return new BeanAccessorContext<T>( beanType, beanModel, cp, db, beanModelManager );
  }

}
