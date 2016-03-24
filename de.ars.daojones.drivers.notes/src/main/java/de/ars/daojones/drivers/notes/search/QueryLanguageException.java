package de.ars.daojones.drivers.notes.search;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.SearchCriterion;

/**
 * An exception that occurs when building the query language.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class QueryLanguageException extends Exception {

  private static final long serialVersionUID = 1L;

  private final SearchCriterion criterion;
  private final Bean model;

  /**
   * Constructs a new exception with <code>null</code> as its detail message.
   * The cause is not initialized, and may subsequently be initialized by a call
   * to {@link #initCause}.
   * 
   * @param criterion
   *          the search criterion
   * @param model
   *          the bean model
   */
  public QueryLanguageException( final SearchCriterion criterion, final Bean model ) {
    super();
    this.criterion = criterion;
    this.model = model;
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is
   * not initialized, and may subsequently be initialized by a call to
   * {@link #initCause}.
   * 
   * @param criterion
   *          the search criterion
   * @param model
   *          the bean model
   * @param message
   *          the detail message. The detail message is saved for later
   *          retrieval by the {@link #getMessage()} method.
   */
  public QueryLanguageException( final SearchCriterion criterion, final Bean model, final String message ) {
    super( message );
    this.criterion = criterion;
    this.model = model;
  }

  /**
   * Constructs a new exception with the specified cause and a detail message of
   * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains
   * the class and detail message of <tt>cause</tt>). This constructor is useful
   * for exceptions that are little more than wrappers for other throwables (for
   * example, {@link java.security.PrivilegedActionException}).
   * 
   * @param criterion
   *          the search criterion
   * @param model
   *          the bean model
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
   *          and indicates that the cause is nonexistent or unknown.)
   */
  public QueryLanguageException( final SearchCriterion criterion, final Bean model, final Throwable cause ) {
    super( cause );
    this.criterion = criterion;
    this.model = model;
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   * <p>
   * Note that the detail message associated with <code>cause</code> is
   * <i>not</i> automatically incorporated in this exception's detail message.
   * 
   * @param criterion
   *          the search criterion
   * @param model
   *          the bean model
   * @param message
   *          the detail message (which is saved for later retrieval by the
   *          {@link #getMessage()} method).
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
   *          and indicates that the cause is nonexistent or unknown.)
   */
  public QueryLanguageException( final SearchCriterion criterion, final Bean model, final String message,
          final Throwable cause ) {
    super( message, cause );
    this.criterion = criterion;
    this.model = model;
  }

  /**
   * Returns the search criterion that cannot be translated to a query string.
   * 
   * @return the search criterion that cannot be translated to a query string
   */
  public SearchCriterion getCriterion() {
    return criterion;
  }

  /**
   * Returns the bean model that cannot be translated to a query string.
   * 
   * @return the bean model that cannot be translated to a query string
   */
  public Bean getModel() {
    return model;
  }

}
