package de.ars.daojones.internal.integration.web;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

final class ClasspathUrlFinder {

  private ClasspathUrlFinder() {
    super();
  }

  /**
   * Find the classpath URLs for a specific classpath resource. The classpath
   * URL is extracted from loader.getResources() using the baseResource.
   * 
   * @param baseResource
   * @return
   */
  public static URL[] findResourceBases( final String baseResource, final ClassLoader loader ) {
    final ArrayList<URL> list = new ArrayList<URL>();
    try {
      final Enumeration<URL> urls = loader.getResources( baseResource );
      while ( urls.hasMoreElements() ) {
        final URL url = urls.nextElement();
        list.add( ClasspathUrlFinder.findResourceBase( url, baseResource ) );
      }
    } catch ( final IOException e ) {
      throw new RuntimeException( e );
    }
    return list.toArray( new URL[list.size()] );
  }

  /**
   * Find the classpath URLs for a specific classpath resource. The classpath
   * URL is extracted from loader.getResources() using the baseResource.
   * 
   * @param baseResource
   * @return
   */
  public static URL[] findResourceBases( final String baseResource ) {
    return ClasspathUrlFinder.findResourceBases( baseResource, Thread.currentThread().getContextClassLoader() );
  }

  private static URL findResourceBase( final URL url, final String baseResource ) {
    String urlString = url.toString();
    final int idx = urlString.lastIndexOf( baseResource );
    urlString = urlString.substring( 0, idx );
    URL deployUrl = null;
    try {
      deployUrl = new URL( urlString );
    } catch ( final MalformedURLException e ) {
      throw new RuntimeException( e );
    }
    return deployUrl;
  }

  /**
   * Find the classpath URL for a specific classpath resource. The classpath URL
   * is extracted from
   * Thread.currentThread().getContextClassLoader().getResource() using the
   * baseResource.
   * 
   * @param baseResource
   * @return
   */
  public static URL findResourceBase( final String baseResource ) {
    return ClasspathUrlFinder.findResourceBase( baseResource, Thread.currentThread().getContextClassLoader() );
  }

  /**
   * Find the classpath URL for a specific classpath resource. The classpath URL
   * is extracted from loader.getResource() using the baseResource.
   * 
   * @param baseResource
   * @param loader
   * @return
   */
  public static URL findResourceBase( final String baseResource, final ClassLoader loader ) {
    final URL url = loader.getResource( baseResource );
    return ClasspathUrlFinder.findResourceBase( url, baseResource );
  }

  /**
   * Find the classpath for the particular class
   * 
   * @param clazz
   * @return
   */
  public static URL findClassBase( final Class<?> clazz ) {
    final String resource = clazz.getName().replace( '.', '/' ) + ".class";
    return ClasspathUrlFinder.findResourceBase( resource, clazz.getClassLoader() );
  }

  /**
   * Uses the java.class.path system property to obtain a list of URLs that
   * represent the CLASSPATH
   * 
   * @return
   */
  public static URL[] findClassPaths() {
    final List<URL> list = new ArrayList<URL>();
    final String classpath = System.getProperty( "java.class.path" );
    final StringTokenizer tokenizer = new StringTokenizer( classpath, File.pathSeparator );

    while ( tokenizer.hasMoreTokens() ) {
      final String path = tokenizer.nextToken();
      final File fp = new File( path );
      if ( !fp.exists() ) {
        throw new RuntimeException( "File in java.class.path does not exist: " + fp );
      }
      try {
        list.add( fp.toURI().toURL() );
      } catch ( final MalformedURLException e ) {
        throw new RuntimeException( e );
      }
    }
    return list.toArray( new URL[list.size()] );
  }

  /**
   * Uses the java.class.path system property to obtain a list of URLs that
   * represent the CLASSPATH
   * <p/>
   * paths is used as a filter to only include paths that have the specific
   * relative file within it
   * 
   * @param paths
   *          comma list of files that should exist in a particular path
   * @return
   */
  public static URL[] findClassPaths( final String... paths ) {
    final ArrayList<URL> list = new ArrayList<URL>();

    final String classpath = System.getProperty( "java.class.path" );
    final StringTokenizer tokenizer = new StringTokenizer( classpath, File.pathSeparator );
    for ( int i = 0; i < paths.length; i++ ) {
      paths[i] = paths[i].trim();
    }

    while ( tokenizer.hasMoreTokens() ) {
      final String path = tokenizer.nextToken().trim();
      boolean found = false;
      for ( final String wantedPath : paths ) {
        if ( path.endsWith( File.separator + wantedPath ) ) {
          found = true;
          break;
        }
      }
      if ( !found ) {
        continue;
      }
      final File fp = new File( path );
      if ( !fp.exists() ) {
        throw new RuntimeException( "File in java.class.path does not exists: " + fp );
      }
      try {
        list.add( fp.toURI().toURL() );
      } catch ( final MalformedURLException e ) {
        throw new RuntimeException( e );
      }
    }
    return list.toArray( new URL[list.size()] );
  }
}
