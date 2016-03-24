package de.ars.daojones.drivers.notes.search;


/**
 * An enumeration of search types that can be executed within Notes databases.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public enum SearchType {

  /**
   * Searching by using formula language.
   */
  FORMULA( "formula" ),
  /**
   * Searching by using full-text search.
   */
  FT_SEARCH( "ftsearch" );

  private final String value;

  private SearchType( final String value ) {
    this.value = value;
  }

  /**
   * Returns a value that can be used for string-based references.
   * 
   * @return
   */
  public String getValue() {
    return value;
  }

}
