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

  private static final Messages bundle = Messages.create("GradlePlugin");

  @Override
  public void apply(final Project project) {
    if (project.getPlugins().hasPlugin("java")) {
      project.getTasks().create("scanBeanModel", ScanBeanModelTask.class);
    } else {
      project.getLogger().warn(GradlePlugin.bundle.get("warn.java.missing"));
    }
    // TODO insert into Java cycle
    // TODO get compile dependencies
  }
}
