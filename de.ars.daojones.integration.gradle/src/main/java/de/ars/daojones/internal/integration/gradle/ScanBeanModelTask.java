package de.ars.daojones.internal.integration.gradle;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolveException;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.jvm.JvmBinarySpec;
import org.gradle.platform.base.BinaryContainer;
import org.gradle.platform.base.BinarySpec;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;

/**
 * Implementation for <tt>scanBeanModel> task.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class ScanBeanModelTask extends DefaultTask {

  private static final Messages bundle = Messages.create( "ScanModelTask" );

  /**
   * Resolves the compile dependencies of the project.
   *
   * @return the compile dependencies
   * @throws UnknownConfigurationException
   *           if the project hasn't any compile configuration (that is
   *           typically assigned by the java plugin)
   * @throws ResolveException
   *           if the dependencies of the project could not be resolved
   */
  protected Collection<ResolvedArtifact> resolveCompileDependencies()
          throws UnknownConfigurationException, ResolveException {
    final Configuration compile = getProject().getConfigurations().getByName( JavaPlugin.COMPILE_CONFIGURATION_NAME );
    final ResolvedConfiguration resolvedConfiguration = compile.getResolvedConfiguration();
    resolvedConfiguration.rethrowFailure();
    final Set<ResolvedArtifact> artifacts = resolvedConfiguration.getResolvedArtifacts();
    return artifacts;
  }

  /**
   * Finds the JVM binary spec.
   *
   * @return the JVM binary spec
   * @throws UnknownDomainObjectException
   *           if the project hasn't any binary spec
   */
  protected JvmBinarySpec getJvmBinarySpec() throws UnknownDomainObjectException {
    final BinaryContainer bc = getProject().getExtensions().getByType( BinaryContainer.class );
    final BinarySpec mainSpec = bc.getByName( "mainClasses" );
    if ( mainSpec instanceof JvmBinarySpec ) {
      return ( JvmBinarySpec ) mainSpec;
    } else {
      throw new UnknownDomainObjectException( ScanBeanModelTask.bundle.get( "error.jvmBinarySpec.wrongType",
              JvmBinarySpec.class.getName(), mainSpec.getClass().getName() ) );
    }
  }

  @TaskAction
  public void scan() {
    try {
      final ScanBeanModel command = new ScanBeanModel();
      final JvmBinarySpec binarySpec = getJvmBinarySpec();
      // set bytecode directory to scan for annotations (build/classes/main by default)
      command.setBytecodeDirectory( binarySpec.getClassesDir() );
      // set target directory to create XML file (build/resources/main by default)
      command.setTargetDirectory( binarySpec.getResourcesDir() );
      // dependency-resolving class loader must contain all project dependencies with compile scope
      final Collection<ResolvedArtifact> compileDependencies = resolveCompileDependencies();
      final StringBuilder sbDebug = new StringBuilder();
      final URL[] classpathElementUrls = new URL[compileDependencies.size()];
      int idx = 0;
      for ( final ResolvedArtifact artifact : compileDependencies ) {
        classpathElementUrls[idx] = artifact.getFile().toURI().toURL();
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
