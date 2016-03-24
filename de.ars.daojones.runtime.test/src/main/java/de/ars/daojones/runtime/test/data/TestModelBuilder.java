package de.ars.daojones.runtime.test.data;

import javax.naming.ConfigurationException;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionUtility;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;
import de.ars.daojones.runtime.test.spi.database.TestConnection;

/**
 * A helper class to create a Java-based test model.<br/>
 * <br/>
 * <b>Example of usage:</b><br/>
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
 */
public final class TestModelBuilder {

  private TestModelBuilder() {
    super();
  }

  /**
   * Creates an entry.
   * 
   * @return the entry builder
   */
  public static EntryBuilder newEntry() {
    return EntryBuilder.newEntry();
  }

  /**
   * Creates a data source.
   * 
   * @param name
   *          the name of the data source
   * @return the data source
   */
  public static DataSourceBuilder newDataSource( final String name ) {
    return DataSourceBuilder.newDataSource( name );
  }

  /**
   * Creates a database accessor for the given entry. This can be used to read
   * out an entry's values with a custom data type.
   * 
   * @param model
   *          the entry
   * @param connection
   *          the connection
   * @return the database accessor
   * @throws ConfigurationException
   */
  public static DatabaseAccessor newAccessor( final Entry model, final Connection<?> connection )
          throws ConfigurationException {
    final de.ars.daojones.runtime.spi.database.Connection<?> driver = ConnectionUtility.getDriver( connection );
    if ( driver instanceof TestConnection ) {
      final BeanModel beanModel = connection.getBeanModel();
      final DatabaseEntry entry = ( ( TestConnection<?> ) driver ).createDatabaseEntry( model, beanModel );
      return entry;
    } else {
      throw new ConfigurationException();
    }
  }

}
