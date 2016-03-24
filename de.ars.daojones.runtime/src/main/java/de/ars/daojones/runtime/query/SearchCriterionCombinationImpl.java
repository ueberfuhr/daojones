package de.ars.daojones.runtime.query;

class SearchCriterionCombinationImpl implements SearchCriterionCombination {

  private final SearchCriterion c1;
  private final LogicalCombinationType combination;

  public SearchCriterionCombinationImpl( final SearchCriterion c, final LogicalCombinationType combination ) {
    super();
    c1 = SearchCriterionBuilder.unwrap( c );
    this.combination = combination;
  }

  /**
   * @return the c1
   */
  protected SearchCriterion getCriterion() {
    return c1;
  }

  /**
   * @return the combination
   */
  protected LogicalCombinationType getCombination() {
    return combination;
  }

  @Override
  public ExtensibleSearchCriterion TRUE() {
    return new ExtensibleSearchCriterionImpl( new LogicalCombination( c1, combination,
            SearchCriterionBuilder.unwrap( SearchCriterionBuilderImpl.TRUE ) ) );
  }

  @Override
  public ExtensibleSearchCriterion FALSE() {
    return new ExtensibleSearchCriterionImpl( new LogicalCombination( c1, combination,
            SearchCriterionBuilder.unwrap( SearchCriterionBuilderImpl.FALSE ) ) );
  }

  @Override
  public ExtensibleSearchCriterion not( final SearchCriterion c ) {
    final SearchCriterion c2 = Negation.not( SearchCriterionBuilder.unwrap( c ) );
    return new ExtensibleSearchCriterionImpl( new LogicalCombination( c1, combination, c2 ) );
  }

  @Override
  public ExtensibleSearchCriterion with( final SearchCriterion c ) {
    return new ExtensibleSearchCriterionImpl( new LogicalCombination( c1, combination,
            SearchCriterionBuilder.unwrap( c ) ) );
  }

  @Override
  public SingleFieldSearchCriterionBuilder field( final String field ) {
    return new SinglePropertySearchCriterionBuilderLogicalCombinationImpl( field );
  }

  private class SinglePropertySearchCriterionBuilderLogicalCombinationImpl extends
          SingleFieldSearchCriterionBuilderImpl {

    public SinglePropertySearchCriterionBuilderLogicalCombinationImpl( final String field ) {
      super( field );
    }

    @Override
    protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
      return new LogicalCombination( c1, combination, SearchCriterionBuilder.unwrap( c ) );
    }

  }

}