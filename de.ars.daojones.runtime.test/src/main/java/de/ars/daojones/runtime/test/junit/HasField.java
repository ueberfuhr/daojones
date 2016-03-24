package de.ars.daojones.runtime.test.junit;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContextImpl;

/**
 * A matcher that matches if the database accessor has the named field. Use the
 * methods of this matcher to create matchers that can check the field's value.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class HasField extends TypeSafeMatcher<DatabaseAccessor> {

  private static final Messages bundle = Messages.create( "junit.HasField" );

  private final String name;

  private HasField( final String name ) {
    super();
    this.name = name;
  }

  @Override
  public void describeTo( final Description description ) {
    description.appendText( HasField.bundle.get( "hasfield.description", name ) );
  }

  @Override
  protected void describeMismatchSafely( final DatabaseAccessor item, final Description mismatchDescription ) {
    try {
      mismatchDescription.appendText( HasField.bundle.get( "hasfield.mismatch.description",
              Arrays.asList( item.getFields() ).toString() ) );
    } catch ( final DataAccessException e ) {
      mismatchDescription.appendText( HasField.bundle.get( "hasfield.mismatch.error.description", name ) );
    }
  }

  @Override
  protected boolean matchesSafely( final DatabaseAccessor item ) {
    try {
      return Arrays.asList( item.getFields() ).contains( name );
    } catch ( final DataAccessException e ) {
      throw new AssertionError( e );
    }
  }

  /**
   * Returns the has-field matcher.
   * 
   * @param name
   *          the field name
   * @return the has-field matcher
   */
  public static HasField hasField( final String name ) {
    return new HasField( name );
  }

  /**
   * Type that provides matchers for the given field type.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   * @param <T>
   *          the field type
   */
  public static interface OfType<T> {

    Matcher<DatabaseAccessor> which( final Matcher<T> matcher );

  }

  /**
   * Returns an instance that provides matchers for the given field type.
   * 
   * @param fieldType
   *          the field type
   * @return the instance that provides matchers for the given field type
   */
  public <T> OfType<T> ofType( final Class<T> fieldType ) {
    return new OfType<T>() {
      @Override
      public Matcher<DatabaseAccessor> which( final Matcher<T> matcher ) {
        final Matcher<DatabaseAccessor> which = new TypeSafeMatcher<DatabaseAccessor>() {

          private boolean describedOnce = false;

          @Override
          public void describeTo( final Description description ) {
            if ( !describedOnce ) {
              description.appendText( HasField.bundle.get( "which.description", fieldType.getName() ) + " " );
              matcher.describeTo( description );
            }
            describedOnce = true;
          }

          @Override
          protected void describeMismatchSafely( final DatabaseAccessor item, final Description mismatchDescription ) {
            try {
              final T value = item.getFieldValue( new FieldContextImpl<T>( name, fieldType, UpdatePolicy.REPLACE,
                      Collections.<Property> emptyList() ) );
              matcher.describeMismatch( value, mismatchDescription );
            } catch ( final Exception e ) {
              throw new AssertionError( e );
            }
          }

          @Override
          protected boolean matchesSafely( final DatabaseAccessor item ) {
            try {
              final T value = item.getFieldValue( new FieldContextImpl<T>( name, fieldType, UpdatePolicy.REPLACE,
                      Collections.<Property> emptyList() ) );
              return matcher.matches( value );
            } catch ( final Exception e ) {
              throw new AssertionError( e );
            }
          }

        };
        return CoreMatchers.allOf( HasField.this, which );
      }
    };
  }

}
