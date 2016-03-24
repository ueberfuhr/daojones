package de.ars.daojones.runtime.configuration.provider;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.ars.daojones.internal.runtime.utilities.JAXBReader;
import de.ars.daojones.internal.runtime.utilities.JAXBWriter;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.BeanConfiguration;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappingReference;
import de.ars.daojones.runtime.configuration.beans.GlobalConverter;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelImpl;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModelImpl;

/**
 * Reads the beans configuration from a beans configuration XML resource.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class XmlBeanConfigurationSource extends XmlConfigurationSource<BeanConfiguration, BeanConfiguration> {
  // BeanConfiguration instead of BeanModel because we must encapsulate the bean model and the global converters model

  private static final String QUALIFIER = ".."; // any NCName character to separate class name from field name

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application
   * @param resourceURL
   *          the URL to the XML resource
   */
  public XmlBeanConfigurationSource( final String application, final URL resourceURL ) {
    super( application, resourceURL );
  }

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application
   * @param ioProvider
   *          the I/O provider
   */
  public XmlBeanConfigurationSource( final String application, final IOProvider ioProvider ) {
    super( application, ioProvider );
  }

  protected XmlBeanConfigurationSource( final String application, final IOProvider ioProvider,
          final ApplicationConfigurationSourceAdapter<BeanConfiguration> cache ) {
    super( application, ioProvider, cache );
  }

  @Override
  protected URL getSchema() {
    return XmlBeanConfigurationSource.class.getResource( "/META-INF/schemas/daojones-2.0-beans.xsd" );
  }

  @Override
  protected Class<? extends BeanConfiguration> getRootElementType() {
    return BeanConfiguration.class;
  }

  @Override
  protected JAXBReader.Callback getReaderCallback() {
    return new JAXBReader.Callback() {

      @Override
      public void beforeUnmarshal( final Object target, final Object parent ) {
      }

      @Override
      public void afterUnmarshal( final Object target, final Object parent ) {
        if ( target instanceof DatabaseFieldMapping ) {
          final DatabaseFieldMapping mapping = ( DatabaseFieldMapping ) target;
          final String id = mapping.getId();
          if ( null != id ) {
            final int idx = id.indexOf( XmlBeanConfigurationSource.QUALIFIER );
            if ( idx >= 0 ) {
              mapping.setId( id.substring( idx + 2 ) );
            }
          }
        } else if ( target instanceof DatabaseFieldMappingReference ) {
          final DatabaseFieldMappingReference ref = ( DatabaseFieldMappingReference ) target;
          final Object id = ref.getMapping();
          if ( id instanceof String ) {
            final int idx = id.toString().indexOf( XmlBeanConfigurationSource.QUALIFIER );
            if ( idx >= 0 ) {
              ref.setMapping( id.toString().substring( idx + 2 ) );
            }
          }
        }
      }
    };
  }

  @Override
  protected JAXBWriter.Callback getWriterCallback() {

    return new JAXBWriter.Callback() {

      private final Map<String, String> declaredFieldMappingsByBean = new HashMap<String, String>();

      private String currentBeanId;
      private String originalId;
      private DatabaseFieldMapping originalMapping;
      private DatabaseFieldMappingReference originalMappingRef;

      @Override
      public void beforeMarshal( final Object model ) {
        if ( model instanceof Bean ) {
          declaredFieldMappingsByBean.clear();
          currentBeanId = ( ( Bean ) model ).getType();
        } else if ( model instanceof DatabaseFieldMappedElement ) {
          final DatabaseFieldMappedElement element = ( DatabaseFieldMappedElement ) model;
          final DatabaseFieldMapping mapping = element.getFieldMapping();
          if ( null != mapping ) {
            originalMapping = mapping;
            originalMappingRef = element.getFieldMappingRef();
            originalId = mapping.getId();
            if ( null != originalId ) {
              final String newId = currentBeanId + XmlBeanConfigurationSource.QUALIFIER + originalId;
              mapping.setId( newId );
              if ( null != declaredFieldMappingsByBean.put( originalId, newId ) ) {
                // repeating declaration -> replace by ref
                final DatabaseFieldMappingReference ref = new DatabaseFieldMappingReference();
                ref.setMapping( mapping );
                element.setFieldMappingRef( ref );
                element.setFieldMapping( null );
              }
            }
          }
        }
      }

      @Override
      public void afterMarshal( final Object model ) {
        if ( model instanceof Bean ) {
          declaredFieldMappingsByBean.clear();
          currentBeanId = null;
        } else if ( model instanceof DatabaseFieldMappedElement ) {
          final DatabaseFieldMappedElement element = ( DatabaseFieldMappedElement ) model;
          if ( null != originalMapping ) {
            element.setFieldMapping( originalMapping );
            if ( null != originalId ) {
              originalMapping.setId( originalId );
            }
          }
          if ( null != originalMappingRef ) {
            element.setFieldMappingRef( originalMappingRef );
          }
          originalId = null;
          originalMapping = null;
          originalMappingRef = null;
        }
      }
    };
  }

  @Override
  protected Class<? extends BeanConfiguration> getModelType() {
    return BeanConfiguration.class;
  }

  @Override
  protected ConfigurationProvider<BeanConfiguration> createConfigurationProviderForModel( final BeanConfiguration root )
          throws ConfigurationException {
    return new ConfigurationProvider<BeanConfiguration>() {

      @Override
      public Iterable<BeanConfiguration> readConfiguration() throws ConfigurationException {
        return Arrays.asList( root );
      }
    };
  }

  @Override
  public ConfigurationProvider<BeanModel> getBeanModelConfigurationProvider() throws ConfigurationException {
    return new ConfigurationProvider<BeanModel>() {

      @Override
      public Iterable<BeanModel> readConfiguration() throws ConfigurationException {
        final Collection<BeanModel> result = new HashSet<BeanModel>();
        try {
          for ( final Bean bean : getRootElement().getBeans() ) {
            result.add( new BeanModelImpl( getApplication(), bean ) );
          }
        } catch ( final IOException e ) {
          throw new ConfigurationException();
        }
        return result;
      }
    };
  }

  @Override
  public ConfigurationProvider<GlobalConverterModel> getGlobalConverterModelConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<GlobalConverterModel>() {

      @Override
      public Iterable<GlobalConverterModel> readConfiguration() throws ConfigurationException {
        final Collection<GlobalConverterModel> result = new HashSet<GlobalConverterModel>();
        try {
          for ( final GlobalConverter converter : getRootElement().getConverters() ) {
            result.add( new GlobalConverterModelImpl( getApplication(), converter ) );
          }
        } catch ( final IOException e ) {
          throw new ConfigurationException();
        }
        return result;
      }
    };
  }

  @Override
  public ConfigurationSource adaptTo( final String application ) throws ConfigurationException {
    return new XmlBeanConfigurationSource( getApplication(), getIOProvider(), this );
  }

}
