package de.ars.daojones.internal.integration.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;

/**
 * Gradle Integration Plugin for DaoJones.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class GradlePlugin implements Plugin<Project> {

  private static final Messages bundle = Messages.create( "GradlePlugin" );

  /**
   * The name of the <tt>scanBeanModel</tt> task.
   */
  public static final String SCAN_BEAN_MODEL_TASK_NAME = "scanBeanModel";

  @Override
  public void apply( final Project project ) {
    if ( project.getPlugins().hasPlugin( "java" ) ) {
      final TaskContainer tasks = project.getTasks();
      final Task scanBeanModel = tasks.create( GradlePlugin.SCAN_BEAN_MODEL_TASK_NAME, ScanBeanModelTask.class );
      scanBeanModel.setDescription( GradlePlugin.bundle.get( "scanBeanModelTask.description" ) );
      // scanBeanModel depends on compileJava
      final Task compileJava = tasks.getByName( JavaPlugin.COMPILE_JAVA_TASK_NAME );
      scanBeanModel.dependsOn( compileJava );
      // jar depends on classes, that depends on scanBeanModel
      final Task classes = tasks.getByName( JavaPlugin.CLASSES_TASK_NAME );
      classes.dependsOn( scanBeanModel );
      // test depends on scanBeanModel
      final Task test = tasks.getByName( JavaPlugin.TEST_TASK_NAME );
      test.dependsOn( scanBeanModel );
    } else {
      project.getLogger().warn( GradlePlugin.bundle.get( "warn.java.missing" ) );
    }
  }
}
