package de.ars.daojones.internal.integration.maven;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;

/**
 * Goal which scans the project's classes for bean annotations and writes the
 * model into an XML file. This prevents annotation scanning during runtime.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
// RequiresDependencyResolution because of Reflections that has to scan bytecode for annotations.
@Mojo( name = "scan-bean-model", defaultPhase = LifecyclePhase.PROCESS_CLASSES, inheritByDefault = true, requiresDependencyResolution = ResolutionScope.COMPILE )
public class ScanBeanModelMojo extends AbstractMojo {

  private static final Messages bundle = Messages.create( "ScanMojo" );

  @Component
  private MavenProject project;

  private static URL[] toURLs( final Collection<String> files ) throws IOException {
    final URL[] result = new URL[files.size()];
    int idx = 0;
    for ( final String file : files ) {
      result[idx] = new File( file ).toURI().toURL();
      idx++;
    }
    return result;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void execute() throws MojoExecutionException {
    try {
      final ScanBeanModel command = new ScanBeanModel();
      // set bytecode directory
      command.setBytecodeDirectory( new File( project.getBuild().getOutputDirectory() ) );
      // set the target directory (the same)
      command.setTargetDirectory( new File( project.getBuild().getOutputDirectory() ) );
      // dependency-resolving class loader must contain all project dependencies with compile scope
      final List<String> classpathElements = project.getCompileClasspathElements();
      getLog().debug( ScanBeanModelMojo.bundle.get( "debug.classpath", classpathElements ) );
      final URL[] classpathElementUrls = ScanBeanModelMojo.toURLs( classpathElements );
      final URLClassLoader dependenciesClassLoader = AccessController
              .doPrivileged( new PrivilegedAction<URLClassLoader>() {

                @Override
                public URLClassLoader run() {
                  return new URLClassLoader( classpathElementUrls, ScanBeanModelMojo.class.getClassLoader() );
                }
              } );
      command.setDependenciesClassLoader( dependenciesClassLoader );
      try {
        command.execute();
      } finally {
        // Remove this later
        dependenciesClassLoader.clearAssertionStatus();
        // Replace by (since Java 7)
        // dependenciesClassloader.close();
      }
    } catch ( final Exception e ) {
      throw new MojoExecutionException( ScanBeanModelMojo.bundle.get( "error" ), e );
    }
  }

}
