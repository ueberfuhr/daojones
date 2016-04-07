package de.ars.daojones.internal.integration.gradle;

import org.gradle.api.Task;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Tests that a task is dependent from another dependency.
 *
 * @see Task#dependsOn(Object...)
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class DependsOn extends TypeSafeMatcher<Task> {

  private final Object dependency;

  /**
   * Constructor.
   *
   * @param dependency
   *          the dependency.
   */
  public DependsOn( final Object dependency ) {
    super();
    this.dependency = dependency;
  }

  @Override
  public void describeTo( final Description description ) {
    description.appendText( "dependency on " + dependency );
  }

  @Override
  protected void describeMismatchSafely( final Task item, final Description mismatchDescription ) {
    mismatchDescription.appendText( "task \':" + item.getName() + "\' is not dependent from " + dependency );
  }

  @Override
  protected boolean matchesSafely( final Task task ) {
    return task.getDependsOn().contains( dependency );
  }

  /**
   * Creates the matcher instance.
   *
   * @param dependency
   *          the dependency
   * @return the matcher instance
   */
  public static DependsOn dependsOn( final Object dependency ) {
    return new DependsOn( dependency );
  }

}
