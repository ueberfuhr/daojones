package de.ars.daojones.internal.runtime.test.junit.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Entry;

public class HasEntry extends TypeSafeMatcher<DataSource> {

  private final String id;

  public HasEntry( final String id ) {
    super();
    this.id = id;
  }

  @Override
  public void describeTo( final Description description ) {
    description.appendText( "DataSource has entry <".concat( id ).concat( ">" ) );
  }

  @Override
  protected void describeMismatchSafely( final DataSource item, final Description mismatchDescription ) {
    final StringBuilder sb = new StringBuilder();
    for ( final Entry entry : item.getEntries() ) {
      sb.append( "<" ).append( entry.getId() ).append( '>' );
    }
    mismatchDescription.appendText( "DataSource has entries [".concat( sb.toString() ).concat( "]" ) );
  }

  @Override
  protected boolean matchesSafely( final DataSource item ) {
    for ( final Entry entry : item.getEntries() ) {
      if ( id.equals( entry.getId() ) ) {
        return true;
      }
    }
    return false;
  }

  public static Matcher<DataSource> hasEntry( final String id ) {
    return new HasEntry( id );
  }

}
