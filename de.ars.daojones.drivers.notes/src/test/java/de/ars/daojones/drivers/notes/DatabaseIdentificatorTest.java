package de.ars.daojones.drivers.notes;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseIdentificatorTest {

  @Test
  public void testId() {
    final DatabaseIdentificator identificator = new DatabaseIdentificator( "myReplica", "myHost" );
    final Serializable id = identificator.getId( "myApp" );
    final DatabaseIdentificator identificator2 = DatabaseIdentificator.valueOf( id );
    Assert.assertEquals( identificator.getReplica(), identificator2.getReplica() );
    Assert.assertEquals( identificator.getHost(), identificator2.getHost() );
    Assert.assertEquals( identificator, identificator2 );
  }

  @Test
  public void testIdNoHost() {
    final DatabaseIdentificator identificator = new DatabaseIdentificator( "myReplica", null );
    final Serializable id = identificator.getId( "myApp" );
    final DatabaseIdentificator identificator2 = DatabaseIdentificator.valueOf( id );
    Assert.assertEquals( identificator.getReplica(), identificator2.getReplica() );
    Assert.assertEquals( identificator.getHost(), identificator2.getHost() );
    Assert.assertEquals( identificator, identificator2 );
  }

}
