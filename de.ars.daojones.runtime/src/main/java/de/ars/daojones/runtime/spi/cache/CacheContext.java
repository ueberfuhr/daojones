package de.ars.daojones.runtime.spi.cache;

import java.util.Properties;

/**
 * A transfer object that encapsulates all information that is available for
 * creating a cache instance.
 * 
 * @param <K>
 *          the key type
 * @param <V>
 *          the value type
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.2.0
 */
public interface CacheContext<K, V> {

  /**
   * Returns the application id.
   * 
   * @return the application id
   */
  String getApplication();

  /**
   * Returns the connection id.
   * 
   * @return the connection id
   */
  String getConnectionId();

  /**
   * Returns the custom properties. This will return a copy, so changes could be
   * made and do not affect the original properties source.
   * 
   * @return the custom properties
   */
  Properties getProperties();

  /**
   * Returns the key type.
   * 
   * @return the key type
   */
  Class<K> getKeyType();

  /**
   * Returns the value type.
   * 
   * @return the value type
   */
  Class<V> getValueType();

}