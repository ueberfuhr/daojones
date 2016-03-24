package de.ars.daojones.runtime.query;

class SearchCriterionBuilderImpl implements SearchCriterionBuilderInterface {

  /**
   * Cached instance for performance issues.
   */
  public static final ExtensibleSearchCriterion TRUE = new ExtensibleSearchCriterionImpl( new Tautology() );

  /**
   * Cached instance for performance issues.
   */
  public static final ExtensibleSearchCriterion FALSE = new ExtensibleSearchCriterionImpl(
          Negation.not( new Tautology() ) );

  @Override
  public ExtensibleSearchCriterion TRUE() {
    return SearchCriterionBuilderImpl.TRUE;
  }

  @Override
  public ExtensibleSearchCriterion FALSE() {
    return SearchCriterionBuilderImpl.FALSE;
  }

  @Override
  public ExtensibleSearchCriterion not( final SearchCriterion c ) {
    return new ExtensibleSearchCriterionImpl( Negation.not( SearchCriterionBuilder.unwrap( c ) ) );
  }

  @Override
  public SingleFieldSearchCriterionBuilder field( final String field ) {
    return new SingleFieldSearchCriterionBuilderImpl( field );
  }

  @Override
  public ExtensibleSearchCriterion with( final SearchCriterion c ) {
    return new ExtensibleSearchCriterionImpl( SearchCriterionBuilder.unwrap( c ) );
  }

}
