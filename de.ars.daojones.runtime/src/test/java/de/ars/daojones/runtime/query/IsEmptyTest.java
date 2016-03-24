package de.ars.daojones.runtime.query;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ars.daojones.test.IsEqual;
import de.ars.daojones.test.IsUnequal;

public class IsEmptyTest {

  @Test
  public void testEqualsAndHashcode() {
    final SearchCriterion c1 = new IsEmpty( "feld" );
    final SearchCriterion c2 = new IsEmpty( "feld" );
    Assert.assertThat( c1, Matchers.is( IsEqual.equalTo( c2 ) ) );
  }

  @Test
  public void testNotEqualsAndHashcode() {
    final SearchCriterion c1 = new IsEmpty( "feld" );
    final SearchCriterion c2 = new IsEmpty( "anderesfeld" );
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

}
