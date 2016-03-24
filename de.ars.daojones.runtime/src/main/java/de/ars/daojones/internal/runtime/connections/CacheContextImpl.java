package de.ars.daojones.internal.runtime.connections;

import java.util.Properties;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.spi.cache.CacheContext;

/**
 * Default implementation of {@link CacheContext}.
 * 
 * @param <K>
 *          the key type
 * @param <V>
 *          the value type
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.2.0
 */
public class CacheContextImpl<K, V> implements CacheContext<K, V> {

  private final String application;
  private final String connectionId;
  private final Properties properties;
  private final Class<K> keyType;
  private final Class<V> valueType;

  /**
   * Creates an instance.
   * 
   * @param connection
   *          the connection id
   * @param properties
   *          the properties
   * @param keyType
   *          the key type
   * @param valueType
   *          the value type
   */
  public CacheContextImpl( final ConnectionModel.Id connection, final Properties properties, final Class<K> keyType,
          final Class<V> valueType ) {
    this( connection.getApplicationId(), connection.getConnectionId(), properties, keyType, valueType );
  }

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application id
   * @param connectionId
   *          the connection id
   * @param properties
   *          the properties
   * @param keyType
   *          the key type
   * @param valueType
   *          the value type
   */
  public CacheContextImpl( final String application, final String connectionId, final Properties properties,
          final Class<K> keyType, final Class<V> valueType ) {
    super();
    this.application = application;
    this.connectionId = connectionId;
    this.properties = properties;
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public String getApplication() {
    return application;
  }

  @Override
  public String getConnectionId() {
    return connectionId;
  }

  @Override
  public Class<K> getKeyType() {
    return keyType;
  }

  @Override
  public Class<V> getValueType() {
    return valueType;
  }

  @Override
  public Properties getProperties() {
    return null != properties ? new Properties( properties ) : new Properties();
  }

}