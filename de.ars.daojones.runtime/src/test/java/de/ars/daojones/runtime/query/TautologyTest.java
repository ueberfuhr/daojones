package de.ars.daojones.runtime.query;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ars.daojones.test.IsEqual;

public class TautologyTest {

  @Test
  public void testEqualsAndHashcode() {
    final SearchCriterion c1 = new Tautology();
    final SearchCriterion c2 = new Tautology();
    Assert.assertThat( c1, Matchers.is( IsEqual.equalTo( c2 ) ) );
  }

}
