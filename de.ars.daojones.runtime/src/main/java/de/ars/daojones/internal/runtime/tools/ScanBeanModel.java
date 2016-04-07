package de.ars.daojones.internal.runtime.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.BeanConfiguration;
import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;
import de.ars.daojones.runtime.configuration.provider.XmlBeanConfigurationSource;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

/**
 * The command that scans a classpath for annotated bean classes and creates a
 * bean model XML file.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class ScanBeanModel {

  private static final Messages logger = Messages.create( "tools.ScanBeanModel" );
  private static final String APP_ID = "scan-bean-model";

  /**
   * The classloader that loads dependencies.
   */
  private ClassLoader dependenciesClassLoader = Thread.currentThread().getContextClassLoader();
  /**
   * The directory that contains the bytecode that has to be scanned.
   */
  private File bytecodeDirectory = new File( System.getProperty( "user.dir" ) );
  /**
   * The directory where the XML file is created.
   */
  private File targetDirectory = new File( System.getProperty( "user.dir" ) );

  public ClassLoader getDependenciesClassLoader() {
    return dependenciesClassLoader;
  }

  public void setDependenciesClassLoader( final ClassLoader dependenciesClassLoader ) {
    this.dependenciesClassLoader = dependenciesClassLoader;
  }

  public File getBytecodeDirectory() {
    return bytecodeDirectory;
  }

  public void setBytecodeDirectory( final File bytecodeDirectory ) {
    this.bytecodeDirectory = bytecodeDirectory;
  }

  public File getTargetDirectory() {
    return targetDirectory;
  }

  public void setTargetDirectory( final File targetDirectory ) {
    this.targetDirectory = targetDirectory;
  }

  private static void normalize( final ConfigurationSource config ) throws DataAccessException, ConfigurationException {
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    final DaoJonesContext ctx = dcf.createContext();
    final DaoJonesContextConfigurator configurator = new DaoJonesContextConfigurator( ctx );
    try {
      configurator.configure( config );
    } finally {
      ctx.close();
    }
  }

  private String toString( final Enumeration<URL> urls ) {
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    while ( urls.hasMoreElements() ) {
      if ( first ) {
        first = false;
      } else {
        sb.append( ";" );
      }
      final URL url = urls.nextElement();
      sb.append( url.toExternalForm() );
    }
    return sb.toString();
  }

  public void execute() throws Exception {
    // directory must exist
    if ( getBytecodeDirectory().exists() && getBytecodeDirectory().isDirectory() ) {
      final URLClassLoader dependenciesClassLoader = new URLClassLoader(
              new URL[] { getBytecodeDirectory().toURI().toURL() }, this.dependenciesClassLoader );
      ScanBeanModel.logger.log( Level.INFO, "info.scan.dir", getBytecodeDirectory().getAbsolutePath(),
              toString( dependenciesClassLoader.getResources( "." ) ) );
      try {
        final AnnotationBeanConfigurationSource cp = new AnnotationBeanConfigurationSource( ScanBeanModel.APP_ID,
                dependenciesClassLoader, getBytecodeDirectory().toURI().toURL() );
        // if target model file already exist -> ignore
        cp.setUseAlternativeConfiguration( false );
        final BeanConfiguration bc = cp.getConfiguration();
        //get normalized values
        ScanBeanModel.normalize( cp );
        final File alternativeConfigFile = new File( targetDirectory,
                AnnotationBeanConfigurationSource.ALTERNATE_CONFIG_FILE );
        final File alternativeConfigDirectory = alternativeConfigFile.getParentFile();
        if ( !alternativeConfigDirectory.exists() && !alternativeConfigDirectory.mkdirs() ) {
          throw new IOException(
                  ScanBeanModel.logger.get( "error.makeTarget", alternativeConfigDirectory.getAbsolutePath() ) );
        }
        if ( alternativeConfigFile.exists() ) {
          if ( !alternativeConfigFile.delete() ) {
            ScanBeanModel.logger.log( Level.WARNING, "warn.configfile.notdeleted",
                    alternativeConfigFile.getAbsolutePath() );
          }
        }
        @SuppressWarnings( "resource" ) // do not close it!
        final XmlBeanConfigurationSource xbcp = new XmlBeanConfigurationSource( ScanBeanModel.APP_ID,
                alternativeConfigFile.toURI().toURL() );
        xbcp.writeRootElement( bc );
        ScanBeanModel.logger.log( Level.INFO, "info.done", bc.getBeans().size(),
                alternativeConfigFile.getAbsolutePath() );
      } finally {
        // Remove this later
        dependenciesClassLoader.clearAssertionStatus();
        // Replace by (since Java 7)
        // dependenciesClassloader.close();
      }
    } else {
      // if there isn't any bytecode, just give a warning
      ScanBeanModel.logger.log( Level.WARNING, "warn.target", getBytecodeDirectory().getAbsolutePath() );
    }
  }

}
