package de.ars.daojones.runtime.ri;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.drivers.notes.types.Principal;
import de.ars.daojones.drivers.notes.types.User;
import de.ars.daojones.drivers.notes.xt.ComputeWithFormHandler;
import de.ars.daojones.drivers.notes.xt.NotesAPI;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterionBuilder;

public class MixedBeanController {

  private static final Logger logger = Logger.getLogger( MixedBeanController.class.getName() );

  private static final NotesEventHandler cwf = new ComputeWithFormHandler();
  static final String TEST_DOCUMENT_TITLE = "DaoJones Test Document";
  private final ConnectionProvider cp;

  public MixedBeanController( final ConnectionProvider cp ) throws DataAccessException {
    super();
    this.cp = cp;
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    if ( null == con ) {
      throw new DataAccessException( "No connection found for " + MixedBean.class.getName() + "!" );
    }
    try {
      NotesAPI.addNotesEventHandler( con, MixedBeanController.cwf );
    } finally {
      con.close();
    }
  }

  public void deleteAllGenerated() throws DataAccessException {
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    try {
      con.delete( Query.create().only( SearchCriterionBuilder.field( MixedBean.GENERATED ).asBoolean().isTrue() ) );
    } finally {
      con.close();
    }
  }

  public void createBeans() throws DataAccessException {
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    try {
      NotesAPI.addNotesEventHandler( con, MixedBeanController.cwf );
      final MixedBean b1 = new MixedBean( MixedBeanController.TEST_DOCUMENT_TITLE );

      b1.setStringField( "This is the first document." );
      final User u1 = new User( "Ralf Zahn" );
      u1.setOrganization( "ARS GmbH" );
      b1.setAuthors( new Principal[] { u1 } );
      con.update( b1 );
    } finally {
      con.close();
    }
  }

  public void readUpdatedBean() throws DataAccessException {
    // To test bug with local Notes client on reading a document immediately after update
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    try {
      NotesAPI.addNotesEventHandler( con, MixedBeanController.cwf );
      final SearchResult<MixedBean> generatedBeans = con.findAll( Query.create().only(
              SearchCriterionBuilder.field( MixedBean.GENERATED ).asBoolean().isTrue() ) );
      final MixedBean b1 = generatedBeans.iterator().next();
      generatedBeans.close();
      final String testValue = "Das ist der lokale Test um " + System.currentTimeMillis();
      b1.setStringField( testValue );
      con.update( b1 );
      final Identificator id = b1.getId();
      final MixedBean b2 = con.findById( id );
      MixedBeanController.logger.log( Level.INFO,
              "Erneutes Lesen nach Update per findAll:"
                      + ( testValue.equals( b2.getStringField() ) ? "korrekt" : "fehlerhaft!" ) );
      for ( final MixedBean bean : con.findAll( Query.create() ).getAsList() ) {
        if ( id.equals( bean.getId() ) ) {
          MixedBeanController.logger.log( Level.INFO,
                  "Erneutes Lesen nach Update per findAll:"
                          + ( testValue.equals( bean.getStringField() ) ? "korrekt" : "fehlerhaft!" ) );
        }
      }

    } finally {
      con.close();
    }
  }

  public Collection<MixedBean> readAllBeans() throws DataAccessException {
    final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
    try {
      final SearchResult<MixedBean> result = con.findAll( Query.create() );
      try {
        return result.getAsList();
      } finally {
        result.close();
      }
    } finally {
      con.close();
    }
  }

  public <T> Collection<T> readAllBeansFromView( final Class<T> beanType ) throws DataAccessException {
    final Connection<T> con = cp.getConnection( beanType );
    try {
      final SearchResult<T> result = con.findAll( Query.create() );
      try {
        return result.getAsList();
      } finally {
        result.close();
      }
    } finally {
      con.close();
    }
  }

  public Collection<MixedBeanFromView> readAllBeansFromViewByName( final String name ) throws DataAccessException {
    final Connection<MixedBeanFromView> con = cp.getConnection( MixedBeanFromView.class );
    try {
      final SearchResult<MixedBeanFromView> result = con.findAll( Query.create().only(
              SearchCriterionBuilder.field( MixedBeanFromView.NAME ).asCaseInsensitiveString().contains( name ) ) );
      try {
        return result.getAsList();
      } finally {
        result.close();
      }
    } finally {
      con.close();
    }
  }

  public MixedBeanFromView readSingleBeanFromView( final Object id ) throws DataAccessException {
    final Connection<MixedBeanFromView> con = cp.getConnection( MixedBeanFromView.class );
    try {
      return con.findById( id );
    } finally {
      con.close();
    }
  }

  public void update( final MixedBeanFromView... beans ) throws DataAccessException {
    final Connection<MixedBeanFromView> con = cp.getConnection( MixedBeanFromView.class );
    try {
      con.update( beans );
    } finally {
      con.close();
    }
  }

  private static final String NL = System.getProperty( "line.separator" );

  public String printNotesAPI() throws DataAccessException {
    try {
      final StringBuffer result = new StringBuffer();
      final Connection<MixedBean> con = cp.getConnection( MixedBean.class );
      final MixedBean bean = con.find();
      final String app = con.getModel().getId().getApplicationId();
      try {
        result.append( MixedBeanController.NL )
                .append( "===============================================================================" )
                .append( MixedBeanController.NL )
                .append( "MixedBean Example:" )
                .append( MixedBeanController.NL )
                .append( "===============================================================================" )
                .append( MixedBeanController.NL )
                // SESSION
                .append( "Session:\t\t" )
                .append( NotesAPI.getSession( con ) )
                .append( MixedBeanController.NL )
                // DATABASE
                .append( "Database:\t\t" )
                .append( NotesAPI.getDatabase( con ) )
                .append( MixedBeanController.NL )
                // DOCUMENT
                .append( "Document from bean:\t" )
                .append( NotesAPI.getDocument( con, bean ) )
                .append( MixedBeanController.NL )
                // BEAN FROM DOCUMENT
                .append( "Bean from document:\t" )
                .append( NotesAPI.createBean( con, NotesAPI.getDocument( con, bean ) ) )
                .append( MixedBeanController.NL )
                // DOCUMENT
                .append( "Document from identificator:\t" )
                .append( NotesAPI.getDocument( bean.getId(), app, NotesAPI.getSession( con ) ) )
                .append( MixedBeanController.NL )

                // FINISH
                .append( "===============================================================================" );
        return result.toString();
      } finally {
        con.close();
      }
    } catch ( final Exception e ) {
      throw new DataAccessException( e );
    }
  }
}
