package de.ars.daojones.drivers.notes;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

public class ViewIdentificatorTest {

  @Test
  public void testId() {
    final ViewIdentificator identificator = new ViewIdentificator( "Open/Appointment", new DatabaseIdentificator(
            "myReplica", "myHost" ) );
    final Serializable id = identificator.getId( "myApp" );
    final ViewIdentificator identificator2 = ViewIdentificator.valueOf( id );
    Assert.assertEquals( identificator.getViewName(), identificator2.getViewName() );
    Assert.assertEquals( identificator.getDatabase(), identificator2.getDatabase() );
    Assert.assertEquals( identificator, identificator2 );
  }

}
