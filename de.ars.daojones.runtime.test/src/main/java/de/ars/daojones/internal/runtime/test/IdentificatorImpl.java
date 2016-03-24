package de.ars.daojones.internal.runtime.test;

import java.io.Serializable;

import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.test.data.Entry;

public class IdentificatorImpl implements Identificator {

  private static final long serialVersionUID = 1L;

  private final Entry entry;
  private final BeanModel beanModel;

  public IdentificatorImpl( final Entry entry, final BeanModel beanModel ) {
    super();
    this.entry = entry;
    this.beanModel = beanModel;
  }

  @Override
  public Serializable getId( final String application ) {
    return entry.getId();
  }

  public Entry getEntry() {
    return entry;
  }

  public BeanModel getBeanModel() {
    return beanModel;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( beanModel == null ) ? 0 : beanModel.getId().hashCode() );
    result = prime * result + ( ( entry == null ) ? 0 : entry.getId().hashCode() );
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
    final IdentificatorImpl other = ( IdentificatorImpl ) obj;
    if ( beanModel == null ) {
      if ( other.beanModel != null ) {
        return false;
      }
    } else if ( !beanModel.getId().equals( other.beanModel.getId() ) ) {
      return false;
    }
    if ( entry == null ) {
      if ( other.entry != null ) {
        return false;
      }
    } else if ( !entry.getId().equals( other.entry.getId() ) ) {
      return false;
    }
    return true;
  }

}
