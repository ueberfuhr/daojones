package de.ars.daojones.internal.drivers.notes;

import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import lotus.domino.ViewNavigator;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.spi.database.ViewHelper;

public abstract class NotesViewHelper {

  public static View getView( final ViewEntry entry ) throws NotesException {
    final Object base = entry.getParent();
    if ( base instanceof View ) {
      return ( View ) base;
    } else if ( base instanceof ViewNavigator ) {
      return ( ( ViewNavigator ) base ).getParentView();
    } else if ( base instanceof ViewEntryCollection ) {
      return ( ( ViewEntryCollection ) base ).getParent();
    } else {
      return null;
    }
  }

  @SuppressWarnings( "unchecked" )
  public static ViewColumn findColumn( final View view, final String name ) throws NotesException,
          ConfigurationException {
    try {
      Integer index = ViewHelper.getColumnIndex( name );
      if ( null == index ) {
        index = view.getColumnNames().indexOf( name );
      }
      if ( null != index && index >= 0 ) {
        return view.getColumn( index + 1 );
      } else {
        // Last chance: find per programmatic item name
        for ( final ViewColumn column : ( Vector<ViewColumn> ) view.getColumns() ) {
          if ( name.equals( column.getItemName() ) ) {
            return column;
          }
        }
        return null;
      }
    } catch ( final ParseException e ) {
      throw new ConfigurationException( e );
    }
  }

  public static ViewEntry findViewEntry( final View view, final Document doc ) throws NotesException {
    final ViewNavigator viewNav = view.createViewNavFrom( doc, 0 );
    try {
      if ( viewNav.getCount() > 0 ) {
        final ViewEntry result = viewNav.getFirst();
        if ( result.isDocument() && doc.getUniversalID().equals( result.getUniversalID() ) ) {
          // all right
          return result;

        }
      }
      return null;
    } finally {
      // Do not reccycle - the view cannot be found otherwise
      // viewNav.recycle();
    }
  }

  /**
   * Reads the metadata from the view.
   * 
   * @param view
   *          the view
   * @return the {@link ViewColumnMetadata} objects
   * @throws NotesException
   */
  @SuppressWarnings( "unchecked" )
  public static ViewColumnMetadata[] getColumns( final View view ) throws NotesException {
    final Collection<ViewColumnMetadata> result = new LinkedList<ViewColumnMetadata>();
    for ( final ViewColumn col : ( Collection<ViewColumn> ) view.getColumns() ) {
      result.add( new ViewColumnMetadata( col ) );
    }
    return result.toArray( new ViewColumnMetadata[result.size()] );
  }

}
