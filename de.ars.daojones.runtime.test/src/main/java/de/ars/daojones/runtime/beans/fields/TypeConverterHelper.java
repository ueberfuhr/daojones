package de.ars.daojones.runtime.beans.fields;

/**
 * A helper class to get information about a type converter.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public final class TypeConverterHelper {

  private TypeConverterHelper() {
    super();
  }

  /**
   * Returns the database type that the converter converts to or from.
   * 
   * @param converter
   *          the converter
   * @param context
   *          the context
   * @param <D>
   *          the database type
   * @param <P>
   *          the Java property type
   * @return the database type
   */
  public static <D, P> Class<D> getDatabaseType( final TypeConverter<D, P> converter, final ConverterContext context ) {
    return converter.getDatabaseType( context );
  }

}
