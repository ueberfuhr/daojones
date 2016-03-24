package de.ars.daojones.internal.integration.ant;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;

/**
 * Ant task that scans annotations and writes the model into an XML file. This
 * prevents annotation scanning during runtime.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class ScanBeanModelTask extends Task {

  private static final Messages logger = Messages.create( "ScanBeanModelTask" );

  private final List<Path> paths = new LinkedList<Path>();
  private String bytecodeDirectory;
  private String targetDirectory;

  public void addPath( final Path path ) {
    paths.add( path );
  }

  public String getBytecodeDirectory() {
    return bytecodeDirectory;
  }

  public void setBytecodeDirectory( final String bytecodeDirectory ) {
    this.bytecodeDirectory = bytecodeDirectory;
  }

  public String getTargetDirectory() {
    return targetDirectory;
  }

  public void setTargetDirectory( final String targetDirectory ) {
    this.targetDirectory = targetDirectory;
  }

  @Override
  public void execute() throws BuildException {
    try {
      final ScanBeanModel command = new ScanBeanModel();
      if ( null != getBytecodeDirectory() ) {
        command.setBytecodeDirectory( new File( getBytecodeDirectory() ) );
      }
      if ( null != getTargetDirectory() ) {
        command.setTargetDirectory( new File( getTargetDirectory() ) );
      }
      ClassLoader dependenciesClassLoader = ScanBeanModel.class.getClassLoader();
      if ( !paths.isEmpty() ) {
        final List<URL> urls = new LinkedList<URL>();
        for ( final Path path : paths ) {
          for ( final Resource resource : path ) {
            final FileProvider fp = resource.as( FileProvider.class );
            final File file = fp.getFile();
            final URL url = file.toURI().toURL();
            urls.add( url );
          }
        }
        ScanBeanModelTask.logger.log( Level.FINER, "path", urls );
        dependenciesClassLoader = new URLClassLoader( urls.toArray( new URL[urls.size()] ), dependenciesClassLoader );
      }
      command.setDependenciesClassLoader( dependenciesClassLoader );
      command.execute();
    } catch ( final Exception e ) {
      throw new BuildException( e );
    }
  }
}
