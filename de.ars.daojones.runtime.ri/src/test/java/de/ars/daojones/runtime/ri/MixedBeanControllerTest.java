package de.ars.daojones.runtime.ri;

import java.util.Collection;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterionBuilder;
import de.ars.daojones.runtime.test.junit.Config;
import de.ars.daojones.runtime.test.junit.ConfigType;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.DaoJonesMatchers;

@RunWith( DaoJones.class )
public class MixedBeanControllerTest {

  private final ConnectionProvider cp;
  private final MixedBeanController controller;

  public MixedBeanControllerTest( final ConnectionProvider cp ) throws DataAccessException {
    this.cp = cp;
    controller = new MixedBeanController( cp );
  }

  @Test
  public void testDeleteAllGenerated() throws DataAccessException {
    boolean atLeastOneGenerated = false;
    for ( final MixedBean b : controller.readAllBeans() ) {
      atLeastOneGenerated = atLeastOneGenerated || b.isGenerated();
    }
    Assert.assertThat( atLeastOneGenerated, Matchers.is( true ) );
    controller.deleteAllGenerated();
    atLeastOneGenerated = false;
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    Assert.assertThat( DaoJonesMatchers.dataSourceOf( con ), Matchers.not( DaoJonesMatchers.hasEntry( "entry2" ) ) );
  }

  @Test
  public void testCreateBeans() throws DataAccessException {
    final Collection<MixedBean> beansBefore = controller.readAllBeans();
    controller.createBeans();
    final Collection<MixedBean> beansAfter = controller.readAllBeans();
    Assert.assertThat( beansAfter.size(), Matchers.is( Matchers.greaterThan( beansBefore.size() ) ) );
  }

  @Test
  public void testDateField() throws DataAccessException {
    final String name = "testDateField";
    final Date date = new Date();
    final MixedBean mb = new MixedBean( name );
    mb.setDatefield( date );
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    try {
      con.update( mb );
      final MixedBean loadedBean = con.find( Query.create().only(
              SearchCriterionBuilder.field( MixedBean.NAME ).asString().isEqualTo( name ) ) );
      Assert.assertThat( loadedBean, Matchers.is( Matchers.notNullValue( MixedBean.class ) ) );
      Assert.assertThat( loadedBean.getDatefield(), Matchers.is( Matchers.equalTo( date ) ) );
    } finally {
      con.close();
    }

  }

  @Test
  @Config( value = "/META-INF/daojones-connections.xml", type = ConfigType.CONNECTIONS )
  public void testCreateBeansWithOriginalFile() throws DataAccessException {
    // The original connection configuration is used for this method.
    testCreateBeans();
  }

}
