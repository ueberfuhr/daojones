package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * Default implementation of a factory model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 * @param <T>
 *          the factory type
 */
public abstract class FactoryModelImpl<T> implements FactoryModel<T> {

  private static final long serialVersionUID = 1L;

  private final String id;
  private final Class<? extends T> clazz;
  private String description;
  private transient T instance;

  /**
   * Constructor.
   * 
   * @param id
   *          the id
   * @param clazz
   *          the implementation class
   */
  public FactoryModelImpl( final String id, final Class<? extends T> clazz ) {
    super();
    this.id = id;
    this.clazz = clazz;
  }

  /**
   * Returns the implementation class of the factory.
   * 
   * @return the clazz the implementation class of the factory
   */
  public Class<? extends T> getClazz() {
    return clazz;
  }

  /**
   * Creates the instance. This is implemented by invoking the default
   * constructor of the class. This method can be overwritten to change the
   * default behaviour.
   * 
   * @param clazz
   *          the implementation class
   * @return the instance
   * @throws ConfigurationException
   */
  protected T createInstance( final Class<? extends T> clazz ) throws ConfigurationException {
    try {
      return clazz.newInstance();
    } catch ( final Exception e ) {
      throw new ConfigurationException( e );
    }
  }

  @Override
  public T getInstance() throws ConfigurationException {
    synchronized ( this ) {
      if ( null == instance ) {
        instance = createInstance( getClazz() );
      }
    }
    return instance;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return getId();
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          the description
   */
  public void setDescription( final String description ) {
    this.description = description;
  }

}
