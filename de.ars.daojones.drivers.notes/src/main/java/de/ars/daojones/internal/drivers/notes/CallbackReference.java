package de.ars.daojones.internal.drivers.notes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import lotus.domino.Base;

/**
 * An abstract reference providing common functionality.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
abstract class CallbackReference<T> implements Reference<T> {

  private final Callable<T> creator;

  private final Set<T> allReferences = new HashSet<T>();

  /**
   * Creates a reference to an object.
   * 
   * @param creator
   *          a command object that is able to find the object initially.
   * @param t
   *          the object, if already existing
   */
  public CallbackReference( final Callable<T> creator, final T t ) {
    this( creator );
    setT( t );
  }

  /**
   * Creates a reference to a {@link Base} object.
   * 
   * @param creator
   *          a command object that is able to create the base object.
   */
  public CallbackReference( final Callable<T> creator ) {
    super();
    this.creator = creator;
  }

  /**
   * Sets the value for the base object.
   * 
   * @param t
   *          the base object
   */
  protected abstract void setT( T t );

  /**
   * Returns the value for the base object.
   * 
   * @return the base object
   */
  protected abstract T getT();

  /**
   * Checks if the object is a valid result for the method {@link #get()}. If
   * this method returns false, the creator's method
   * {@link SerializableCallable#call()} is called.
   * 
   * @param t
   *          the object
   * @return true, if the object is a valid result for the method {@link #get()}
   */
  protected boolean isValid( final T t ) {
    return null != t;
  }

  /**
   * Returns the referenced object.
   * 
   * @return the referenced object
   * @throws Exception
   */
  @Override
  public T get() throws Exception {
    T result = getT();
    if ( !isValid( result ) ) {
      result = this.creator.call();
      allReferences.add( result );
      setT( result );
    }
    return result;
  }

  /**
   * Returns all referenced object that were created before.
   * 
   * @return all referenced objects
   */
  public Collection<T> getAll() {
    return Collections.unmodifiableCollection( allReferences );
  }

}
