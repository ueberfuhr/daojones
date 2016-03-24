package de.ars.daojones.runtime.spi.beans.fields;

/**
 * This exception occurs whenever beans refer to each other. This would end in a
 * {@link StackOverflowError} because of an endless loop. For this reason, this
 * exception is thrown to break such endless loops.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class AlreadyInjectingException extends Exception {

  private static final long serialVersionUID = 1L;

  private final Object bean;

  public AlreadyInjectingException( final Object bean ) {
    super();
    this.bean = bean;
  }

  /**
   * Returns the bean that is currently injected.
   * 
   * @return the bean that is currently injected
   */
  public Object getBean() {
    return bean;
  }

}
