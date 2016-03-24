package de.ars.daojones.runtime.search;

import java.util.Collection;
import java.util.TreeSet;

import de.ars.daojones.runtime.Dao;

/**
 * Returns a collection of {@link SearchResult}s.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the DaoJones bean type.
 */
public class SearchResultCollection<T extends Dao> {

	private final SearchEngine<T> searchEngine;
	private final Collection<SearchResult<T>> searchResults;

	/**
	 * Creates an instance.
	 * 
	 * @param searchEngine
	 *            the {@link SearchEngine}
	 * @param searchResults
	 *            the collection of {@link SearchResult}s
	 */
	public SearchResultCollection(SearchEngine<T> searchEngine,
			Collection<SearchResult<T>> searchResults) {
		super();
		this.searchEngine = searchEngine;
		this.searchResults = new TreeSet<SearchResult<T>>(searchResults);
	}

	/**
	 * Returns the {@link SearchEngine}.
	 * 
	 * @return the {@link SearchEngine}
	 */
	public SearchEngine<T> getSearchEngine() {
		return searchEngine;
	}

	/**
	 * Returns the collection of {@link SearchResult}s.
	 * 
	 * @return the collection of {@link SearchResult}s
	 */
	public Collection<SearchResult<T>> getSearchResults() {
		return searchResults;
	}

}
