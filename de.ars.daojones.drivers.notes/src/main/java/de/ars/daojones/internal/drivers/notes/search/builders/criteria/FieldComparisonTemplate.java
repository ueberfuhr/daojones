package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import de.ars.daojones.drivers.notes.search.ComparisonBuilder;
import de.ars.daojones.drivers.notes.search.Encoder;
import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.AwareNotFoundException;
import de.ars.daojones.internal.drivers.notes.search.SearchTypeAwareRegistry;
import de.ars.daojones.runtime.query.Comparison;
import de.ars.daojones.runtime.query.FieldComparison;

public class FieldComparisonTemplate<X> implements QueryLanguageBuilder<FieldComparison<X>> {

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private final SearchTypeAwareRegistry<Comparison<Object>, ComparisonBuilder<Object>> comparisons = new SearchTypeAwareRegistry<Comparison<Object>, ComparisonBuilder<Object>>(
          ( Class ) ComparisonBuilder.class );
  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private final SearchTypeAwareRegistry<Object, Encoder<Object>> encoders = new SearchTypeAwareRegistry<Object, Encoder<Object>>(
          ( Class ) Encoder.class );

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Class<? extends FieldComparison<X>> getKeyType() {
    return ( Class ) FieldComparison.class;
  }

  @Override
  public SearchType getSearchType() {
    return null;
  }

  @Override
  public void createQuery( final StringBuilder buffer, final QueryContext<FieldComparison<X>> context )
          throws QueryLanguageException {
    try {
      @SuppressWarnings( "unchecked" )
      final ComparisonBuilder<X> builder = ( ComparisonBuilder<X> ) comparisons.findAwareNotNull(
              ( Class<? extends Comparison<Object>> ) context.getCriterion().getComparison().getClass(),
              context.getSearchType() );
      builder.createQuery( buffer, new ComparisonContextImpl( context ) );
    } catch ( final AwareNotFoundException e ) {
      throw new QueryLanguageException( context.getCriterion(), context.getModel(), e );
    }
  }

  private class ComparisonContextImpl implements ComparisonBuilder.ComparisonContext<X> {

    private final QueryContext<FieldComparison<X>> delegate;

    public ComparisonContextImpl( final QueryContext<FieldComparison<X>> delegate ) {
      super();
      this.delegate = delegate;
    }

    @Override
    public QueryContext<FieldComparison<X>> getQueryContext() {
      return delegate;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <E> Encoder<E> getEncoder( final Class<E> type ) throws QueryLanguageException {
      try {
        return ( Encoder<E> ) FieldComparisonTemplate.this.encoders.findAwareNotNull( type, getQueryContext()
                .getSearchType() );
      } catch ( final AwareNotFoundException e ) {
        throw new QueryLanguageException( getQueryContext().getCriterion(), getQueryContext().getModel(), e );
      }
    }

  }

}
