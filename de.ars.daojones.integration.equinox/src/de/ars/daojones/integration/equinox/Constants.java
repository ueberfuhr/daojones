package de.ars.daojones.integration.equinox;

import org.eclipse.core.runtime.QualifiedName;

import de.ars.daojones.internal.integration.equinox.Activator;
import de.ars.daojones.internal.integration.equinox.ConnectionsFileDescriber;

/**
 * An interface holding constants concerning connections and connection files.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public interface Constants {

  /**
   * The name of the persistent property that a connection configuration file
   * may have.
   */
  public static final QualifiedName FILE_IDENTIFIER_KEY = new QualifiedName( Activator.PLUGIN_ID, "connectionfile" );
  /**
   * The value of the persistent property that a connection configuration file
   * may have.
   */
  public static final String FILE_IDENTIFIER_VALUE = "true";

  /**
   * The id of the XML connection configuration content type.
   */
  public static final String CONNECTIONSFILE_CONTENTTYPE = ConnectionsFileDescriber.CONTENT_TYPE;

}
