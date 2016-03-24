package de.ars.daojones.runtime.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.internal.runtime.test.Constants;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.TestModelBuilder;

/**
 * Marks a field of type {@link DataSource} to get used as test model. The field
 * can be static (only recommended if the model is not modifed during the test).
 * If the model is modified during test, it can be verified using Hamcrest
 * Matchers that are provided by the {@link DaoJonesMatchers} class.<br/>
 * <br/>
 * You can create a {@link DataSource} and instances of {@link Entry} with the
 * help of the {@link TestModelBuilder} class.<br/>
 * <br/>
 * <b>Example:</b>
 * 
 * <pre>
 *   import static de.ars.daojones.runtime.test.data.TestModelBuilder.*;
 *   ...
 *   &#0064;TestModel
 *   private static final DataSource ds = <i>newDataSource( "MixedForm" )</i>
 *     .withEntries(
 *       <i>newEntry()</i>.withId( "entry1" )
 *         .withProperty( "name", "Entry 1" ),
 *       <i>newEntry()</i>.withId( "entry2" )
 *         .withProperty( "name", "Entry 2" )
 *         .withProperty( "generated", "true" ),
 *       <i>newEntry()</i>.withId( "entry3" )
 *         .withProperty( "name", "Entry 3" )
 *     ).build();
 * </pre>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @see TestModelBuilder
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface TestModel {

  /**
   * The application id that the test model is used for.
   * 
   * @return the application id
   */
  String application() default Constants.DEFAULT_APPLICATION;

}
