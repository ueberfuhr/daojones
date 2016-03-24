package de.ars.daojones.runtime.test.data;

import java.util.Arrays;

import de.ars.daojones.runtime.test.data.DataSource.DataSourceType;

/**
 * A helper class to create a Java-based test data source model. Use
 * {@link TestModelBuilder} directly.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @see TestModelBuilder
 */
public final class DataSourceBuilder {

  private final DataSource ds;

  private DataSourceBuilder( final DataSource ds ) {
    super();
    this.ds = ds;
  }

  static DataSourceBuilder newDataSource( final String name ) {
    return new DataSourceBuilder( new DataSource() ).withName( name );
  }

  /**
   * Builds the data source.
   * 
   * @return the data source
   */
  public DataSource build() {
    return ds;
  }

  /**
   * Configures the data source to get a given name.
   * 
   * @param name
   *          the name
   * @return the builder (chained-calls)
   */
  public DataSourceBuilder withName( final String name ) {
    ds.setName( name );
    return this;
  }

  /**
   * Configures the data source to get a given type.
   * 
   * @param type
   *          the type
   * @return the builder (chained-calls)
   */
  public DataSourceBuilder withType( final DataSourceType type ) {
    ds.setType( type );
    return this;
  }

  /**
   * Configures the data source to get given entries.
   * 
   * @param entries
   *          the entries
   * @return the builder (chained-calls)
   */
  public DataSourceBuilder withEntries( final Entry... entries ) {
    ds.getEntries().addAll( Arrays.asList( entries ) );
    return this;
  }

  /**
   * Configures the data source to get given entries.
   * 
   * @param entries
   *          the entry builders
   * @return the builder (chained-calls)
   */
  public DataSourceBuilder withEntries( final EntryBuilder... entries ) {
    for ( final EntryBuilder builder : entries ) {
      ds.getEntries().add( builder.build() );
    }
    return this;
  }

}
