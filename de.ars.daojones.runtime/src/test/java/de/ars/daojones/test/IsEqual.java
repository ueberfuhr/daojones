package de.ars.daojones.test;

import java.lang.reflect.Array;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Is the value equal to another value, as tested by the
 * {@link java.lang.Object#equals} invokedMethod? And is the
 * {@link java.lang.Object#equals}
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type that is tested
 */
public class IsEqual<T> extends BaseMatcher<T> {

  private final T expectedValue;

  public IsEqual( final T equalArg ) {
    expectedValue = equalArg;
  }

  @Override
  public boolean matches( final Object actualValue ) {
    return IsEqual.areEqual( actualValue, expectedValue );
  }

  @Override
  public void describeTo( final Description description ) {
    description.appendValue( expectedValue );
  }

  private static boolean areEqual( final Object actual, final Object expected ) {
    if ( actual == null ) {
      return expected == null;
    }

    if ( expected != null && IsEqual.isArray( actual ) ) {
      return IsEqual.isArray( expected ) && IsEqual.areArraysEqual( actual, expected );
    }

    return actual.equals( expected ) && actual.hashCode() == expected.hashCode();
  }

  private static boolean areArraysEqual( final Object actualArray, final Object expectedArray ) {
    return IsEqual.areArrayLengthsEqual( actualArray, expectedArray )
            && IsEqual.areArrayElementsEqual( actualArray, expectedArray );
  }

  private static boolean areArrayLengthsEqual( final Object actualArray, final Object expectedArray ) {
    return Array.getLength( actualArray ) == Array.getLength( expectedArray );
  }

  private static boolean areArrayElementsEqual( final Object actualArray, final Object expectedArray ) {
    for ( int i = 0; i < Array.getLength( actualArray ); i++ ) {
      if ( !IsEqual.areEqual( Array.get( actualArray, i ), Array.get( expectedArray, i ) ) ) {
        return false;
      }
    }
    return true;
  }

  private static boolean isArray( final Object o ) {
    return o.getClass().isArray();
  }

  /**
   * Creates a matcher that matches when the examined object is logically equal
   * to the specified <code>operand</code>, as determined by calling the
   * {@link java.lang.Object#equals} method on the <b>examined</b> object.
   * 
   * <p>
   * If the specified operand is <code>null</code> then the created matcher will
   * only match if the examined object's <code>equals</code> method returns
   * <code>true</code> when passed a <code>null</code> (which would be a
   * violation of the <code>equals</code> contract), unless the examined object
   * itself is <code>null</code>, in which case the matcher will return a
   * positive match.
   * </p>
   * 
   * <p>
   * The created matcher provides a special behaviour when examining
   * <code>Array</code>s, whereby it will match if both the operand and the
   * examined object are arrays of the same length and contain items that are
   * equal to each other (according to the above rules) <b>in the same
   * indexes</b>.
   * </p>
   * <p/>
   * For example:
   * 
   * <pre>
   * assertThat( &quot;foo&quot;, equalTo( &quot;foo&quot; ) );
   * assertThat( new String[] { &quot;foo&quot;, &quot;bar&quot; }, equalTo( new String[] { &quot;foo&quot;, &quot;bar&quot; } ) );
   * </pre>
   * 
   * @param operand
   *          the operand
   * @param <T>
   *          the operand type
   */
  @Factory
  public static <T> Matcher<T> equalTo( final T operand ) {
    return new IsEqual<T>( operand );
  }

}
