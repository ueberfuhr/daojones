package de.ars.daojones.runtime.test.junit;

import javax.naming.ConfigurationException;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;

import de.ars.daojones.internal.runtime.test.junit.matchers.HasEntry;
import de.ars.daojones.internal.runtime.test.junit.matchers.IsMappedTo;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionUtility;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.EntryBuilder;
import de.ars.daojones.runtime.test.data.TestModelBuilder;
import de.ars.daojones.runtime.test.spi.database.TestConnection;

/**
 * This class provides common matchers that can be helpful to verify test
 * results with DaoJones.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class DaoJonesMatchers {

  private DaoJonesMatchers() {
    super();
  }

  /**
   * Returns the data source of a connection.
   * 
   * @param connection
   *          the connection
   * @return the data source
   * @throws AssertionFailedError
   *           if the connection is not a test connection
   */
  @SuppressWarnings( "rawtypes" )
  public static DataSource dataSourceOf( final Connection<?> connection ) throws AssertionFailedError {
    final de.ars.daojones.runtime.spi.database.Connection<?> driver = ConnectionUtility.getDriver( connection );
    if ( driver instanceof TestConnection ) {
      return ( ( TestConnection ) driver ).getDataSource( connection.getBeanModel(), true );
    } else {
      throw new AssertionFailedError( "Connection is not a test connection!" );
    }
  }

  /**
   * Returns the entry within the data source with the given id.
   * 
   * @param dataSource
   *          the data source
   * @param id
   *          the entry id
   * @return the entry
   * @throws AssertionFailedError
   *           if an entry with this id could not be found
   */
  public static Entry entry( final DataSource dataSource, final String id ) throws AssertionFailedError {
    for ( final Entry entry : dataSource.getEntries() ) {
      if ( id.equals( entry.getId() ) ) {
        return entry;
      }
    }
    throw new AssertionFailedError( "Entry with id \"".concat( id ).concat( "\" could not be found within data source" ) );
  }

  /**
   * Creates a matcher that matches if the examined entry id exists within the
   * data source.
   * <p/>
   * For example:
   * 
   * <pre>
   * <i>assertThat</i>("myDataSource", <i>hasEntry</i>("myId"))
   * </pre>
   * 
   * @param id
   *          the entry id
   * @return the matcher
   */
  public static Matcher<DataSource> hasEntry( final String id ) {
    return HasEntry.hasEntry( id );
  }

  /**
   * Creates a matcher that matches if the examined entry contains the values
   * that correspond to the given bean.
   * 
   * @param entry
   *          the entry
   * @return the matcher
   */
  public static Matcher<Identificator> isMappedTo( final Entry entry ) {
    return IsMappedTo.isMappedTo( entry );
  }

  /**
   * Creates a matcher that matches if the examined entry contains the values
   * that correspond to the given bean.
   * 
   * @param entry
   *          the entry builder
   * @return the matcher
   */
  public static Matcher<Identificator> isMappedTo( final EntryBuilder entry ) {
    return DaoJonesMatchers.isMappedTo( entry.build() );
  }

  /**
   * Returns an object that can be used for assertions concerning field values
   * of the entry.
   * 
   * @param entry
   *          the entry
   * @param connection
   *          the connection
   * @return the accessor object
   * @throws ConfigurationException
   * @see {@link #hasField(String)}
   */
  public static DatabaseAccessor entry( final Entry entry, final Connection<?> connection )
          throws ConfigurationException {
    return TestModelBuilder.newAccessor( entry, connection );
  }

  /**
   * Creates a matcher that matches if the database accessor has the named
   * field. Use the methods of this matcher to create matchers that can check
   * the field's value.
   * 
   * @param name
   *          the field name
   * @return the matcher
   */
  public static HasField hasField( final String name ) {
    return HasField.hasField( name );
  }
}
