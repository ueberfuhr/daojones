package de.ars.daojones.internal.drivers.notes;

import java.util.concurrent.Callable;

/**
 * A reference managing objects per thread.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type of the referenced object
 */
class ThreadReference<T> extends CallbackReference<T> {

  private final ThreadLocal<T> t = new ThreadLocal<T>();

  /**
   * Creates a reference to an object.
   *
   * @param creator
   *          a command object that is able to create the object.
   */
  public ThreadReference( final Callable<T> creator ) {
    super( creator );
  }

  @Override
  protected T getT() {
    return t.get();
  }

  @Override
  protected void setT( final T t ) {
    this.t.set( t );
  }

}
