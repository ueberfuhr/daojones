package de.ars.daojones.runtime.query;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ars.daojones.test.IsEqual;
import de.ars.daojones.test.IsUnequal;

public class LogicalCombinationTest {

  @Test
  public void testEqualsHashCode() {
    final SearchCriterion left = new Tautology();
    final SearchCriterion right = new IsEmpty( "feld" );
    final SearchCriterion c1 = new LogicalCombination( left, LogicalCombinationType.AND, right );
    final SearchCriterion c2 = new LogicalCombination( left, LogicalCombinationType.AND, right );
    Assert.assertThat( c1, Matchers.is( IsEqual.equalTo( c2 ) ) );
  }

  @Test
  public void testEqualsHashCodeLeftRight() {
    final SearchCriterion left = new Tautology();
    final SearchCriterion right = new IsEmpty( "feld" );
    final SearchCriterion c1 = new LogicalCombination( left, LogicalCombinationType.AND, right );
    final SearchCriterion c2 = new LogicalCombination( right, LogicalCombinationType.AND, left );
    Assert.assertThat( c1, Matchers.is( IsEqual.equalTo( c2 ) ) );
  }

  @Test
  public void testNotEqualsHashCodeRight() {
    final SearchCriterion left = new Tautology();
    final SearchCriterion right1 = new IsEmpty( "feld" );
    final SearchCriterion right2 = new IsEmpty( "feld2" );
    final SearchCriterion c1 = new LogicalCombination( left, LogicalCombinationType.AND, right1 );
    final SearchCriterion c2 = new LogicalCombination( left, LogicalCombinationType.AND, right2 );
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

  @Test
  public void testNotEqualsHashCodeCombination() {
    final SearchCriterion left = new Tautology();
    final SearchCriterion right = new IsEmpty( "feld" );
    final SearchCriterion c1 = new LogicalCombination( left, LogicalCombinationType.AND, right );
    final SearchCriterion c2 = new LogicalCombination( left, LogicalCombinationType.OR, right );
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

}
