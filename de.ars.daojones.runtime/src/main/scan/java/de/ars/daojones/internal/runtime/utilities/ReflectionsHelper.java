package de.ars.daojones.internal.runtime.utilities;

import java.net.URL;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

public abstract class ReflectionsHelper {

  private ReflectionsHelper() {
    super();
  }

  public static Reflections createReflections( final String[] beanConfigFiles, final ClassLoader scanningClassloader,
          final ClassLoader dependenciesClassLoader, final Scanner... scanners ) {
    return ReflectionsHelper.createReflections( ClasspathHelper.findURLsToScan( beanConfigFiles, scanningClassloader ),
            dependenciesClassLoader, scanners );
  }

  public static Reflections createReflections( final URL[] urlsToScan, final ClassLoader dependenciesClassLoader,
          final Scanner... scanners ) {
    // Do not use ThreadPool because it is never shutdown().
    /*final ExecutorService pool = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );*/
    final ConfigurationBuilder config = new ConfigurationBuilder().setUrls( urlsToScan ).setScanners( scanners )
    /*.setExecutorService( pool )*/.addClassLoader( dependenciesClassLoader );
    return new Reflections( config );
  }

  /**
   * Finds all sub types of a type.<br/>
   * <i><b>Please notes:</b> This method may last some time, so only invoke it
   * when necessary.</i>
   * 
   * @param beanConfigFiles
   *          the names of the bean config files
   * @param scanningClassloader
   *          the classloader whose classpath is scanned
   * @param dependenciesClassLoader
   *          the classloader to tload the dependencies
   * @param type
   *          the type's class object
   * @param <T>
   *          the type
   * @return the sub types
   */
  public static <T> Set<Class<? extends T>> getSubtypes( final String[] beanConfigFiles,
          final ClassLoader scanningClassloader, final ClassLoader dependenciesClassLoader, final Class<T> type ) {
    return ReflectionsHelper.createReflections( beanConfigFiles, scanningClassloader, dependenciesClassLoader,
            new SubTypesScanner() ).getSubTypesOf( type );
  }

}
