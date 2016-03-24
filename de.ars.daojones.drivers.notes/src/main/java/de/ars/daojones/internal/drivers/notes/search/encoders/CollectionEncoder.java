package de.ars.daojones.internal.drivers.notes.search.encoders;

import java.util.Collection;
import java.util.Date;

import de.ars.daojones.drivers.notes.search.Encoder;
import de.ars.daojones.drivers.notes.search.EncoderProvider;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.BooleanComparison;
import de.ars.daojones.runtime.query.CollectionComparison;
import de.ars.daojones.runtime.query.Comparison;
import de.ars.daojones.runtime.query.DateComparison;
import de.ars.daojones.runtime.query.FieldComparison;
import de.ars.daojones.runtime.query.NumberComparison;
import de.ars.daojones.runtime.query.StringComparison;

public class CollectionEncoder extends AbstractEncoder<Collection<?>> {

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Class<? extends Collection<?>> getKeyType() {
    return ( Class ) Collection.class;
  }

  @Override
  protected String getKey( final EncoderContext<Collection<?>> context ) throws QueryLanguageException {
    return "collection.format";
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public String encodeLiteral( final EncoderContext<Collection<?>> context, final EncoderProvider provider,
          final Collection<?> values ) throws QueryLanguageException {
    final FieldComparison<Collection<?>> sc = context.getCriterion();
    if ( values.isEmpty() ) {
      return provider.getEncoder( String.class ).encodeLiteral( new EncoderContext<String>() {

        @Override
        public SearchType getSearchType() {
          return context.getSearchType();
        }

        @Override
        public Bean getModel() {
          return context.getModel();
        }

        @Override
        public FieldComparison<String> getCriterion() {
          return new FieldComparison<String>( sc.getField(), StringComparison.EQUALS, "" );
        }
      }, provider, "" );
    } else {
      final StringBuffer sb = new StringBuffer();
      final String sep = getTemplate( context, "collection.format.separator" );
      boolean first = true;
      for ( final Object value : values ) {
        if ( !first ) {
          sb.append( sep );
        }
        final Comparison<?> comparison;
        // find EQUALS comparison for each type
        if ( value instanceof String ) {
          comparison = StringComparison.EQUALS;
        } else if ( value instanceof Boolean ) {
          comparison = BooleanComparison.EQUALS;
        } else if ( value instanceof Collection ) {
          comparison = CollectionComparison.EQUALS;
        } else if ( value instanceof Date ) {
          comparison = DateComparison.TIME_EQUALS;
        } else if ( value instanceof Number ) {
          comparison = NumberComparison.EQUALS;
        } else {
          comparison = null;
        }
        final Encoder<Object> encoder = provider.getEncoder( ( Class<Object> ) value.getClass() );
        final String literal = encoder.encodeLiteral( new EncoderContext<Object>() {

          @Override
          public SearchType getSearchType() {
            return context.getSearchType();
          }

          @Override
          public Bean getModel() {
            return context.getModel();
          }

          @SuppressWarnings( "rawtypes" )
          @Override
          public FieldComparison<Object> getCriterion() {
            if ( null == comparison ) {
              throw new UnsupportedOperationException( new QueryLanguageException( sc, context.getModel() ) );
            } else {
              return new FieldComparison<Object>( sc.getField(), ( Comparison ) comparison, value );
            }
          }
        }, provider, value );
        sb.append( literal );
        first = false;
      }
      return getTemplate( context, getKey( context ), sb.toString() );
    }
  }
}
