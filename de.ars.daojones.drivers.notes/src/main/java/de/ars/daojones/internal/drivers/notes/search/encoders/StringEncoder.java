package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.runtime.query.Comparison;
import de.ars.daojones.runtime.query.StringComparison;

public class StringEncoder extends AbstractEncoder<String> {

  @Override
  public Class<? extends String> getKeyType() {
    return String.class;
  }

  @Override
  protected String getKey( final EncoderContext<String> context ) throws QueryLanguageException {
    final Comparison<String> comparison = context.getCriterion().getComparison();
    if ( comparison instanceof StringComparison ) {
      switch ( ( StringComparison ) comparison ) {
      case CONTAINS:
      case CONTAINS_IGNORECASE:
        return "string.literal.contains";
      case STARTSWITH:
      case STARTSWITH_IGNORECASE:
        return "string.literal.starts";
      case ENDSWITH:
      case ENDSWITH_IGNORECASE:
        return "string.literal.ends";
      case LIKE:
      case LIKE_IGNORECASE:
        throw new QueryLanguageException( context.getCriterion(), context.getModel(),
                new UnsupportedOperationException() );
      default:
        return "string.literal.equals";
      }
    } else {
      throw new QueryLanguageException( context.getCriterion(), context.getModel() );
    }
  }

}
