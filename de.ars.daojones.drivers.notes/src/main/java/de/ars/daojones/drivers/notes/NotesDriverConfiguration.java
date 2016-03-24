package de.ars.daojones.drivers.notes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import de.ars.daojones.internal.drivers.notes.utilities.Messages;

/**
 * A class that provides the system-wide configuration of the driver. The
 * configuration is read from the classpath resource &quot;{@value #CONFIG_FILE}
 * &quot;.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class NotesDriverConfiguration {

  /**
   * The id of the connection factory model.
   */
  public static final String DRIVER_ID = "de.ars.daojones.drivers.notes"; //$NON-NLS-1$

  /**
   * The name of the soft deletion property.
   * 
   * @see #DELETE_SOFT
   */
  public static final String PROPERTY_DELETE_SOFT = "daojones.notes.delete.soft"; //$NON-NLS-1$

  /**
   * The name of the force delete property.
   * 
   * @see #DELETE_FORCE
   */
  public static final String PROPERTY_DELETE_FORCE = "daojones.notes.delete.force"; //$NON-NLS-1$

  /**
   * The name of the mark read property.
   * 
   * @see #SAVE_MARK_READ
   */
  public static final String PROPERTY_SAVE_MARKREAD = "daojones.notes.save.markread"; //$NON-NLS-1$

  /**
   * The name of the create response property.
   * 
   * @see #SAVE_CREATE_RESPONSE
   */
  public static final String PROPERTY_SAVE_CREATERESPONSE = "daojones.notes.save.createresponse"; //$NON-NLS-1$

  /**
   * The name of the force property.
   * 
   * @see #SAVE_FORCE
   */
  public static final String PROPERTY_SAVE_FORCE = "daojones.notes.save.force"; //$NON-NLS-1$

  /**
   * The name of the session scope property.
   * 
   * @see #SESSION_SCOPE
   */
  public static final String PROPERTY_SESSION_SCOPE = "daojones.notes.session.scope"; //$NON-NLS-1$

  private static final Messages bundle = Messages.create( "Configuration" ); //$NON-NLS-1$

  /**
   * The scope of a Notes session.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static enum SessionScope {
    /**
     * A single Notes Session (per host and user) for the whole application.<br/>
     * <i>This is the default.</i>
     */
    APPLICATION,
    /**
     * One Notes Session (per host and user) per Thread.
     */
    THREAD;
  }

  /**
   * The scope of a Notes Session.
   */
  public static final SessionScope SESSION_SCOPE;

  /**
   * If <tt>true</tt>, the document is saved even if someone else edits and
   * saves the document while the script is running. The last version of the
   * document that was saved wins; the earlier version is discarded.<br/>
   * If <tt>false</tt>, and someone else edits the document while the script is
   * running, the {@link #SAVE_CREATE_RESPONSE} argument determines what
   * happens.
   */
  public static final boolean SAVE_FORCE;
  /**
   * If <tt>true</tt>, the current document becomes a response to the original
   * document (this is what the replicator does when there's a replication
   * conflict).<br/>
   * If <tt>false</tt>, the save is canceled. If the {@link #SAVE_FORCE}
   * parameter is <tt>true</tt>, this parameter has no effect.
   */
  public static final boolean SAVE_CREATE_RESPONSE;
  /**
   * If <tt>true</tt>, the document is marked as read on behalf of the current
   * user ID.<br/>
   * If <tt>false</tt>, the document is not marked as read.<br/>
   * <br/>
   * <b>Please note:</b> If the database does not track unread marks, all
   * documents are considered read, and this parameter has no effect.
   */
  public static final boolean SAVE_MARK_READ;

  /**
   * If <tt>true</tt>, the document is deleted even if another user modifies the
   * document after the script opens it.<br/>
   * If <tt>false</tt>, the document is not deleted if another user modifies it.
   */
  public static final boolean DELETE_FORCE;

  /**
   * If <tt>true</tt>, documents are deleted permanently from the database,
   * doing a soft deletion if soft deletions are enabled.<br/>
   * If <tt>false</tt> documents are deleted permanently from the database,
   * doing a hard deletion even if soft deletions are enabled.
   */
  public static final boolean DELETE_SOFT;

  /**
   * The classpath resource that contains the configuration.
   */
  public static final String CONFIG_FILE = "/daojones-notes.properties";

  static {
    final Properties p = new Properties();
    try {
      final InputStream in = NotesDriverConfiguration.class.getResourceAsStream( NotesDriverConfiguration.CONFIG_FILE );
      try {
        p.load( in );
      } finally {
        in.close();
      }
    } catch ( final IOException e ) {
      NotesDriverConfiguration.bundle.log( Level.WARNING, e, "error.readconfig", NotesDriverConfiguration.CONFIG_FILE ); //$NON-NLS-1$
    }
    // Read properties
    SESSION_SCOPE = SessionScope.valueOf( NotesDriverConfiguration.getProperty( p,
            NotesDriverConfiguration.PROPERTY_SESSION_SCOPE, SessionScope.APPLICATION.name() ).toUpperCase() );
    SAVE_FORCE = Boolean.valueOf( NotesDriverConfiguration.getProperty( p,
            NotesDriverConfiguration.PROPERTY_SAVE_FORCE, "true" ) ); //$NON-NLS-1$
    SAVE_CREATE_RESPONSE = Boolean.valueOf( NotesDriverConfiguration.getProperty( p,
            NotesDriverConfiguration.PROPERTY_SAVE_CREATERESPONSE, "false" ) );
    SAVE_MARK_READ = Boolean.valueOf( NotesDriverConfiguration.getProperty( p,
            NotesDriverConfiguration.PROPERTY_SAVE_MARKREAD, "false" ) ); //$NON-NLS-1$
    DELETE_FORCE = Boolean.valueOf( NotesDriverConfiguration.getProperty( p,
            NotesDriverConfiguration.PROPERTY_DELETE_FORCE, "true" ) ); //$NON-NLS-1$
    DELETE_SOFT = Boolean.valueOf( NotesDriverConfiguration.getProperty( p,
            NotesDriverConfiguration.PROPERTY_DELETE_SOFT, "false" ) ); //$NON-NLS-1$

  }

  private static String getProperty( final Properties p, final String name, final String defaultValue ) {
    final String value = System.getProperty( name );
    return null != value ? value : p.getProperty( name, defaultValue );
  }

  private NotesDriverConfiguration() {
    super();
  }

  /**
   * The name of the property that sets the field type.
   */
  public static final String MODEL_PROPERTY_FIELD_TYPE = de.ars.daojones.runtime.beans.fields.Properties.FIELD_TYPE_PROPERTY;
  /**
   * The value of the property that sets the field type to AUTHORS.
   */
  public static final String MODEL_PROPERTY_AUTHORS = "authors"; //$NON-NLS-1$
  /**
   * The value of the property that sets the field type to READERS.
   */
  public static final String MODEL_PROPERTY_READERS = "readers"; //$NON-NLS-1$
  /**
   * The value of the property that sets the field type to RICHTEXT.
   */
  public static final String MODEL_PROPERTY_RICHTEXT = "richtext"; //$NON-NLS-1$
  /**
   * The value of the property that sets the field type to MIME ENTITY.
   */
  public static final String MODEL_PROPERTY_MIME_ENTITY = "mime-entity"; //$NON-NLS-1$
  /**
   * The name of the property that sets the type of MIME Entity.
   */
  public static final String MODEL_PROPERTY_MIME_ENTITY_TYPE = "mime-entity-type";
  /**
   * The value of the property that sets the field type to DOCUMENT MAPPED.
   */
  public static final String MODEL_PROPERTY_DOCUMENT_MAPPED = "document-mapped"; //$NON-NLS-1$

}
