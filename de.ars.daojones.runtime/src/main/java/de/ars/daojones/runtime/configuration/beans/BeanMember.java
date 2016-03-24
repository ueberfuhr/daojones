package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import de.ars.daojones.runtime.configuration.beans.Bean;

/**
 * A super class for any member that a bean declares. This is used especially
 * for effective models, where a bean contains members of supertypes and
 * interfaces. Those must be localized with the knowledge of the declaring bean.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@XmlAccessorType( XmlAccessType.FIELD )
public abstract class BeanMember implements Serializable {

  private static final long serialVersionUID = 8294837858452267001L;

  @XmlTransient
  // or transient keyword, do not use both at the same time!!!
  private Bean declaringBean;

  /**
   * Returns the bean that declares this member.
   * 
   * @return the bean that declares this member
   */
  public Bean getDeclaringBean() {
    return declaringBean;
  }

  /**
   * Sets the bean that declares this member.
   * 
   * @param declaringBean
   *          the bean that declares this member
   */
  public void setDeclaringBean( final Bean declaringBean ) {
    this.declaringBean = declaringBean;
  }

}
