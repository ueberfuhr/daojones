package de.ars.daojones.internal.runtime.ri.web;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.ri.MixedBeanController;
import de.ars.daojones.runtime.ri.MixedBeanFromView;

@ManagedBean( name = "sampleController" )
@ApplicationScoped
public class SampleFaceletController {

  private static final Logger logger = Logger.getLogger( SampleFaceletController.class.getName() );

  @Inject
  private ConnectionProvider connectionProvider;
  private MixedBeanController mbc;

  @PostConstruct
  public void init() {
    try {
      mbc = new MixedBeanController( connectionProvider );
    } catch ( final DataAccessException e ) {
      SampleFaceletController.logger.log( Level.SEVERE, "Error during facelet initialization!", e );
    }
  }

  // filter text
  private String filter;

  public String getFilter() {
    return filter;
  }

  public void setFilter( final String filter ) {
    this.filter = filter;
  }

  // prevent searching 
  private String lastSearchFilter;
  private Collection<MixedBeanFromView> lastSearchResult;

  private static boolean isEmpty( final String text ) {
    return null == text || text.trim().length() == 0;
  }

  private Collection<MixedBeanFromView> getLastSearchResult( final String currentfilter ) {
    if ( SampleFaceletController.isEmpty( lastSearchFilter ) && SampleFaceletController.isEmpty( currentfilter )
            || null != lastSearchFilter && lastSearchFilter.trim().equalsIgnoreCase( currentfilter ) ) {
      return lastSearchResult;
    } else {
      return null;
    }
  }

  // database access
  public Collection<MixedBeanFromView> getBeans() throws DataAccessException {
    Collection<MixedBeanFromView> result = getLastSearchResult( filter );
    if ( null == result && null != mbc ) {
      if ( SampleFaceletController.isEmpty( filter ) ) {
        SampleFaceletController.logger.log( Level.INFO, "Reading all beans from database..." );
        result = mbc.readAllBeansFromView( MixedBeanFromView.class );
        SampleFaceletController.logger.log( Level.INFO, "Done!" );
      } else {
        SampleFaceletController.logger.log( Level.INFO, "Searching beans for \"" + filter.trim() + "\"!" );
        result = mbc.readAllBeansFromViewByName( filter.trim() );
        SampleFaceletController.logger.log( Level.INFO, "Done!" );
      }
      lastSearchResult = result;
      lastSearchFilter = filter;
    }
    return result;
  }
}
