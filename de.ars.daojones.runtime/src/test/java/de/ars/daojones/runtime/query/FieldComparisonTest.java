package de.ars.daojones.runtime.query;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.ars.daojones.test.IsEqual;
import de.ars.daojones.test.IsUnequal;

public class FieldComparisonTest {

  @Test
  public void testEqualsHashCode() {
    final SearchCriterion c1 = new FieldComparison<String>( "feld", StringComparison.CONTAINS, "wert" );
    final SearchCriterion c2 = new FieldComparison<String>( "feld", StringComparison.CONTAINS, "wert" );
    Assert.assertThat( c1, Matchers.is( IsEqual.equalTo( c2 ) ) );
  }

  @Test
  public void testNotEqualsHashCodeField() {
    final SearchCriterion c1 = new FieldComparison<String>( "feld", StringComparison.CONTAINS, "wert" );
    final SearchCriterion c2 = new FieldComparison<String>( "feld2", StringComparison.CONTAINS, "wert" );
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

  @Test
  public void testNotEqualsHashCodeComparison() {
    final SearchCriterion c1 = new FieldComparison<String>( "feld", StringComparison.CONTAINS, "wert" );
    final SearchCriterion c2 = new FieldComparison<String>( "feld", StringComparison.ENDSWITH, "wert" );
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

  @Test
  public void testNotEqualsHashCodeValue() {
    final SearchCriterion c1 = new FieldComparison<String>( "feld", StringComparison.CONTAINS, "wert" );
    final SearchCriterion c2 = new FieldComparison<String>( "feld", StringComparison.CONTAINS, "wert2" );
    Assert.assertThat( c1, Matchers.is( IsUnequal.unequalTo( c2 ) ) );
  }

}
