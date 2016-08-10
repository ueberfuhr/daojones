package de.ars.daojones.internal.integration.gradle;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.gradle.api.DefaultTask;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ResolveException;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;

/**
 * Implementation for <tt>scanBeanModel</tt> task.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class ScanBeanModelTask extends DefaultTask {

  private static final Messages bundle = Messages.create( "ScanModelTask" );

  /**
   * Resolves the compile dependencies of the project.
   *
   * @return the resolved files
   * @throws UnknownConfigurationException
   *           if the project hasn't any compile configuration (that is
   *           typically assigned by the java plugin)
   * @throws ResolveException
   *           if the dependencies of the project could not be resolved
   */
  protected Collection<File> resolveCompileDependencies() throws UnknownConfigurationException, ResolveException {
    final Configuration compile = getProject().getConfigurations().getByName( JavaPlugin.COMPILE_CONFIGURATION_NAME );
    final ResolvedConfiguration resolvedConfiguration = compile.getResolvedConfiguration();
    resolvedConfiguration.rethrowFailure();
    final Collection<File> result = resolvedConfiguration.getFiles( new Spec<Dependency>() {

      @Override
      public boolean isSatisfiedBy( final Dependency dep ) {
        return true;
      }

    } );
    return result;
  }

  /**
   * Finds the main source set.
   *
   * @return the main source set
   * @throws UnknownDomainObjectException
   *           if the project hasn't any main source set
   */
  protected SourceSetOutput getMainSourceSet() throws UnknownDomainObjectException {
    final JavaPluginConvention javaConvention = getProject().getConvention().getPlugin( JavaPluginConvention.class );
    final SourceSetContainer sourceSets = javaConvention.getSourceSets();
    final SourceSet mainSourceSet = sourceSets.getByName( SourceSet.MAIN_SOURCE_SET_NAME );
    final SourceSetOutput mainOutput = mainSourceSet.getOutput();
    return mainOutput;
  }

  @TaskAction
  public void scan() {
    try {
      final ScanBeanModel command = new ScanBeanModel();
      final SourceSetOutput main = getMainSourceSet();
      // set bytecode directory to scan for annotations (build/classes/main by default)
      command.setBytecodeDirectory( main.getClassesDir() );
      // set target directory to create XML file (build/resources/main by default)
      command.setTargetDirectory( main.getResourcesDir() );
      // dependency-resolving class loader must contain all project dependencies with compile scope
      final Collection<File> compileDependencies = resolveCompileDependencies();
      final StringBuilder sbDebug = new StringBuilder();
      final URL[] classpathElementUrls = new URL[compileDependencies.size()];
      int idx = 0;
      for ( final File artifact : compileDependencies ) {
        classpathElementUrls[idx] = artifact.toURI().toURL();
        sbDebug.append( '\n' );
        sbDebug.append( classpathElementUrls[idx] );
        idx++;
      }
      getLogger().debug( ScanBeanModelTask.bundle.get( "debug.classpath", sbDebug.toString() ) );
      try ( final URLClassLoader dependenciesClassLoader = new URLClassLoader( classpathElementUrls,
              ScanBeanModelTask.class.getClassLoader() ) ) {
        command.setDependenciesClassLoader( dependenciesClassLoader );
        command.execute();
      }
    } catch ( final Exception e ) {
      throw new TaskExecutionException( this, e );
    }
  }

}
