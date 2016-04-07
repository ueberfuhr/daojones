package de.ars.daojones.internal.integration.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class GradlePluginTest {

  @Test
  public void testTaskAdd() {
    final Project project = ProjectBuilder.builder().build();
    final Plugin<?> plugin = project.getPlugins().apply( "daojones" );
    Assert.assertThat( plugin, Matchers.is( Matchers.instanceOf( GradlePlugin.class ) ) );
    final Task task = project.getTasks().getByName( "scanBeanModel" );
    Assert.assertThat( task, Matchers.is( Matchers.instanceOf( ScanBeanModelTask.class ) ) );
  }

}
