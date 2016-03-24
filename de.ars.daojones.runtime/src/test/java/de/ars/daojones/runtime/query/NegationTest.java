package de.ars.daojones.runtime.query;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ars.daojones.test.IsEqual;
import de.ars.daojones.test.IsUnequal;

public class NegationTest {

  @Test
  public void testEqualsAndHashcode() {
    final SearchCriterion c1 = Negation.not( new Tautology() );
    final SearchCriterion c2 = Negation.not( new Tautology() );
    Assert.assertThat( c1, Matchers.is( IsEqual.equalTo( c2 ) ) );
  }

  @Test
  public void testNotEquals() {
    final SearchCriterion c1 = Negation.not( new Tautology() );
    final SearchCriterion c2 = new Tautology();
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

  public void testDoubledNegotiation() {
    final SearchCriterion c1 = new Tautology();
    final SearchCriterion c2 = Negation.not( Negation.not( c1 ) );
    Assert.assertThat( c2, Matchers.is( Matchers.sameInstance( c1 ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testNullArgument() {
    Negation.not( null );
  }

}
