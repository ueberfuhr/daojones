package de.ars.daojones.internal.runtime.test.junit;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.internal.runtime.test.DefaultTestConnectionFactoryModel;
import de.ars.daojones.internal.runtime.test.Constants;
import de.ars.daojones.internal.runtime.test.junit.Injector.InjectionContext;
import de.ars.daojones.internal.runtime.test.junit.TestContext.TestSetup;
import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelImpl;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationProvider;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSourceAdapter;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;
import de.ars.daojones.runtime.configuration.provider.XmlBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;
import de.ars.daojones.runtime.context.InjectionEngine;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;
import de.ars.daojones.runtime.test.TestConnectionFactory;
import de.ars.daojones.runtime.test.TestConnectionFactoryModel;
import de.ars.daojones.runtime.test.TestModelResolver;
import de.ars.daojones.runtime.test.XmlResourceTestModelResolver;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Model;
import de.ars.daojones.runtime.test.junit.Config;
import de.ars.daojones.runtime.test.junit.ConfigAnnotations;
import de.ars.daojones.runtime.test.junit.ConfigDriver;
import de.ars.daojones.runtime.test.junit.ConfigType;
import de.ars.daojones.runtime.test.junit.Configs;
import de.ars.daojones.runtime.test.junit.TestModel;
import de.ars.daojones.runtime.test.spi.database.CompoundTestModelResolver;

/**
 * A common helper class used by both the runner and the rule.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DaoJonesContextBuilder implements Closeable, TestModelResolver {

  private static final Messages bundle = Messages.create( "junit.DaoJonesContextBuilder" );

  private final Map<String, Set<ConfigType>> configuredTypes = new HashMap<String, Set<ConfigType>>();
  private final boolean threaded;
  private final Class<?> testClass;
  private Object test;
  private final DaoJonesContext context;
  private final DaoJonesContextConfigurator configurator;

  public DaoJonesContextBuilder( final Class<?> testClass, final AnnotatedElement testMethod )
          throws ConfigurationException, IOException {
    super();
    this.testClass = testClass;
    threaded = null != testMethod;
    context = createDaoJonesContext();
    configurator = new DaoJonesContextConfigurator( context );
    // read annotations from class
    configure( testClass );
    // read annotations from test method
    if ( null != testMethod ) {
      configure( testMethod );
    }
    // default configurations
    finishConfiguration();
  }

  /**
   * Returns the test instance.
   * 
   * @return the test instance
   */
  public Object getTest() {
    return test;
  }

  /**
   * Sets the test instance.
   * 
   * @param test
   *          the test instance
   */
  public void setTest( final Object test ) {
    this.test = test;
  }

  /**
   * Creates the DaoJones context.
   * 
   * @return the DaoJones context
   * @throws ConfigurationException
   */
  protected DaoJonesContext createDaoJonesContext() throws ConfigurationException {
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    return dcf.createContext();
  }

  public DaoJonesContext getContext() {
    return context;
  }

  public Class<?> getTestClass() {
    return testClass;
  }

  /* *************************************
   *   T E S T   M O D E L   F I L E S   *
   ************************************* */

  public static final String DEFAULT_DB_PATH = "{1}-model{0}.xml";

  private static final MessageFormat MODEL_FILE_APP = new MessageFormat( "-{0}" ); // NON-NLS-$1

  public static String getModelFile( final Class<?> testClass ) {
    return DaoJonesContextBuilder.getModelFile( testClass, Constants.DEFAULT_APPLICATION );
  }

  public static String getModelFile( final Class<?> testClass, final String application ) {
    final String db = DaoJonesContextBuilder.DEFAULT_DB_PATH;
    return DaoJonesContextBuilder.getModelFile( testClass, application, db );
  }

  private static String getModelFile( final Class<?> testClass, final String application, final String db ) {
    final boolean isDefaultApp = Constants.DEFAULT_APPLICATION.equals( application );
    final String p0 = isDefaultApp ? "" : DaoJonesContextBuilder.MODEL_FILE_APP.format( new Object[] { application } );
    final String p1 = testClass.getSimpleName();
    return MessageFormat.format( db, p0, p1 );
  }

  /* *******************************
   *   C O N F I G U R A T I O N   *
   ******************************* */

  private void setConfigured( final String application, final ConfigType type ) {
    Set<ConfigType> set = configuredTypes.get( application );
    if ( null == set ) {
      set = new HashSet<ConfigType>();
      configuredTypes.put( application, set );
    }
    set.add( type );
  }

  /**
   * Configures the DaoJones environment with the given element's config data.
   * 
   * @param element
   *          the annotated element
   * @throws IOException
   * @throws ConfigurationException
   */
  protected void configure( final AnnotatedElement element ) throws IOException, ConfigurationException {
    final ConfigAnnotations configAnnotations = element.getAnnotation( ConfigAnnotations.class );
    if ( null != configAnnotations ) {
      configure( configAnnotations );
    }
    final Config config = element.getAnnotation( Config.class );
    if ( null != config ) {
      configure( config );
    }
    final Configs configs = element.getAnnotation( Configs.class );
    if ( null != configs ) {
      for ( final Config c : configs.value() ) {
        configure( c );
      }
    }
  }

  /**
   * Finishes the configuration by configuring default values (if no
   * configuration applied) and by replacing original drivers by test drivers.
   * 
   * @throws ConfigurationException
   */
  protected void finishConfiguration() throws ConfigurationException {
    // add app id for test models
    final Class<?> c = getTestClass();
    for ( final Field field : c.getDeclaredFields() ) {
      final TestModel tm = field.getAnnotation( TestModel.class );
      if ( null != tm ) {
        final String app = tm.application();
        if ( !configuredTypes.containsKey( app ) ) {
          configuredTypes.put( app, new HashSet<ConfigType>() );
        }
      }
    }
    // if there isn't anything configured, configure default application
    if ( configuredTypes.isEmpty() ) {
      configuredTypes.put( Constants.DEFAULT_APPLICATION, new HashSet<ConfigType>() );
    }
    for ( final Map.Entry<String, Set<ConfigType>> entry : configuredTypes.entrySet() ) {
      final String application = entry.getKey();
      final Set<ConfigType> types = entry.getValue();
      // default configurations
      if ( !types.contains( ConfigType.BEANS ) ) {
        configureDefaultBeans( application );
      }
      if ( !types.contains( ConfigType.CONNECTIONS ) ) {
        configureDefaultConnections( application );
      }
    }
    // replace original drivers
    configureTestStage();
  }

  /**
   * Configures the DaoJones context with the given config data.
   * 
   * @param config
   *          the config data
   * @throws IOException
   * @throws ConfigurationException
   */
  protected void configure( final Config config ) throws IOException, ConfigurationException {
    configure( new ConfigData( config.application(), config.value(), config.type() ) );
  }

  protected static class ConfigData {

    private final String application;
    private final String value;
    private final ConfigType type;

    public ConfigData( final String application, final String value, final ConfigType type ) {
      super();
      this.application = application;
      this.value = value;
      this.type = type;
    }

    public String getApplication() {
      return application;
    }

    public String getValue() {
      return value;
    }

    public ConfigType getType() {
      return type;
    }

  }

  protected void configure( final ConfigData config ) throws IOException, ConfigurationException {
    final String application = config.getApplication();
    final String file = config.getValue();
    final ConfigType type = config.getType();
    final URL resource = getTestClass().getResource( file );
    if ( null == resource ) {
      throw new FileNotFoundException( file );
    }
    switch ( type ) {
    case BEANS:
      configurator.configure( new XmlBeanConfigurationSource( application, resource ) );
      break;
    case CONNECTIONS:
      configurator.configure( new XmlConnectionConfigurationSource( application, resource ) );
      break;
    default:
      throw new IllegalArgumentException( type.name() );
    }
    setConfigured( application, type );
  }

  // Performance issue: Avoid classpath scanning multiple times
  private static final AnnotationBeanConfigurationSource annotationScanner = new AnnotationBeanConfigurationSource(
          null );

  protected void configureBeansByAnnotationScanning( final String application ) throws ConfigurationException {
    configurator.configure( DaoJonesContextBuilder.annotationScanner.adaptTo( application ) );
    setConfigured( application, ConfigType.BEANS );
  }

  protected void configure( final ConfigAnnotations config ) throws ConfigurationException {
    configureBeansByAnnotationScanning( config.application() );
  }

  protected void configureDefaultBeans( final String application ) throws ConfigurationException {
    configureBeansByAnnotationScanning( application );
  }

  protected void configureDefaultConnections( final String application ) throws ConfigurationException {
    configurator.configure( new ConfigurationSourceAdapter() {

      @Override
      public ConfigurationProvider<ConnectionModel> getConnectionModelConfigurationProvider()
              throws ConfigurationException {
        return new ConfigurationProvider<ConnectionModel>() {

          @Override
          public Iterable<ConnectionModel> readConfiguration() throws ConfigurationException {
            final Connection con = new Connection();
            con.setId( "default-connection" );
            con.setDefault( true );

            // configure driver
            final ConfigDriver configDriver = getTestClass().getAnnotation( ConfigDriver.class );
            con.setType( null != configDriver ? configDriver.value() : DefaultTestConnectionFactoryModel.ID );

            // prepare TestSetup
            TestContext.setTestSetup( new TestSetup() {

              @Override
              public void configure( final TestConnectionFactory testConnectionFactory ) throws ConfigurationException {
                testConnectionFactory.setResolver( new CompoundTestModelResolver( new XmlResourceTestModelResolver() {

                  @Override
                  protected Model handleNullResource( final ConnectionModel model, final String file )
                          throws IOException {
                    // Only throw an exception if the class does not define any custom Java-based model.
                    if ( DaoJonesContextBuilder.this.definesModel( model ) ) {
                      return new Model();
                    } else {
                      return super.handleNullResource( model, file );
                    }
                  }

                  @Override
                  protected String getContentFile( final ConnectionModel model ) throws IOException {
                    final String db = super.getContentFile( model );
                    final Class<?> c = DaoJonesContextBuilder.this.getTestClass();
                    final String app = model.getId().getApplicationId();
                    final String result = DaoJonesContextBuilder.getModelFile( c, app, db );
                    return result;
                  }

                  @Override
                  protected URL resolveResource( final String file ) throws IOException {
                    final Class<?> c = DaoJonesContextBuilder.this.getTestClass();
                    return c.getResource( file );
                  }

                }, DaoJonesContextBuilder.this ) );

              }
            }, threaded );

            /*
             *  Database Path
             *  =============
             *  - Path should be the package of the test class
             *  - File Name should contain the name of the test class (isolation!)
             *  - File Name should contain the name of the application
             *  - Maybe annotations to configure the model or hashmap containing the model! (dynamic and comparable)
             *  
             *  @TestDataSource("...", type=VIEW)
             *  private final Map<String, Object> ds...
             *  (Referenz erst holen, wenn gebraucht wg. Austauschbarkeit...)
             *  (wenn Obj. direkt vom Typ... sonst toString() und Data Handler)
             *  
             *  Alternativ: @ConfigTestDriverWithModelFile("...") statt connection-config
             *  
             *  Matcher zum Testen: Bean<-->Entry oder Bean<-->Bean (Connection wird benötigt?)
             *  Matcher zum Testen des Models (Datasource, entry etc. entry(dd).hasProperty)
             */
            con.setDatabase( DaoJonesContextBuilder.DEFAULT_DB_PATH );
            return Arrays.asList( ( ConnectionModel ) new ConnectionModelImpl( application, con ) );
          }
        };
      }

    } );
  }

  /**
   * Configures the context to be run within the test stage. This will replace
   * original driver models.
   * 
   * @param context
   * @throws ConfigurationException
   */
  protected void configureTestStage() throws ConfigurationException {
    final ConnectionFactoryModelManager cfmm = getContext().getConfiguration().getConnectionFactoryModelManager();
    final Collection<ConnectionFactoryModel> models = cfmm.getModels();
    for ( final ConnectionFactoryModel model : models ) {
      if ( model instanceof TestConnectionFactoryModel ) {
        final TestConnectionFactoryModel tcfm = ( TestConnectionFactoryModel ) model;
        final String originalId = tcfm.getOriginalId();
        // remove original driver
        final ConnectionFactoryModel originalModel = cfmm.getModel( originalId );
        if ( null != originalModel ) {
          cfmm.deregister( originalModel );
        }
        // register the test driver
        cfmm.register( new UnderCoverConnectionFactoryModel( tcfm, originalId ) );
      }
    }
  }

  private static class UnderCoverConnectionFactoryModel implements ConnectionFactoryModel {

    private static final long serialVersionUID = 1L;

    private final ConnectionFactoryModel delegate;
    private final String id;

    public UnderCoverConnectionFactoryModel( final ConnectionFactoryModel delegate, final String id ) {
      super();
      this.delegate = delegate;
      this.id = id;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public ConnectionFactory getInstance() throws ConfigurationException {
      return delegate.getInstance();
    }

    @Override
    public String getName() {
      return delegate.getName();
    }

    @Override
    public String getDescription() {
      return delegate.getDescription();
    }

  }

  /* ***********************
   *   I N J E C T I O N   *
   *********************** */

  @SuppressWarnings( "unchecked" )
  public <T> T getInjectedValue( final InjectionContext ic, final Class<T> type ) throws ConfigurationException {
    if ( configuredTypes.containsKey( ic.getApplication() ) ) {
      final Injector injector = DaoJonesContextBuilder.injectors.get( type );
      if ( null != injector ) {
        return ( T ) injector.getInjectedValue( ic );
      } else {
        throw new ConfigurationException( DaoJonesContextBuilder.bundle.get( "inject.invalidType", type.getName() ) );
      }
    } else {
      throw new ConfigurationException( DaoJonesContextBuilder.bundle.get( "inject.noApplication", ic.getApplication() ) );
    }
  }

  private static final Map<Class<?>, Injector> injectors = new InjectorsMap();

  private static final class InjectorsMap extends HashMap<Class<?>, Injector> {
    private static final long serialVersionUID = 1L;
    {
      put( DaoJonesContext.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext();
        }

      } );
      put( DaoJonesContextConfiguration.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration();
        }

      } );
      put( ApplicationModelManager.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration().getApplicationModelManager();
        }

      } );
      put( ApplicationModel.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration().getApplicationModelManager().getModel( ic.getApplication() );
        }

      } );
      put( BeanModelManager.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration().getBeanModelManager();
        }

      } );
      put( CacheFactoryModelManager.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration().getCacheFactoryModelManager();
        }

      } );
      put( ConnectionFactoryModelManager.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration().getConnectionFactoryModelManager();
        }

      } );
      put( ConnectionModelManager.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getConfiguration().getConnectionModelManager();
        }

      } );
      put( Application.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getApplication( ic.getApplication() );
        }

      } );
      put( ApplicationContext.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getApplication( ic.getApplication() ).getApplicationContext();
        }

      } );
      put( InjectionEngine.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getApplication( ic.getApplication() ).getInjectionEngine();
        }

      } );
      put( ConnectionProvider.class, new Injector() {

        @Override
        public Object getInjectedValue( final InjectionContext ic ) {
          return ic.getContext().getApplication( ic.getApplication() );
        }

      } );
    }
  };

  @Override
  public void close() throws IOException {
    // TODO remove when objects can directly be stored within the model
    TestContext.removeTestSetup( threaded );
    test = null;
    configuredTypes.clear();
    context.close();
  }

  public boolean definesModel( final ConnectionModel model ) throws IOException {
    final Class<?> c = getTestClass();
    for ( final Field field : c.getDeclaredFields() ) {
      final TestModel tm = field.getAnnotation( TestModel.class );
      if ( null != tm && tm.application().equals( model.getId().getApplicationId() ) ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Model resolveModel( final ConnectionModel model ) throws IOException {
    final Model result = new Model();
    final Class<?> c = getTestClass();
    for ( final Field field : c.getDeclaredFields() ) {
      final TestModel tm = field.getAnnotation( TestModel.class );
      if ( null != tm && tm.application().equals( model.getId().getApplicationId() ) ) {
        final boolean accessible = field.isAccessible();
        if ( !accessible ) {
          field.setAccessible( true );
        }
        try {
          try {
            if ( null == test && !Modifier.isStatic( field.getModifiers() ) ) {
              throw new IOException( DaoJonesContextBuilder.bundle.get( "testmodel.notinitialized", c.getName(),
                      field.getName() ) );
            } else {
              final Object tmValue = field.get( test );
              try {
                result.getDataSources().add( ( DataSource ) tmValue );
              } catch ( final ClassCastException e ) {
                // only occurs when tmValue is not null
                throw new IOException( DaoJonesContextBuilder.bundle.get( "testmodel.invalidType", c.getName(),
                        field.getName(), tmValue.getClass().getName(), DataSource.class.getName() ), e );
              }
            }
          } catch ( final IllegalArgumentException e ) {
            throw new IOException( e );
          } catch ( final IllegalAccessException e ) {
            throw new IOException( e );
          }
        } finally {
          if ( !accessible ) {
            field.setAccessible( false );
          }
        }
      }
    }
    return result;
  }
}
