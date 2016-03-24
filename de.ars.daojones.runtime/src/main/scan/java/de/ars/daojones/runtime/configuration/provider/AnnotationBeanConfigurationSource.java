package de.ars.daojones.runtime.configuration.provider;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.internal.runtime.utilities.ClasspathHelper;
import de.ars.daojones.internal.runtime.utilities.ClasspathUrlFinder;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.BeanConfiguration;
import de.ars.daojones.runtime.configuration.beans.GlobalConverter;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelImpl;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModelImpl;

/**
 * A beans configuration source that scans the classpath for annotated types.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class AnnotationBeanConfigurationSource extends ApplicationConfigurationSourceAdapter<BeanConfiguration> {

  private static final String ANNOTATION_SCANNING_IMPL_CLASS = "de.ars.daojones.internal.runtime.configuration.provider.AnnotationBeanConfigurationProviderForReflections";
  /**
   * The default path to the bean configuration file. Annotations get scanned
   * for all archives that contain such a file.
   */
  public static final String DEFAULT_CONFIG_FILE = "META-INF/daojones-beans.xml";
  /**
   * The bean configuration file that provides the bean model, that is given by
   * annotations, as an alternate XML file. If this file exists within an
   * archive, annotation scanning is replaced by reading this configuration.
   */
  public static final String ALTERNATE_CONFIG_FILE = "META-INF/daojones-annotated-beans.xml";

  private static final ClassLoader DEFAULT_CLASS_LOADER = Thread.currentThread().getContextClassLoader();

  private final URL[] resourceBases;
  private final ClassLoader dependenciesClassLoader;
  private boolean useAlternativeConfiguration = true;

  /**
   * Creates a default instance that scans the whole classpath for directories
   * and archives that contain a
   * <tt>{@value AnnotationBeanConfigurationSource#DEFAULT_CONFIG_FILE}</tt>
   * file.
   * 
   * @param application
   *          the application (if <tt>null</tt>, use #)
   */
  public AnnotationBeanConfigurationSource( final String application ) {
    this( application, AnnotationBeanConfigurationSource.DEFAULT_CONFIG_FILE );
  }

  /**
   * Creates an instance with a given set of resource bases and a class loader
   * that resolves all dependencies.
   * 
   * @param application
   *          the application
   * @param dependenciesClassLoader
   *          the classLoader that resolves all dependencies
   * @param resourceBases
   *          the resource base directories that should be scanned
   */
  public AnnotationBeanConfigurationSource( final String application, final ClassLoader dependenciesClassLoader,
          final URL... resourceBases ) {
    super( application );
    this.dependenciesClassLoader = dependenciesClassLoader;
    this.resourceBases = resourceBases;
  }

  protected AnnotationBeanConfigurationSource( final String application,
          final ApplicationConfigurationSourceAdapter<BeanConfiguration> cache,
          final ClassLoader dependenciesClassLoader, final URL... resourceBases ) {
    super( application, cache );
    this.dependenciesClassLoader = dependenciesClassLoader;
    this.resourceBases = resourceBases;
  }

  /**
   * Creates an instance with a given set of resource bases. Dependencies are
   * resolved by the context classloader.
   * 
   * @param application
   *          the application
   * @param resourceBases
   *          the resource base directories that should be scanned
   */
  public AnnotationBeanConfigurationSource( final String application, final URL... resourceBases ) {
    this( application, AnnotationBeanConfigurationSource.DEFAULT_CLASS_LOADER, resourceBases );
  }

  /**
   * Creates an instance with a given set of allowed bean configuration files.
   * The classpath is scanned for all modules that contain such a config file.
   * Modules that do not contain at least one of the config files are excluded
   * from annotation scanning.
   * 
   * @param application
   *          the application
   * @param dependenciesClassLoader
   *          the classLoader that resolves all dependencies
   * @param scanningClassLoader
   *          the classloader whose classpath should be scanned
   * @param beanConfigFiles
   *          the bean configuration file names within the module
   */
  public AnnotationBeanConfigurationSource( final String application, final ClassLoader dependenciesClassLoader,
          final ClassLoader scanningClassLoader, final String... beanConfigFiles ) {
    this( application, dependenciesClassLoader, ClasspathHelper.findURLsToScan( beanConfigFiles, scanningClassLoader ) );
  }

  /**
   * Creates an instance with a given set of allowed bean configuration files.
   * The classpath is scanned for all modules that contain such a config file.
   * Modules that do not contain at least one of the config files are excluded
   * from annotation scanning.
   * 
   * @param application
   *          the application
   * @param beanConfigFiles
   *          the bean configuration file names within the module
   */
  public AnnotationBeanConfigurationSource( final String application, final String... beanConfigFiles ) {
    this( application, AnnotationBeanConfigurationSource.DEFAULT_CLASS_LOADER,
            AnnotationBeanConfigurationSource.DEFAULT_CLASS_LOADER, beanConfigFiles );
  }

  /**
   * Returns <tt>true</tt>, if an alternative model file should be used,
   * otherwise <tt>false</tt>. (Default is <tt>true</tt>.)<br/>
   * The path to the alternate model file is {@value #ALTERNATE_CONFIG_FILE}
   * 
   * @return <tt>true</tt>, if an alternative model file should be used,
   *         otherwise <tt>false</tt>
   */
  public boolean isUseAlternativeConfiguration() {
    return useAlternativeConfiguration;
  }

  /**
   * Sets the flag to search for an alternative model file. If <tt>false</tt>,
   * an existing model file would be ignored and the bytecode is scanned anyway.<br/>
   * The path to the alternate model file is {@value #ALTERNATE_CONFIG_FILE}
   * 
   * @param useAlternativeConfiguration
   *          the flag to search for an alternative model file
   */
  public void setUseAlternativeConfiguration( final boolean useAlternativeConfiguration ) {
    this.useAlternativeConfiguration = useAlternativeConfiguration;
  }

  private static URL[] toArray( final Enumeration<URL> urls ) {
    final List<URL> result = new LinkedList<URL>();
    while ( urls.hasMoreElements() ) {
      result.add( urls.nextElement() );
    }
    return result.toArray( new URL[result.size()] );
  }

  /**
   * Returns the bean configuration.
   * 
   * @return the bean configuration
   * @throws ConfigurationException
   * @throws IOException
   */
  public BeanConfiguration getConfiguration() throws ConfigurationException, IOException {
    return getCache( true );
  }

  @Override
  protected BeanConfiguration readCache() throws ConfigurationException {
    try {
      return readConfiguration();
    } catch ( final IOException e ) {
      throw new ConfigurationException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  private BeanConfiguration readConfiguration() throws ConfigurationException, IOException {
    final BeanConfiguration result = new BeanConfiguration();
    // 1. split resource bases by an existence of #ALTERNATIVE_CONFIG_FILE
    final URL[] alternativeResources;
    if ( isUseAlternativeConfiguration() ) {
      final URLClassLoader cl = new URLClassLoader( resourceBases );
      try {
        try {
          alternativeResources = AnnotationBeanConfigurationSource.toArray( cl
                  .getResources( AnnotationBeanConfigurationSource.ALTERNATE_CONFIG_FILE ) );
        } catch ( final IOException e ) {
          throw new ConfigurationException( e );
        }
      } finally {
        // TODO Java 7: invoke close()
        cl.clearAssertionStatus();
      }
    } else {
      alternativeResources = new URL[0];
    }
    // Load alternative URLs from XML file
    final Set<String> doubledEntries = new HashSet<String>();
    for ( final URL url : alternativeResources ) {
      if ( doubledEntries.add( url.toExternalForm() ) ) {
        final XmlBeanConfigurationSource xbcp = new XmlBeanConfigurationSource( getApplication(), url );
        final BeanConfiguration alternativeConfig = xbcp.readRootElement();
        result.getConverters().addAll( alternativeConfig.getConverters() );
        result.getBeans().addAll( alternativeConfig.getBeans() );
      }
    }
    // Load by annotation scanning - only those without alternative XML file
    final URL[] nonAlternatives = findNonAlternatives( alternativeResources );
    if ( nonAlternatives.length > 0 ) {
      // BE AWARE: Load by reflection !!!
      try {
        final ConfigurationProvider<BeanConfiguration> annotationScanner = ( ConfigurationProvider<BeanConfiguration> ) Class
                .forName( AnnotationBeanConfigurationSource.ANNOTATION_SCANNING_IMPL_CLASS )
                .getConstructor( ClassLoader.class, URL[].class )
                .newInstance( dependenciesClassLoader, nonAlternatives );
        for ( final BeanConfiguration bc : annotationScanner.readConfiguration() ) {
          result.getConverters().addAll( bc.getConverters() );
          result.getBeans().addAll( bc.getBeans() );
        }
      } catch ( final Exception e ) {
        throw new ConfigurationException( e );
      }
    }
    return result;
  }

  private URL[] findNonAlternatives( final URL[] alternativeResources ) {
    final URL[] alternativeURLs = ClasspathUrlFinder.toResourceBases(
            AnnotationBeanConfigurationSource.ALTERNATE_CONFIG_FILE, alternativeResources );
    // Comparing URLs by their equals and hashcode method is not performant -> use external form
    final Map<String, URL> allURLs = new HashMap<String, URL>();
    for ( final URL resourceBase : resourceBases ) {
      allURLs.put( resourceBase.toExternalForm(), resourceBase );
    }
    for ( final URL alternativeURL : alternativeURLs ) {
      allURLs.remove( alternativeURL.toExternalForm() );
    }
    final URL[] result = new URL[allURLs.size()];
    int i = 0;
    for ( final Map.Entry<String, URL> entry : allURLs.entrySet() ) {
      result[i] = entry.getValue();
      i++;
    }
    return result;
  }

  @Override
  public ConfigurationProvider<BeanModel> getBeanModelConfigurationProvider() throws ConfigurationException {
    return new ConfigurationProvider<BeanModel>() {

      @Override
      public Iterable<BeanModel> readConfiguration() throws ConfigurationException {
        try {
          final Collection<BeanModel> result = new HashSet<BeanModel>();
          for ( final Bean bean : AnnotationBeanConfigurationSource.this.getConfiguration().getBeans() ) {
            result.add( new BeanModelImpl( AnnotationBeanConfigurationSource.this.getApplication(), bean ) );
          }
          return result;
        } catch ( final IOException e ) {
          throw new ConfigurationException();
        }
      }
    };
  }

  @Override
  public ConfigurationProvider<GlobalConverterModel> getGlobalConverterModelConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<GlobalConverterModel>() {

      @Override
      public Iterable<GlobalConverterModel> readConfiguration() throws ConfigurationException {
        try {
          final Collection<GlobalConverterModel> result = new HashSet<GlobalConverterModel>();
          for ( final GlobalConverter converter : AnnotationBeanConfigurationSource.this.getConfiguration()
                  .getConverters() ) {
            result.add( new GlobalConverterModelImpl( AnnotationBeanConfigurationSource.this.getApplication(),
                    converter ) );
          }
          return result;
        } catch ( final IOException e ) {
          throw new ConfigurationException();
        }
      }
    };
  }

  @Override
  public ConfigurationSource adaptTo( final String application ) throws ConfigurationException {
    final AnnotationBeanConfigurationSource result = new AnnotationBeanConfigurationSource( application, this,
            dependenciesClassLoader, resourceBases );
    result.setUseAlternativeConfiguration( isUseAlternativeConfiguration() );
    return result;
  }

}
