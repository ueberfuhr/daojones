package de.ars.daojones.runtime.test.data;

/**
 * A helper class to create a Java-based test entry model. Use
 * {@link TestModelBuilder} directly.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @see TestModelBuilder
 */
public final class EntryBuilder {

  private final Entry entry;

  private EntryBuilder( final Entry entry ) {
    super();
    this.entry = entry;
  }

  static EntryBuilder newEntry() {
    return new EntryBuilder( new Entry() );
  }

  /**
   * Builds the entry.
   * 
   * @return the entry
   */
  public Entry build() {
    return entry;
  }

  /**
   * Configures the entry to get a given id.
   * 
   * @param id
   *          the id
   * @return the builder (chained-calls)
   */
  public EntryBuilder withId( final String id ) {
    entry.setId( id );
    return this;
  }

  /**
   * Configures the entry to get a given property. The property value can be
   * either the value object (that is used directly) or the string
   * representation (that is read using a corresponding DataHandler).
   * 
   * @param name
   *          the property name
   * @param value
   *          the property value
   * @return the builder (chained-calls)
   */
  public EntryBuilder withProperty( final String name, final Object value ) {
    final Property p = new Property();
    p.setName( name );
    p.setValue( value );
    entry.getProperties().add( p );
    return this;
  }

}
