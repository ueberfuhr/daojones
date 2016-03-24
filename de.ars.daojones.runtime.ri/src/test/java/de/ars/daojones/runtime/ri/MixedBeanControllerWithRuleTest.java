package de.ars.daojones.runtime.ri;

import java.util.Collection;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.test.junit.Config;
import de.ars.daojones.runtime.test.junit.ConfigType;
import de.ars.daojones.runtime.test.junit.DaoJones;

public class MixedBeanControllerWithRuleTest {

  @Rule
  public DaoJones.Rule dj = DaoJones.asRule( this );

  private MixedBeanController controller;

  @Before
  public void init() throws DataAccessException {
    controller = new MixedBeanController( dj.getConnectionProvider() );
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
    for ( final MixedBean b : controller.readAllBeans() ) {
      atLeastOneGenerated = atLeastOneGenerated || b.isGenerated();
    }
    Assert.assertThat( atLeastOneGenerated, Matchers.is( false ) );
  }

  @Test
  public void testCreateBeans() throws DataAccessException {
    final Collection<MixedBean> beansBefore = controller.readAllBeans();
    controller.createBeans();
    final Collection<MixedBean> beansAfter = controller.readAllBeans();
    Assert.assertThat( beansAfter.size(), Matchers.is( Matchers.greaterThan( beansBefore.size() ) ) );
  }

  @Test
  @Config( value = "/META-INF/daojones-connections.xml", type = ConfigType.CONNECTIONS )
  public void testCreateBeansWithOriginalFile() throws DataAccessException {
    // The original connection configuration is used for this method.
    testCreateBeans();
  }

}
