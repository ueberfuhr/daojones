package de.ars.daojones.runtime.query;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class SearchCriterionBuilderTest {

  /**
   * Tests the search criterion builder for always returning unwrapped search
   * criteria.
   */
  @Test
  public void testUnwrapped() {
    SearchCriterion sc = SearchCriterionBuilder.with( SearchCriterionBuilder.TRUE() ).or().FALSE().and().field( "test" )
            .asString().contains( "test" ).or().not( SearchCriterionBuilder.FALSE() );
    sc = SearchCriterionBuilder.unwrap( sc );
    sc.accept( new SearchCriterionVisitor() {

      @Override
      public boolean preVisit( final SearchCriterion criterion ) {
        if ( criterion instanceof SearchCriterionWrapper ) {
          throw new AssertionFailedError( "Wrapped search criterion not allowed! (" + criterion + ")" );
        }
        return super.preVisit( criterion );
      }

    } );
  }

  @Test
  public void testIsEmptyOrAnythingElse() {
    final String testField = "testField";
    final String testValue = "testValue";
    final SearchCriterion sc = SearchCriterionBuilder.field( testField ).isEmpty().or().asString()
            .isEqualTo( testValue ).getUnwrapped();
    final SearchCriterion scToCompare = LogicalCombination.toSearchCriterion( LogicalCombinationType.OR, new IsEmpty(
            testField ), new FieldComparison<String>( testField, StringComparison.EQUALS, testValue ) );
    Assert.assertThat( sc, Matchers.is( Matchers.equalTo( scToCompare ) ) );
  }
}
