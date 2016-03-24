package de.ars.daojones.connections;

import java.io.Serializable;

import de.ars.daojones.connections.model.ICredential;

/**
 * Transfer object for databse connection data.
 *
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionData implements Serializable {

	private static final long serialVersionUID = -7790337925964185096L;
	private final String databaseName;
	private final String host;
	private final ICredential credential;
	private final int maxResults;

	/**
	 * Creates an instance.
	 * @param databaseName the file name or path of the database.
	 * @param host the host of the database or null, if local
	 * @param credential the credential to access the database or null, if the database can be accessed unsecured
	 * @param maxResults the maximum count of results that this database should return for search queries
	 */
	public ConnectionData(final String databaseName, final String host, final ICredential credential, final int maxResults) {
		super();
		this.databaseName = databaseName;
		this.host = host;
		this.credential = credential;
		this.maxResults = maxResults;
	}
	/**
	 * Returns the file name or path of the database.
	 * @return the file name or path of the database
	 */
	public String getDatabaseName() {
		return databaseName;
	}
	/**
	 * Returns the host of the database.
	 * @return the host of the database or null, if local
	 */
	public String getHost() {
		return host;
	}
	/**
	 * Returns the credential to access the database.
	 * @return the credential to access the database or null, if the database can be accessed unsecured
	 */
	public ICredential getCredential() {
		return credential;
	}
	/**
	 * Returns the maximum count of results that this database should return for search queries.
	 * If the count of results is not limited, this method returns {@link Integer#MAX_VALUE}.
	 * @return the maximum count of results that this database should return for search queries
	 */
	public int getMaxResults() {
		return maxResults <=0 ? Integer.MAX_VALUE : maxResults;
	}
	
	/**
	 * Generates the hashcode value based on host, database, username and maximum count of results.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((databaseName == null) ? 0 : databaseName.hashCode());
		result = PRIME * result + ((host == null) ? 0 : host.hashCode());
		result = PRIME * result + maxResults;
		result = PRIME * result + ((credential == null) ? 0 : credential.hashCode());
		return result;
	}
	
	/**
	 * Compares two objects based on host, database, username and maximum count of results.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ConnectionData other = (ConnectionData) obj;
		if (databaseName == null) {
			if (other.databaseName != null)
				return false;
		} else if (!databaseName.equals(other.databaseName))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (maxResults != other.maxResults)
			return false;
		if (credential == null) {
			if (other.credential != null)
				return false;
		} else if (!credential.equals(other.credential))
			return false;
		return true;
	}
	
}
