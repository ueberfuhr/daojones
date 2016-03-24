package de.ars.daojones.internal.runtime.utilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

public final class ClasspathUrlFinder {

  private static final String JARFILE_SUFFIX = "!/";

  private ClasspathUrlFinder() {
    super();
  }

  public static URL[] toResourceBases( final String baseResource, final Enumeration<URL> urls ) {
    final ArrayList<URL> list = new ArrayList<URL>();
    while ( urls.hasMoreElements() ) {
      final URL url = urls.nextElement();
      list.add( ClasspathUrlFinder.findResourceBase( url, baseResource ) );
    }
    return list.toArray( new URL[list.size()] );
  }

  public static URL[] toResourceBases( final String baseResource, final URL[] urls ) {
    final URL[] result = new URL[urls.length];
    for ( int i = 0; i < urls.length; i++ ) {
      result[i] = ClasspathUrlFinder.findResourceBase( urls[i], baseResource );
    }
    return result;
  }

  /**
   * Find the classpath URLs for a specific classpath resource. The classpath
   * URL is extracted from loader.getResources() using the baseResource.
   * 
   * @param baseResource
   * @return
   */
  public static URL[] findResourceBases( final String baseResource, final ClassLoader classLoader ) {
    try {
      final Enumeration<URL> urls = classLoader.getResources( baseResource );
      return ClasspathUrlFinder.toResourceBases( baseResource, urls );
    } catch ( final IOException e ) {
      throw new RuntimeException( e );
    }
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
    try {
      File baseResourceFile = new File( baseResource );
      URL result;
      try {
        // WebSphere Classloader returns URLs that contain space chars
        result = new URL( url.toExternalForm().replaceAll( " ", "%20" ) );
      } catch ( final Exception e ) {
        // Fallback
        result = url;
      }
      boolean first = !result.toExternalForm().endsWith( "/" );
      while ( null != baseResourceFile ) {
        result = new URL( result, first ? "./" : "../" );
        // wsjar/reflections fix: URLs to JARs ending with !/ are not handled correctly -> change to file:url
        final String externalForm = result.toExternalForm();
        if ( externalForm.endsWith( ClasspathUrlFinder.JARFILE_SUFFIX ) ) {
          final String part = result.toURI().getSchemeSpecificPart();
          result = new URL( part.substring( 0, part.length() - ClasspathUrlFinder.JARFILE_SUFFIX.length() ) );
        }
        baseResourceFile = baseResourceFile.getParentFile();
        first = false;
      }
      return result;
    } catch ( final MalformedURLException e ) {
      throw new RuntimeException( e );
    } catch ( final URISyntaxException e ) {
      throw new RuntimeException( e );
    }
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
