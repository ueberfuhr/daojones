package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.beans.Bean;

/**
 * Default implementation of a bean model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class BeanModelImpl implements BeanModel {

  private static final long serialVersionUID = 1L;

  private final Id id;
  private final Bean bean;
  private String description;

  private static Id createId( final String applicationId, final Bean bean ) {
    return new Id( applicationId, bean.getType() );
  }

  /**
   * Constructor.
   * 
   * @param application
   *          the application
   * @param bean
   *          the bean
   */
  public BeanModelImpl( final String application, final Bean bean ) {
    super();
    this.bean = bean;
    id = BeanModelImpl.createId( application, bean );
  }

  @Override
  public Id getId() {
    return id;
  }

  @Override
  public String getName() {
    return getId().getApplicationId() + ":" + getId().getTypeId();
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

  @Override
  public Bean getBean() {
    return bean;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
    return result;
  }

  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return false;
    }
    final BeanModelImpl other = ( BeanModelImpl ) obj;
    if ( id == null ) {
      if ( other.id != null ) {
        return false;
      }
    } else if ( !id.equals( other.id ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "BeanModel [id=" ).append( id ).append( ", bean=" ).append( bean ).append( "]" );
    return builder.toString();
  }

}
