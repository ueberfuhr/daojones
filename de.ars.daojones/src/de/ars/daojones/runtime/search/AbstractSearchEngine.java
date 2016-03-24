package de.ars.daojones.runtime.search;

import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.ConnectionFactory;
import de.ars.daojones.runtime.Dao;

/**
 * An abstract implementation of {@link SearchEngine} that can be used for
 * subclasses.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the DaoJones bean type.
 */
public abstract class AbstractSearchEngine<T extends Dao> extends
		ConnectionFactory implements SearchEngine<T> {

	private final Map<String, String> configParameters = new HashMap<String, String>();
	private String name;

	/**
	 * @see de.ars.daojones.runtime.search.SearchEngine#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the search engine.
	 * 
	 * @param name
	 *            the name of the search engine
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the configuration parameters using a map. This does not remove any
	 * existing parameters, but overwrites if existing in the parameter map.
	 * 
	 * @param configParameters
	 *            the configuration parameters
	 * 
	 * @see de.ars.daojones.runtime.search.SearchEngine#setConfigParameter(java.lang.String,
	 *      java.lang.String)
	 */
	public void setConfigParameters(Map<String, String> configParameters) {
		this.configParameters.putAll(configParameters);
	}

	/**
	 * @see de.ars.daojones.runtime.search.SearchEngine#setConfigParameter(java.lang.String,
	 *      java.lang.String)
	 */
	public void setConfigParameter(String name, String value) {
		this.configParameters.put(name, value);
	}

	/**
	 * Removes all configuration parameters.
	 */
	protected void clearConfigParameters() {
		this.configParameters.clear();
	}

	/**
	 * Returns a copy of the internal map containing the configuration
	 * parameters.
	 * 
	 * @return the configuration parameters
	 */
	protected Map<String, String> getConfigParameters() {
		return new HashMap<String, String>(this.configParameters);
	}

	/**
	 * Returns the value of a configuration parameter.
	 * 
	 * @param name
	 *            the name of the configuration parameter
	 * @return the value of the configuration parameter
	 */
	protected String getConfigParameters(String name) {
		return this.configParameters.get(name);
	}
}
