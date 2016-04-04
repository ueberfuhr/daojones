package de.ars.daojones.internal.integration.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle Integration Plugin for DaoJones.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class GradlePlugin implements Plugin<Project> {

  @Override
  public void apply(final Project project) {
    project.getTasks().create("scanBeanModel", ScanBeanModelTask.class);
    // TODO insert into Java cycle
  }

}
