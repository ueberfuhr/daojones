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
    pcs.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    pcs.removePropertyChangeListener( listener );
  }

  public PathType getType() {
    return db.getType();
  }

  public void setType( final PathType type ) {
    final PathType oldValue = db.getType();
    db.setType( type );
    pcs.firePropertyChange( NotesDatabasePathBean.PROPERTY_TYPE, oldValue, type );
  }

  public String getAuthority() {
    return db.getAuthority();
  }

  public void setAuthority( final String authority ) {
    final String oldValue = db.getAuthority();
    db.setAuthority( authority );
    pcs.firePropertyChange( NotesDatabasePathBean.PROPERTY_AUTHORITY, oldValue, authority );
  }

  public String getDatabase() {
    return db.getDatabase();
  }

  public void setDatabase( final String database ) {
    final String oldValue = db.getDatabase();
    db.setDatabase( database );
    pcs.firePropertyChange( NotesDatabasePathBean.PROPERTY_DATABASE, oldValue, database );
  }

  @Override
  public int hashCode() {
    return db.hashCode();
  }

  @Override
  public boolean equals( final Object obj ) {
    return obj instanceof NotesDatabasePathBean && db.equals( ( ( NotesDatabasePathBean ) obj ).db );
  }

  @Override
  public String toString() {
    return db.toString();
  }

}
