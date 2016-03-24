package de.ars.daojones.internal.drivers.notes.integration.eclipse;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.drivers.notes.NotesDatabasePath.PathType;

class NotesDatabasePathBean {

  public static final String PROPERTY_TYPE = "type";
  public static final String PROPERTY_AUTHORITY = "authority";
  public static final String PROPERTY_DATABASE = "database";

  private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
  private final NotesDatabasePath db;

  public NotesDatabasePathBean( final NotesDatabasePath db ) {
    super();
    this.db = db;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    this.pcs.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    this.pcs.removePropertyChangeListener( listener );
  }

  public PathType getType() {
    return this.db.getType();
  }

  public void setType( final PathType type ) {
    final PathType oldValue = this.db.getType();
    this.db.setType( type );
    this.pcs.firePropertyChange( NotesDatabasePathBean.PROPERTY_TYPE, oldValue, type );
  }

  public String getAuthority() {
    return this.db.getAuthority();
  }

  public void setAuthority( final String authority ) {
    final String oldValue = this.db.getAuthority();
    this.db.setAuthority( authority );
    this.pcs.firePropertyChange( NotesDatabasePathBean.PROPERTY_AUTHORITY, oldValue, authority );
  }

  public String getDatabase() {
    return this.db.getDatabase();
  }

  public void setDatabase( final String database ) {
    final String oldValue = this.db.getDatabase();
    this.db.setDatabase( database );
    this.pcs.firePropertyChange( NotesDatabasePathBean.PROPERTY_DATABASE, oldValue, database );
  }

  @Override
  public int hashCode() {
    return this.db.hashCode();
  }

  @Override
  public boolean equals( final Object obj ) {
    return this.db.equals( obj );
  }

  @Override
  public String toString() {
    return this.db.toString();
  }

}
