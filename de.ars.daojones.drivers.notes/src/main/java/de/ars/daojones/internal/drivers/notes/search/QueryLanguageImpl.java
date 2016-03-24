package de.ars.daojones.internal.drivers.notes.search;

import de.ars.daojones.drivers.notes.search.QueryLanguage;
import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder;
import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.AwareNotFoundException;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.SearchCriterionWrapper;

public class QueryLanguageImpl implements QueryLanguage {

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private final SearchTypeAwareRegistry<SearchCriterion, QueryLanguageBuilder<SearchCriterion>> builders = new SearchTypeAwareRegistry<SearchCriterion, QueryLanguageBuilder<SearchCriterion>>(
          ( Class ) QueryLanguageBuilder.class );

  @Override
  public void createQuery( final StringBuilder sb, final Query query, final Bean model ) throws QueryLanguageException {
    final SearchCriterion criterion = query.getCriterion();
    final SearchCriterion unwrapped;
    if ( criterion instanceof SearchCriterionWrapper ) {
      unwrapped = ( ( SearchCriterionWrapper ) criterion ).getUnwrapped();
    } else {
      unwrapped = criterion;
    }
    final QueryContext<SearchCriterion> context = new QueryContextImpl<SearchCriterion>( query, this, unwrapped, model );
    try {
      final QueryLanguageBuilder<SearchCriterion> builder = builders.findAwareNotNull( unwrapped.getClass(),
              context.getSearchType() );
      builder.createQuery( sb, context );
    } catch ( final AwareNotFoundException e ) {
      throw new QueryLanguageException( unwrapped, model, e );
    }
  }

  private static class QueryContextImpl<T extends SearchCriterion> implements QueryLanguageBuilder.QueryContext<T> {

    private final Query query;
    private final QueryLanguage language;
    private final T criterion;
    private final Bean model;

    public QueryContextImpl( final Query query, final QueryLanguage language, final T criterion, final Bean model ) {
      super();
      this.query = query;
      this.language = language;
      this.criterion = criterion;
      this.model = model;
    }

    @Override
    public QueryLanguage getLanguage() {
      return language;
    }

    @Override
    public T getCriterion() {
      return criterion;
    }

    @Override
    public Bean getModel() {
      return model;
    }

    @Override
    public Query getQuery() {
      return query;
    }

    @Override
    public SearchType getSearchType() {
      return SearchTypeHelper.getSearchType( getQuery(), getModel().getTypeMapping().getType() );
    }
  }

}
