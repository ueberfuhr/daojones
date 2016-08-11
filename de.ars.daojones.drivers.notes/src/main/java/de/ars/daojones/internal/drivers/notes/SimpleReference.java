package de.ars.daojones.internal.drivers.notes;

import java.util.concurrent.Callable;

/**
 * A reference holding a single object that is shared between multiple threads.
 *
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
class SimpleReference<T> extends CallbackReference<T> {

  private T t;

  /**
   * Creates a reference to an object.
   *
   * @param creator
   *          a command object that is able to create the object.
   */
  public SimpleReference( final Callable<T> creator ) {
    super( creator );
  }

  /**
   * @see de.ars.daojones.internal.drivers.notes.CallbackReference#getT()
   */
  @Override
  protected T getT() {
    return t;
  }

  /**
   * @see de.ars.daojones.internal.drivers.notes.CallbackReference#setT(lotus.domino.Base)
   */
  @Override
  protected void setT( final T t ) {
    this.t = t;
  }

}
