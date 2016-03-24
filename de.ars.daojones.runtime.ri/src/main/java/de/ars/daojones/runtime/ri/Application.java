package de.ars.daojones.runtime.ri;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.drivers.notes.types.Principal;
import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

/**
 * Reference implementation to test the runtime and the Notes Driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class Application {

  private static final Logger logger = Logger.getLogger( Application.class.getName() );

  /**
   * The id of the application.
   */
  public static final String ID = "daojones-ri";

  /**
   * Main Method.
   * 
   * @param args
   *          command line arguments
   * @throws ConfigurationException
   * @throws DataAccessException
   */
  public static void main( final String[] args ) throws ConfigurationException, DataAccessException {
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    dcf.setConfigurationSources( // to configure:
            new AnnotationBeanConfigurationSource( Application.ID ), // scan annotations for bean model
            // scan annotations for bean model
            new XmlConnectionConfigurationSource( Application.ID, Application.class
                    .getResource( "/META-INF/daojones-connections.xml" ) ) );
    final DaoJonesContext ctx = dcf.createContext();
    try {
      final ConnectionProvider cp = ctx.getApplication( Application.ID );
      final MixedBeanController controller = new MixedBeanController( cp );
      // [MIXED] Access Public Notes API
      Application.printNotesAPI( controller );
      // [VIEW] Execute FTSearch on view
      Application.readAllBeansFromViewByName( controller );
      // [VIEW] Read all beans from view (without FTSearch)
      Application.readAllBeansFromView( controller );
      // [VIEW] Find view entry by document id
      Application.readSingleBeanByIdFromView( controller );
      // [VIEW] Update field
      Application.updateBeansFromViewDateField( controller );
      // [TABLE] Read documents by query incl. generated
      Application.readAllBeans( controller );
      // [TABLE] Delete documents by query
      Application.deleteAllGenerated( controller );
      // [TABLE] Read documents by query again excl. generated
      Application.readAllBeans( controller );
      // [TABLE] Create/Update a document
      Application.createBeans( controller );
      // [TABLE] Read a document after update
      Application.readUpdatedBean( controller );
    } finally {
      ctx.close();
    }
    Application.logger.log( Level.INFO, "Well done!" );

  }

  private static void deleteAllGenerated( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    c.deleteAllGenerated();
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Reading all beans done within " + ( t2 - t1 ) + "ms." );
  }

  private static void createBeans( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    c.createBeans();
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Saving all beans done within " + ( t2 - t1 ) + "ms." );
  }

  private static void readUpdatedBean( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    c.readUpdatedBean();
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Updating bean in 2 steps done within " + ( t2 - t1 ) + "ms." );
  }

  private static void printNotesAPI( final MixedBeanController c ) throws DataAccessException {
    Application.logger.info( c.printNotesAPI() );
  }

  private static void readAllBeans( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    final Collection<MixedBean> result = c.readAllBeans();
    for ( final MixedBean bean : result ) {
      Application.logger.log( Level.INFO, String.valueOf( bean ) );
      Application.logger.log( Level.INFO, "Authors: " );
      for ( final Principal p : bean.getAuthors() ) {
        Application.logger.log( Level.INFO, " - " + p.getValue() );
      }
    }
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Reading all beans done within " + ( t2 - t1 ) + "ms." );
  }

  private static void readAllBeansFromView( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    final Collection<MixedBeanFromViewWithDocumentMappedBean> result = c
            .readAllBeansFromView( MixedBeanFromViewWithDocumentMappedBean.class );
    for ( final MixedBeanFromViewWithDocumentMappedBean bean : result ) {
      Application.logger.log( Level.INFO, bean.getName() + " - " + bean.getLastModified() + " (" + bean.getMixedBean()
              + ")" );
    }
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Reading all beans from view done within " + ( t2 - t1 ) + "ms." );
  }

  private static void readAllBeansFromViewByName( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    final Collection<MixedBeanFromView> result = c.readAllBeansFromViewByName( MixedBeanController.TEST_DOCUMENT_TITLE );
    for ( final MixedBeanFromView bean : result ) {
      Application.logger.log( Level.INFO, bean.getName() + " - " + bean.getLastModified() );
    }
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Reading all beans from view by name done within " + ( t2 - t1 ) + "ms." );
  }

  private static final int DAY = 1000 * 60 * 60 * 24;

  private static void updateBeansFromViewDateField( final MixedBeanController c ) throws DataAccessException {
    final long t1 = System.currentTimeMillis();
    final Collection<MixedBeanFromView> result = c.readAllBeansFromView( MixedBeanFromView.class );
    for ( final MixedBeanFromView bean : result ) {
      // set date to 1 day before
      bean.setDatefield( new Date( System.currentTimeMillis() - Application.DAY ) );
      Application.logger.log( Level.INFO, bean.getName() + " - " + bean.getLastModified() );
    }
    c.update( result.toArray( new MixedBeanFromView[result.size()] ) );
    final long t2 = System.currentTimeMillis();
    Application.logger.log( Level.INFO, "Updating all beans from view done within " + ( t2 - t1 ) + "ms." );
  }

  private static void readSingleBeanByIdFromView( final MixedBeanController c ) throws DataAccessException {
    final Collection<MixedBeanFromView> result = c.readAllBeansFromView( MixedBeanFromView.class );
    if ( result.size() > 0 ) {
      final MixedBeanFromView bean = result.iterator().next();
      final long t1 = System.currentTimeMillis();
      final MixedBeanFromView bean2 = c.readSingleBeanFromView( bean.getId() );
      final long t2 = System.currentTimeMillis();
      Application.logger.log( Level.INFO, "Reading single bean from view done within " + ( t2 - t1 ) + "ms.\n" + bean );
      Application.logger.log( Level.INFO, bean.getName() + " == " + bean2.getName() );
    }
  }

}
