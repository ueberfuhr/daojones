package de.ars.daojones.internal.drivers.notes;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lotus.domino.NotesException;
import lotus.domino.View;

/**
 * A transfer object containing information for navigation through a view to
 * fetch information.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ViewMetadata implements Serializable {

  private static final long serialVersionUID = 5706378072490507875L;
  private final String viewName;
  private final Map<String, ViewColumnMetadata> columns = new HashMap<String, ViewColumnMetadata>();
  private final Map<String, Integer> columnIndices = new HashMap<String, Integer>();

  @SuppressWarnings( "unchecked" )
  public ViewMetadata( View view ) throws NotesException {
    this( view.getName(), ( String[] ) view.getColumnNames().toArray( new String[view.getColumnNames().size()] ),
            NotesViewHelper.getColumns( view ) );
  }

  public ViewMetadata( String viewName, String[] columnNames, ViewColumnMetadata... columns ) {
    super();
    this.viewName = viewName;
    int index = 0;
    for ( ViewColumnMetadata col : columns ) {
      final String key = index > columnNames.length - 1 ? col.getTitle() : columnNames[index];
      this.columns.put( key, col );
      this.columnIndices.put( key, index );
      index++;
    }
  }

  public String getViewName() {
    return viewName;
  }

  public String[] getColumnNames() {
    final Collection<String> result = columns.keySet();
    return result.toArray( new String[result.size()] );
  }

  public Integer getColumnIndex( String name ) {
    return this.columnIndices.get( name );
  }

  public ViewColumnMetadata getColumn( String name ) {
    return this.columns.get( name );
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( viewName == null ) ? 0 : viewName.hashCode() );
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    ViewMetadata other = ( ViewMetadata ) obj;
    if ( viewName == null ) {
      if ( other.viewName != null )
        return false;
    } else if ( !viewName.equals( other.viewName ) )
      return false;
    return true;
  }

}
