package de.ars.daojones.drivers.notes.types;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class AddressTest {

  @Test
  public void testSimpleAddress() {
    final String test = "test@test.com";
    final Address address = new Address( test );
    final String testToString = address.toString();
    Assert.assertThat( testToString, Matchers.is( Matchers.equalTo( test ) ) );
    final Address parsedAddress = Address.valueOf( testToString );
    Assert.assertThat( parsedAddress, Matchers.is( Matchers.equalTo( address ) ) );
    Assert.assertThat( parsedAddress.isSimple(), Matchers.is( Matchers.equalTo( address.isSimple() ) ) );
    Assert.assertThat( parsedAddress.getAddress(), Matchers.is( Matchers.equalTo( address.getAddress() ) ) );

  }

  @Test
  public void testAddress() {
    final String test = "test@test.com";
    final String phrase = "Max Mustermann";
    final String comment1 = "Employee";
    final String comment2 = "Trainer";
    final Address address = new Address( test );
    address.setPhrase( phrase );
    address.setComments( comment1, comment2 );
    final String testToString = address.toString();
    Assert.assertThat( testToString,
            Matchers.is( Matchers.equalTo( "\"Max Mustermann\" <test@test.com> (Employee) (Trainer)" ) ) );
    final Address parsedAddress = Address.valueOf( testToString );
    Assert.assertThat( parsedAddress.isSimple(), Matchers.is( Matchers.equalTo( address.isSimple() ) ) );
    Assert.assertThat( parsedAddress.getAddress(), Matchers.is( Matchers.equalTo( address.getAddress() ) ) );
    Assert.assertThat( parsedAddress.getPhrase(), Matchers.is( Matchers.equalTo( address.getPhrase() ) ) );
    Assert.assertThat( parsedAddress.getComments(), Matchers.is( Matchers.arrayContaining( address.getComments() ) ) );
    Assert.assertThat( parsedAddress, Matchers.is( Matchers.equalTo( address ) ) );
  }

}
