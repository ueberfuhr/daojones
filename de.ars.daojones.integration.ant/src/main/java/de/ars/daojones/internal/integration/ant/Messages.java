package de.ars.daojones.internal.integration.ant;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The messages from the resource bundle. The resource bundle is specified by a
 * bundle name. The logger name is the bundle name including
 * <tt>{@value Messages#LOGGER_NAMESPACE}</tt> as a prefix.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public class Messages {

  /**
   * The namespace for the logger names.
   */
  public static final String LOGGER_NAMESPACE = "de.ars.daojones.integration.ant.";
  /**
   * The namespace for the bundle names.
   */
  public static final String BUNDLE_NAMESPACE = "de.ars.daojones.internal.integration.ant.";

  private final String bundle;
  private final ClassLoader classLoader;

  private Messages( final String bundle, final ClassLoader classLoader ) {
    super();
    this.bundle = bundle;
    this.classLoader = classLoader;
  }

  /**
   * Creates a message bundle.
   * 
   * @param bundle
   *          the bundle name
   * @param classLoader
   *          the class loader to load the bundle
   * @return the message bundle
   */
  public static Messages create( final String bundle, final ClassLoader classLoader ) {
    return new Messages( bundle, classLoader );
  }

  /**
   * Creates a message bundle.
   * 
   * @param bundle
   *          the bundle name
   * @return the message bundle
   */
  public static Messages create( final String bundle ) {
    return new Messages( bundle, Messages.class.getClassLoader() );
  }

  /**
   * Binds a message from the resource bundle to a set of specific parameters.
   * 
   * @param key
   *          the message key
   * @param params
   *          the parameters for the message
   * @return the message with the wildcards replaced by the parameters
   */
  public String get( final String key, final Object... params ) {
    return get( Locale.getDefault(), key, params );
  }

  /**
   * Binds a message from the resource bundle to a set of specific parameters.
   * 
   * @param locale
   *          the {@link Locale}
   * @param key
   *          the message key
   * @param params
   *          the parameters for the message
   * @return the message with the wildcards replaced by the parameters
   */
  public String get( final Locale locale, final String key, final Object... params ) {
    try {
      final ResourceBundle bundle = ResourceBundle.getBundle( Messages.BUNDLE_NAMESPACE + this.bundle, locale,
              classLoader );
      return MessageFormat.format( bundle.getString( key ), params );
    } catch ( final MissingResourceException e ) {
      final StringBuffer sb = new StringBuffer( "%" );
      sb.append( key );
      if ( null != params && params.length > 0 ) {
        for ( final Object param : params ) {
          sb.append( " [" );
          sb.append( param );
          sb.append( ']' );
        }
      }
      return sb.toString();
    }
  }

  /**
   * Logs a message.
   * 
   * @param level
   *          the log level
   * @param key
   *          the message key
   * @param params
   *          the parameters for the message
   */
  public void log( final Level level, final String key, final Object... params ) {
    log( level, null, key, params );
  }

  /**
   * Logs an error message.
   * 
   * @param level
   *          the log level
   * @param t
   *          the exception
   * @param key
   *          the message key
   * @param params
   *          the parameters for the message
   */
  public void log( final Level level, final Throwable t, final String key, final Object... params ) {
    getLogger().log( level, get( key, params ), t );
  }

  /**
   * Returns a flag indicating whether the given level is logged or not.
   * 
   * @param level
   *          the level
   * @return <tt>true</tt>, if the level is logged
   */
  public boolean isLoggable( final Level level ) {
    return getLogger().isLoggable( level );
  }

  private Logger getLogger() {
    return Logger.getLogger( Messages.LOGGER_NAMESPACE + bundle );
  }

  /**
   * An expander handler transforms a single entry to a string during expanding.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <T>
   *          the element type
   */
  public static interface ExpanderHandler<T> {

    /**
     * Transforms a single entry to a string.
     * 
     * @param t
     *          the entry
     * @return the string
     */
    String handle( T t );

  }

  /**
   * Expands a collection or anything else that is iterable to a string.
   * 
   * @param <T>
   *          the type of elements within the collection
   * @param iterable
   *          the collection or anything else that is iterable
   * @param separator
   *          the separator between the elements
   * @param handler
   *          the handler that transforms a single entry to a string
   * @return the string
   */
  public static <T> String expand( final Iterable<T> iterable, final String separator, final ExpanderHandler<T> handler ) {
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    for ( final T t : iterable ) {
      if ( !first ) {
        sb.append( separator );
      }
      sb.append( handler.handle( t ) );
      first = false;
    }
    return sb.toString();
  }

  /**
   * Expands a collection or anything else that is iterable to a string. During
   * expansion, the elements within the collection are transformed using their
   * {@link Object#toString()} method.
   * 
   * @param <T>
   *          the type of elements within the collection
   * @param iterable
   *          the collection or anything else that is iterable
   * @param separator
   *          the separator between the elements
   * @return the string
   */
  public static <T> String expand( final Iterable<T> iterable, final String separator ) {
    return Messages.expand( iterable, separator, new ExpanderHandler<T>() {

      @Override
      public String handle( final T t ) {
        return null != t ? t.toString() : null;
      }
    } );
  }

}
