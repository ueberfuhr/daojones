package de.ars.daojones.internal.integration.gradle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;

/**
 * Implementation for <tt>scanBeanModel> task.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class ScanBeanModelTask extends DefaultTask {

  private static final Messages bundle = Messages.create("ScanBeanModelTask");

  private static URL[] toURLs(final Collection<String> files) throws IOException {
    final URL[] result = new URL[files.size()];
    int idx = 0;
    for (final String file : files) {
      result[idx] = new File(file).toURI().toURL();
      idx++;
    }
    return result;
  }

  @TaskAction
  public void scan() {
    final Project project = getProject();
    try {
      final ScanBeanModel command = new ScanBeanModel();
      // set bytecode directory
      // TODO command.setBytecodeDirectory(new File(project.getBuild().getOutputDirectory()));
      // set the target directory (the same)
      // TODO command.setTargetDirectory(new File(project.getBuild().getOutputDirectory()));
      // dependency-resolving class loader must contain all project dependencies with compile scope
      final List<String> classpathElements = new LinkedList<String>(); // TODO project.getCompileClasspathElements();
      getLogger().debug(
              ScanBeanModelTask.bundle.get("debug.classpath", classpathElements));
      final URL[] classpathElementUrls = ScanBeanModelTask.toURLs(classpathElements);
      final URLClassLoader dependenciesClassLoader = new URLClassLoader(
              classpathElementUrls, ScanBeanModelTask.class.getClassLoader());
      command.setDependenciesClassLoader(dependenciesClassLoader);
      try {
        command.execute();
      } finally {
        // Remove this later
        dependenciesClassLoader.clearAssertionStatus();
        // Replace by (since Java 7)
        // dependenciesClassloader.close();
      }
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw new TaskExecutionException(this, e);
    }
  }

}
