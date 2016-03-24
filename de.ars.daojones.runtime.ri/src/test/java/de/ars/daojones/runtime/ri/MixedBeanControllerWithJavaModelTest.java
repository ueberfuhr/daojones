package de.ars.daojones.runtime.ri;

import java.util.Collection;

import javax.naming.ConfigurationException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.DataSource.DataSourceType;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.TestModelBuilder;
import de.ars.daojones.runtime.test.junit.Config;
import de.ars.daojones.runtime.test.junit.ConfigDriver;
import de.ars.daojones.runtime.test.junit.ConfigType;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.DaoJonesMatchers;
import de.ars.daojones.runtime.test.junit.Inject;
import de.ars.daojones.runtime.test.junit.TestModel;
import de.ars.daojones.runtime.test.notes.NotesTestModel;

@RunWith( DaoJones.class )
@ConfigDriver( NotesDriverConfiguration.DRIVER_ID )
public class MixedBeanControllerWithJavaModelTest {

  private static final String ENTRY1 = "entry1";

  /*
   * Be aware:
   * =========
   * The following instances should not be declared as 'static'
   * because of test isolation concerns.
   */

  // Document 1
  private final Entry entry1 = TestModelBuilder.newEntry() //
          .withId( MixedBeanControllerWithJavaModelTest.ENTRY1 ) //
          .withProperty( "name", "Entry 1" ) //
          .withProperty( "stringfield", "TestString" ) //
          .build();
  // Document 2
  private final Entry entry2 = TestModelBuilder.newEntry() //
          .withId( "entry2" ) //
          .withProperty( "name", "Entry 2" ) //
          .withProperty( "generated", "true" ) //
          .build();
  // Document 3
  private final Entry entry3 = TestModelBuilder.newEntry() //
          .withId( "entry3" ) //
          .withProperty( "name", "Entry 3" ) //
          .build();

  // Documents (DataSource)
  @TestModel( application = "test-app" )
  private final DataSource ds = TestModelBuilder.newDataSource( "MixedForm" ) //
          .withEntries( entry1, entry2, entry3 ) //
          .build();

  // View Entry 1
  private final Entry viewEntry1 = TestModelBuilder.newEntry() //
          .withId( "viewentry1" ) //
          .withProperty( "?1", "Entry 1" ) //
          .withProperty( NotesTestModel.DOCUMENT_MAPPING_PROPERTY, MixedBeanControllerWithJavaModelTest.ENTRY1 ) //
          .build();
  // View Entry 2
  private final Entry viewEntry2 = TestModelBuilder.newEntry() //
          .withId( "viewentry2" ) //
          .withProperty( "?1", "Entry 2" ) //
          .build();
  @TestModel( application = "test-app" )
  private final DataSource dsView = TestModelBuilder.newDataSource( "MixedFormsByName" ) //
          .withType( DataSourceType.VIEW ) //
          .withEntries( viewEntry1, viewEntry2 ) //
          .build();

  @Inject( application = "test-app" )
  private ConnectionProvider cp;
  private MixedBeanController controller;

  @Before
  public void init() throws DataAccessException {
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
    Assert.assertThat( ds, Matchers.not( DaoJonesMatchers.hasEntry( "entry2" ) ) );
  }

  @Test
  public void testReadAllBeansFromViewByName() throws DataAccessException {
    final Collection<MixedBeanFromView> result = controller.readAllBeansFromViewByName( "Entry 1" );
    Assert.assertThat( result.size(), Matchers.is( Matchers.equalTo( 1 ) ) );
    final MixedBeanFromView bean = result.iterator().next();
    controller.readSingleBeanFromView( bean.getId() );
    Assert.assertThat( bean.getId(), DaoJonesMatchers.isMappedTo( viewEntry1 ) );
    Assert.assertThat( bean.getId(), Matchers.not( DaoJonesMatchers.isMappedTo( viewEntry2 ) ) );
  }

  @Test
  public void testReadAllBeansFromViewWithDocumentMapped() throws DataAccessException {
    final Collection<MixedBeanFromViewWithDocumentMappedBean> result = controller
            .readAllBeansFromView( MixedBeanFromViewWithDocumentMappedBean.class );
    Assert.assertThat( result.size(), Matchers.is( Matchers.equalTo( 2 ) ) );
    for ( final MixedBeanFromViewWithDocumentMappedBean viewBean : result ) {
      if ( "Entry 1".equals( viewBean.getName() ) ) {
        Assert.assertThat( viewBean.getMixedBean().getId(), DaoJonesMatchers.isMappedTo( entry1 ) );
      }
    }
    final MixedBeanFromViewWithDocumentMappedBean bean = result.iterator().next();
    Assert.assertThat( bean.getId(), DaoJonesMatchers.isMappedTo( viewEntry1 ) );
    Assert.assertThat( bean.getId(), Matchers.not( DaoJonesMatchers.isMappedTo( viewEntry2 ) ) );
  }

  @Test
  public void testReadSingleBeanFromView() throws DataAccessException {
    final MixedBeanFromView bean = controller.readAllBeansFromView( MixedBeanFromView.class ).iterator().next();
    final MixedBeanFromView bean2 = controller.readSingleBeanFromView( bean.getId() );
    Assert.assertThat( bean2, Matchers.is( Matchers.notNullValue() ) );
    Assert.assertThat( bean2.getId(), Matchers.is( Matchers.equalTo( bean.getId() ) ) );
  }

  @Test
  public void testCreateBeans() throws DataAccessException {
    final Collection<MixedBean> beansBefore = controller.readAllBeans();
    controller.createBeans();
    final Collection<MixedBean> beansAfter = controller.readAllBeans();
    Assert.assertThat( beansAfter.size(), Matchers.is( Matchers.greaterThan( beansBefore.size() ) ) );
  }

  @Test
  public void testDaoJonesMatcher() throws DataAccessException, ConfigurationException { // not a test of the controller
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    try {
      Assert.assertThat(
              DaoJonesMatchers.entry( entry1, con ),
              DaoJonesMatchers.hasField( "name" ).ofType( String.class )
                      .which( Matchers.is( Matchers.equalTo( "Entry 1" ) ) ) );
      Assert.assertThat( DaoJonesMatchers.entry( entry1, con ), Matchers.not( DaoJonesMatchers.hasField( "date" ) ) );
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
