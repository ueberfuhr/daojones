package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.Encoder;
import de.ars.daojones.drivers.notes.search.EncoderProvider;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

abstract class AbstractEncoder<T> implements Encoder<T> {

  private static final Messages templates = Messages.create( "search.builders.templates" );

  /**
   * Returns the value of the template with the place holders replaced by the
   * parameter values. Parameters are of type search criterion. <b>Please
   * note:</b> In case of creating custom templates for custom criteria, you
   * should overwrite this method and refer to your custom resource bundle.
   * 
   * @param context
   *          the encoder context
   * @param key
   *          the key
   * @param params
   *          the parameters
   * @return the value of the template
   */
  protected String getTemplate( final EncoderContext<T> context, final String key, final Object... params ) {
    final SearchType searchType = context.getSearchType();
    return AbstractEncoder.templates.get( searchType.getValue().concat( "." ).concat( key ), params );
  }

  @Override
  public SearchType getSearchType() {
    return null;
  }

  @Override
  public String encodeNull( final EncoderContext<T> context ) throws QueryLanguageException {
    final DataSourceType type = context.getModel().getTypeMapping().getType();
    switch ( type ) {
    case VIEW:
      // null value cannot be searched within a view
      throw new QueryLanguageException( context.getCriterion(), context.getModel(), new IllegalArgumentException(
              type.name() ) );
    default:
      return getTemplate( context, "nullvalue" );
    }
  }

  @Override
  public String encodeLiteral( final EncoderContext<T> context, final EncoderProvider provider, final T value )
          throws QueryLanguageException {
    return getTemplate( context, getKey( context ), value );
  }

  /**
   * Returns the key within the templates file.
   * 
   * @param context
   *          the encoder context
   * @return the key within the templates file
   * @throws QueryLanguageException
   */
  protected abstract String getKey( final EncoderContext<T> context ) throws QueryLanguageException;

}
