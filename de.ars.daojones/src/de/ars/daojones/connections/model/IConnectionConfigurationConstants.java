package de.ars.daojones.connections.model;

/**
 * A set of constants that are used when using the XML I/O classes. 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConnectionConfigurationConstants {

	/**
	 * The XML namespace.
	 */
	public static final String XMLNS = "http://www.ars.de/daojones/connections/";
	/**
	 * The XML schema instance namespace.
	 */
	public static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	/**
	 * The name of the schema file.
	 */
	public static final String SCHEMA = "connections.xsd";

	/**
	 * The XML tag for the root containing the connections.
	 */
	public static final String CONFIGURATION_TAG = "configuration";
	
	/**
	 * The XML tag for a {@link Connection}.
	 */
	public static final String CONNECTION_TAG = "connection";
	/**
	 * The name of the &quot;name&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_NAME = "name";
	/**
	 * The name of the &quot;description&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_DESCRIPTION = "description";
	/**
	 * The name of the &quot;cached&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_CACHED = "cached";
	/**
	 * The name of the &quot;cacheExpiration&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_CACHEEXPIRATION = "cacheExpiration";
	/**
	 * The name of the &quot;factory&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_FACTORY = "factory";
	/**
	 * The name of the &quot;maxResults&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_MAXRESULTS = "maxResults";
	/**
	 * The name of the &quot;default&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_DEFAULT = "default";
	/**
	 * The name of the &quot;forClass&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_FORCLASS = "forClass";
	/**
	 * The name of the &quot;credential&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_CREDENTIAL = "credential";
	/**
	 * The name of the &quot;credentialref&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_CREDENTIALREF = "credentialref";
	/**
	 * The name of the &quot;connectionpool&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_CONNECTIONPOOL = "connectionpool";
	/**
	 * The name of the &quot;host&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_HOST = "host";
	/**
	 * The name of the &quot;database&quot; attribute/element of a {@link Connection}.
	 */
	public static final String CONNECTION_DATABASE = "database";

	/**
	 * The XML tag for an {@link ImportDeclaration}.
	 */
	public static final String IMPORT_TAG = "import";
	/**
	 * The name of the &quot;file&quot; attribute/element of an {@link ImportDeclaration}.
	 */
	public static final String IMPORT_FILE = "file";
	
	/**
	 * The XML tag for a {@link Credential}.
	 */
	public static final String CREDENTIAL_TAG = "credential";
	/**
	 * The name of the &quot;id&quot; attribute/element of a {@link Credential}.
	 */
	public static final String CREDENTIAL_ID = "id";

	/**
	 * The name of the &quot;type&quot; attribute/element of a {@link ConcreteCredential}.
	 */
	public static final String CONCRETECREDENTIAL_TYPE = "type";

	/**
	 * The name of the &quot;username&quot; attribute/element of a {@link UserPasswordCredential}.
	 */
	public static final String USERPASSWORDCREDENTIAL_USERNAME = "username";
	/**
	 * The name of the &quot;password&quot; attribute/element of a {@link UserPasswordCredential}.
	 */
	public static final String USERPASSWORDCREDENTIAL_PASSWORD = "password";

	/**
	 * The name of the &quot;minimum&quot; attribute/element of an interval.
	 */
	public static final String INTERVAL_MINIMUM = "minimum";
	/**
	 * The name of the &quot;maximum&quot; attribute/element of an interval.
	 */
	public static final String INTERVAL_MAXIMUM = "maximum";

}
