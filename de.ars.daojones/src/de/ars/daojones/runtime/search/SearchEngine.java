package de.ars.daojones.runtime.search;

import java.util.Map;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.runtime.Dao;

/**
 * A search engine that searches the database by using special search keywords.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the DaoJones bean type.
 */
public interface SearchEngine<T extends Dao> {

	/**
	 * Returns the type of the DaoJones bean that can be searched.
	 * 
	 * @return the type of the DaoJones bean that can be searched
	 */
	public Class<T> getSearchType();
	
	/**
	 * Returns the name of the search engine.
	 * This can be used for the UI.
	 * @return the name of the search engine
	 */
	public String getName();

	/**
	 * Searches the database for a given text.
	 * 
	 * @param options
	 *            the options that influence the search behaviour
	 * @param text
	 *            the text elements to search
	 * @return the collection of search results
	 * @throws SearchException
	 */
	public SearchResultCollection<T> search(Map<String, Object> options, 
			String... text) throws SearchException;

	/**
	 * Sets a search parameter that is used for each search.
	 * 
	 * @param name
	 *            the parameter's name
	 * @param value
	 *            the parameter's value
	 */
	public void setConfigParameter(String name, String value);

	/**
	 * Returns the {@link ApplicationContext}.
	 * 
	 * @return the {@link ApplicationContext}
	 */
	public ApplicationContext getContext();

	/**
	 * Sets the {@link ApplicationContext}.
	 * 
	 * @param ctx
	 *            the {@link ApplicationContext}
	 */
	public void setContext(ApplicationContext ctx);

}
