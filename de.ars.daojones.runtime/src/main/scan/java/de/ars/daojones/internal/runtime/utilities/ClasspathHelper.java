package de.ars.daojones.internal.runtime.utilities;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ClasspathHelper {

  private ClasspathHelper() {
    super();
  }

  /**
   * Finds URLs that are scanned for annotations.
   * 
   * @param beanConfigFiles
   *          the names of the bean config files
   * @param classloader
   *          the class loader
   * @return the URLs to scan
   */
  public static URL[] findURLsToScan( final String[] beanConfigFiles, final ClassLoader classLoader ) {
    final Map<String, URL> result = new HashMap<String, URL>();
    for ( final String bcf : beanConfigFiles ) {
      final URL[] resourceBases = ClasspathUrlFinder.findResourceBases( bcf, classLoader );
      for ( final URL resourceBase : resourceBases ) {
        result.put( resourceBase.toExternalForm(), resourceBase );
      }
    }
    final Collection<URL> values = result.values();
    return values.toArray( new URL[values.size()] );
  }

}
