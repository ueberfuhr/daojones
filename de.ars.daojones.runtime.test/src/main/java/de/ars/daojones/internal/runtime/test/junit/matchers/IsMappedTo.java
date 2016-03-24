package de.ars.daojones.internal.runtime.test.junit.matchers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import de.ars.daojones.internal.runtime.test.IdentificatorImpl;
import de.ars.daojones.runtime.beans.identification.ApplicationDependentIdentificator;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.Property;

public class IsMappedTo extends TypeSafeMatcher<Identificator> {

  private final Entry entry;

  public IsMappedTo( final Entry entry ) {
    super();
    this.entry = entry;
  }

  private static String entriesToString( final Collection<Entry> entries ) {
    if ( entries.size() == 1 ) {
      return IsMappedTo.entryToString( entries.iterator().next() );
    } else {
      final StringBuilder sb = new StringBuilder();
      sb.append( '{' );
      boolean first = true;
      for ( final Entry e : entries ) {
        sb.append( IsMappedTo.entryToString( e ) );
        if ( first ) {
          sb.append( ", " );
        }
        first = false;
      }
      sb.append( '}' );
      return sb.toString();
    }
  }

  private static String entryToString( final Entry entry ) {
    final StringBuilder sb = new StringBuilder();
    final Map<String, Object> entries = new TreeMap<String, Object>();
    for ( final Property p : entry.getProperties() ) {
      entries.put( p.getName(), p.getValue() );
    }
    sb.append( entries.toString() );
    return sb.toString();
  }

  @Override
  public void describeTo( final Description description ) {
    description.appendText( "Bean <-- mapped to --> [".concat( IsMappedTo.entryToString( entry ) ).concat( "...]" ) );
  }

  @Override
  protected void describeMismatchSafely( final Identificator item, final Description mismatchDescription ) {
    mismatchDescription.appendText( "Bean <-- mapped to -->  [".concat(
            IsMappedTo.entriesToString( IsMappedTo.entriesFromIdentificator( item ) ) ).concat( "]" ) );
  }

  private static Collection<Entry> entriesFromIdentificator( final Identificator id ) {
    if ( id instanceof ApplicationDependentIdentificator ) {
      final ApplicationDependentIdentificator adi = ( ApplicationDependentIdentificator ) id;
      final Collection<Entry> result = new HashSet<Entry>();
      for ( final Map.Entry<String, Identificator> e : adi ) {
        result.addAll( IsMappedTo.entriesFromIdentificator( e.getValue() ) );
      }
      result.remove( null );
      return result;
    } else if ( id instanceof IdentificatorImpl ) {
      return Arrays.asList( ( ( IdentificatorImpl ) id ).getEntry() );
    } else {
      return null;
    }
  }

  @Override
  protected boolean matchesSafely( final Identificator item ) {
    final Collection<Entry> entries = IsMappedTo.entriesFromIdentificator( item );
    return entries.contains( entry );
  }

  public static Matcher<Identificator> isMappedTo( final Entry entry ) {
    return new IsMappedTo( entry );
  }

}
