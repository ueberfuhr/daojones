package de.ars.daojones.internal.runtime.test;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.test.spi.database.Aware;

public class AwareRegistry<K, T extends Aware<K>> {

  private static final Messages bundle = Messages.create( "aware.registry" );

  private static final Map<Class<?>, Class<?>> primitiveToReference = new HashMap<Class<?>, Class<?>>();

  static {
    AwareRegistry.primitiveToReference.put( Integer.TYPE, Integer.class );
    AwareRegistry.primitiveToReference.put( Long.TYPE, Long.class );
    AwareRegistry.primitiveToReference.put( Short.TYPE, Short.class );
    AwareRegistry.primitiveToReference.put( Byte.TYPE, Byte.class );
    AwareRegistry.primitiveToReference.put( Double.TYPE, Double.class );
    AwareRegistry.primitiveToReference.put( Float.TYPE, Float.class );
    AwareRegistry.primitiveToReference.put( Boolean.TYPE, Boolean.class );
    AwareRegistry.primitiveToReference.put( Character.TYPE, Character.class );
    // Arrays
    AwareRegistry.primitiveToReference.put( int[].class, Integer[].class );
    AwareRegistry.primitiveToReference.put( long[].class, Long[].class );
    AwareRegistry.primitiveToReference.put( short[].class, Short[].class );
    AwareRegistry.primitiveToReference.put( byte[].class, Byte[].class );
    AwareRegistry.primitiveToReference.put( double[].class, Double[].class );
    AwareRegistry.primitiveToReference.put( float[].class, Float[].class );
    AwareRegistry.primitiveToReference.put( boolean[].class, Boolean[].class );
    AwareRegistry.primitiveToReference.put( char[].class, Character[].class );
  }

  private final Map<Class<? extends K>, T> map = new HashMap<Class<? extends K>, T>();

  private final Class<? extends T> awareType;
  private final boolean replacePrimitiveTypes;

  public AwareRegistry( final Class<? extends T> awareType ) {
    this( awareType, true );
  }

  public AwareRegistry( final Class<? extends T> awareType, final boolean replacePrimitiveTypes ) {
    super();
    this.awareType = awareType;
    this.replacePrimitiveTypes = replacePrimitiveTypes;
    // Initialize
    final ServiceLoader<? extends T> sl = ServiceLoader.load( awareType );
    for ( final T aware : sl ) {
      final Class<? extends K> keyType = aware.getKeyType();
      if ( isAware( keyType, aware ) ) {
        map.put( keyType, aware );
      }
    }
  }

  protected boolean isAware( final Class<? extends K> keyType, final T aware ) {
    return true;
  }

  @SuppressWarnings( "unchecked" )
  public T findAware( final Class<? extends K> keyType ) {
    final Class<? extends K> referencedKeyType = ( Class<? extends K> ) ( replacePrimitiveTypes
            && AwareRegistry.primitiveToReference.containsKey( keyType ) ? AwareRegistry.primitiveToReference
            .get( keyType ) : keyType );
    synchronized ( map ) {
      T result = null;
      if ( map.containsKey( keyType ) ) {
        result = map.get( keyType );
      } else {
        Map.Entry<Class<? extends K>, T> candidate = null;
        for ( final Map.Entry<Class<? extends K>, T> entry : map.entrySet() ) {
          if ( entry.getKey().isAssignableFrom( referencedKeyType )
                  && ( null == candidate || candidate.getKey().isAssignableFrom( entry.getKey() ) ) ) {
            candidate = entry;
          }
        }
        // return value
        if ( null != candidate ) {
          result = candidate.getValue();
        }
        // cache this query directly within the map
        map.put( keyType, result );
      }
      return result;
    }
  }

  public T findAwareNotNull( final Class<? extends K> keyType ) throws AwareNotFoundException {
    final T result = findAware( keyType );
    if ( null != result ) {
      return result;
    } else {
      // I18n
      final String key = "error.aware.missing";
      final Object p0 = awareType.getName();
      final Object p1 = keyType.getName();
      final String message = AwareRegistry.bundle.get( key, p0, p1 );
      throw new AwareNotFoundException( message );
    }
  }

}
