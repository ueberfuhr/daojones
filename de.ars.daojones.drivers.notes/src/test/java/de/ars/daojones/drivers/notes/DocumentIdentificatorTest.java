package de.ars.daojones.drivers.notes;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

public class DocumentIdentificatorTest {

  @Test
  public void testId() {
    final DocumentIdentificator identificator = new DocumentIdentificator( "1234567890", new DatabaseIdentificator(
            "myReplica", "myHost" ) );
    final Serializable id = identificator.getId( "myApp" );
    final DocumentIdentificator identificator2 = DocumentIdentificator.valueOf( id );
    Assert.assertEquals( identificator.getUniversalId(), identificator2.getUniversalId() );
    Assert.assertEquals( identificator.getDatabase(), identificator2.getDatabase() );
    Assert.assertEquals( identificator, identificator2 );
  }

}
