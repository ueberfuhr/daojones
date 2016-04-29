package de.ars.daojones.internal.integration.gradle;

import static de.ars.daojones.internal.integration.gradle.DependsOn.dependsOn;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GradlePluginTest {

  @Rule
  public ExpectedException ex = ExpectedException.none();

  private Project project;

  @Before
  public void setup() {
    project = ProjectBuilder.builder().build();
  }

  @Test
  public void testTaskScanBeanModelTaskConfiguration() {
    project.getPlugins().apply( "java" );
    final Plugin<?> plugin = project.getPlugins().apply( "daojones" );
    assertThat( plugin, is( instanceOf( GradlePlugin.class ) ) );
    final TaskContainer tasks = project.getTasks();
    final Task task = tasks.getByName( GradlePlugin.SCAN_BEAN_MODEL_TASK_NAME );
    assertThat( task, Matchers.is( instanceOf( ScanBeanModelTask.class ) ) );
    assertThat( task.getGroup(), is( nullValue() ) ); // private task
    // check dependencies
    assertThat( task, dependsOn( tasks.getByName( JavaPlugin.COMPILE_JAVA_TASK_NAME ) ) );
    assertThat( tasks.getByName( JavaPlugin.CLASSES_TASK_NAME ), dependsOn( task ) );
    assertThat( tasks.getByName( JavaPlugin.TEST_TASK_NAME ), dependsOn( task ) );
  }

  @Test
  public void testTaskNotAddedWithoutJavaPlugin() {
    project.getPlugins().apply( "daojones" );
    ex.expect( UnknownTaskException.class );
    project.getTasks().getByName( GradlePlugin.SCAN_BEAN_MODEL_TASK_NAME );
  }

}
